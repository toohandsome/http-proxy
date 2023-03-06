package demo.spy;

import cn.hutool.core.util.RuntimeUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import demo.entity.MyClass;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MySpy implements ApplicationListener {

    private String mainPackage = "";
    private String mainClass = "";
    private String pid = "";
    private String sessionId = "";

    CloseableHttpClient httpClient = HttpClients.createDefault();

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            final String name = ((ApplicationReadyEvent) event).getSpringApplication().getMainApplicationClass().getName();
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            mainClass = name;
            System.out.println("pid:" + jvmName.split("@")[0] + " , " + name);
            mainPackage = name.substring(0, name.lastIndexOf("."));
            pid = jvmName.split("@")[0];
        }
    }

    @PostMapping("/tracking")
    public String tracking(@RequestBody MyClass myClass) {

        JSONObject json = new JSONObject();
        json.put("action", "async_exec");
        if ("watch".equals(myClass.getType())) {
            json.put("command", "watch " + myClass.getFullName() + " " + myClass.getFunName() + " '{params,returnObj,throwExp}' -x 3");
        } else if ("trace".equals(myClass.getType())) {
            json.put("command", "trace " + myClass.getFullName() + " " + myClass.getFunName());
        } else if ("stack".equals(myClass.getType())) {
            json.put("command", "stack " + myClass.getFullName() + " " + myClass.getFunName());
        }
        final JSONObject jsonObject = reqToArthas(json);
        return "false";
    }


    @PostMapping("/compileClass")
    public boolean compileClass(@RequestBody MyClass myClass) {
        String classLoaderHash = getClassLoad();
        String classPath = "";
        try {
            classPath = myClass.getClassName() + ".java";
            final Path path = Paths.get(classPath);
            final File file = new File(classPath);
            if (file.exists()) {
                file.delete();
            }
            Files.write(path, myClass.getSource().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            return mcClass(classPath, classLoaderHash, myClass.getFullName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean mcClass(String path, String classLoaderHash, String fullName) {
        JSONObject json = new JSONObject();
        json.put("action", "exec");
        json.put("command", "mc -c  " + classLoaderHash + " " + path);
        final JSONObject jsonObject = reqToArthas(json);

        if ("SUCCEEDED".equals(jsonObject.getString("state"))) {
            final JSONObject results1 = (JSONObject) jsonObject.getJSONObject("body").getJSONArray("results").get(0);
            String file = results1.getJSONArray("files").get(0) + "";
            return retransformClass(file, classLoaderHash, fullName);
        }
        return false;

    }

    public boolean retransformClass(String file, String classLoaderHash, String fullName) {
        JSONObject json = new JSONObject();
        json.put("action", "exec");
        file = file.replaceAll("\\\\", "/");
        String command = "retransform -c  " + classLoaderHash + " " + file;
        json.put("command", command);
        final JSONObject jsonObject = reqToArthas(json);
        final JSONObject results1 = (JSONObject) jsonObject.getJSONObject("body").getJSONArray("results").get(0);
        return fullName.equals(results1.getJSONArray("retransformClasses").get(0) + "");

    }


    public String getClassLoad() {
        JSONObject json = new JSONObject();
        json.put("action", "exec");
        json.put("command", "sc -d  " + mainClass);
        final JSONObject jsonObject = reqToArthas(json);
        final JSONObject results1 = (JSONObject) jsonObject.getJSONObject("body").getJSONArray("results").get(0);
        return results1.getJSONObject("classInfo").getString("classLoaderHash");
    }

    @GetMapping("/jad")
    public JSONObject jad(String className) {
        JSONObject json = new JSONObject();
        json.put("action", "exec");
        json.put("command", "jad " + className + " --source-only");
        return reqToArthas(json);
    }

    @GetMapping("/getMainPackageTree")
    public List<DirTreeVo> getMainPackage() {

        if (checkState()) {
            final JSONObject packageTree = getPackageTree();
            final JSONObject body = packageTree.getJSONObject("body");
            final JSONArray results = body.getJSONArray("results");
            List<String> allClass = new ArrayList();
            for (Object result : results) {
                JSONObject result1 = (JSONObject) result;
                if (result1.containsKey("segment")) {
                    allClass.addAll(result1.getList("classNames", String.class));
                }
            }
            final List<DirTreeVo> packageTree1 = getPackageTree(allClass);

            return packageTree1;
        } else {
            startArthas();
        }
        return new ArrayList<>();
    }

    @GetMapping("/checkState")
    public boolean checkState() {

        boolean startSuccess = false;

        for (int i = 0; i < 10; i++) {
            JSONObject json = new JSONObject();
            json.put("action", "exec");
            json.put("command", "version");
            JSONObject jsonObject = reqToArthas(json);
            if (jsonObject != null) {
                startSuccess = true;
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }

        }
        if (startSuccess) {
            return true;
        }
        return false;

    }

    public JSONObject getPackageTree() {


        JSONObject json = new JSONObject();
        json.put("action", "exec");
        json.put("command", "sc " + mainPackage + ".*");
        return reqToArthas(json);

    }

    public JSONObject reqToArthas(JSONObject json) {
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8563/api");
        httpPost.setHeader("Connection", "close");
        httpPost.setEntity(new StringEntity(json.toString(), "UTF-8"));
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);

            String ret = "";
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                // 获取返回的信息
                ret = EntityUtils.toString(entity, "UTF-8");
                System.out.println(ret);
                final JSONObject jsonObject = JSONObject.parseObject(ret, JSONObject.class);
                return jsonObject;
            } else {
                System.out.println("状态码错误 ：" + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/startArthas")
    public void startArthas() {
        final String property = System.getProperty("user.home");
        final String arthasPath = property + "\\arthas\\arthas-boot.jar";
        String commands = "java -jar " + arthasPath + "  " + pid;

        new Thread(new Runnable() {
            @Override
            public void run() {
                RuntimeUtil.execForLines(commands);
            }
        }).start();
    }

    public String init_session() {
        JSONObject json = new JSONObject();
        json.put("action", "init_session");
        final JSONObject jsonObject = reqToArthas(json);
        sessionId = jsonObject.getString("sessionId");
        return sessionId;
    }

    public List<DirTreeVo> getPackageTree(List<String> pathList) {

        //第一步，处理存在重复节点的数据，因为linux递归获取目录，所以下面的目录肯定包含上面的目录，将列表倒序
        Collections.reverse(pathList);
        //建立新的列表和临时字符串
        List<String> paths = new ArrayList<>();
//        String tempStr = "";

        //判断是否包含，如果不包含，就添加进入新的字符串，
        for (String path : pathList) {
//            if (!tempStr.contains(path)) {
//                tempStr += path;
            paths.add(path);
//            }
        }

        //创建新的列表，用来存储返回树形数据
        List<DirTreeVo> dirTreeVos = new ArrayList<>();
        //递归处理列表数据为树形数据
        dealTree(paths, dirTreeVos, "");

        return dirTreeVos;
    }


    /**
     * 递归处理树形数据
     *
     * @param pathList
     * @param dirTreeVos
     * @author xxx
     * @date 2020/7/17
     */
    private List<DirTreeVo> dealTree(List<String> pathList, List<DirTreeVo> dirTreeVos, String currentName) {
        List<String> urlNoChild = pathList.stream().filter(dir -> !dir.contains(".")).collect(Collectors.toList());

        for (String path : urlNoChild) {
            DirTreeVo dirTreeVo = new DirTreeVo();
            dirTreeVo.setLabel(path);
            final String fullName = currentName + "." + path;
            if (fullName.startsWith(".")) {
                dirTreeVo.setFullName(fullName.substring(1, fullName.length()));
            } else {
                dirTreeVo.setFullName(fullName);
            }
            dirTreeVos.add(dirTreeVo);
        }

        //过滤出包含/的list
        List<String> urlContansChild = pathList.stream().filter(dir -> dir.contains(".")).collect(Collectors.toList());

        if (urlContansChild != null && urlContansChild.size() > 0) {
            //将存在/的路径分组
            Map<String, List<String>> hasChildMap = urlContansChild.stream()
                    .collect(Collectors.groupingBy(item -> item.split("\\.")[0]));
            for (Map.Entry<String, List<String>> entry : hasChildMap.entrySet()) {
                //处理children
                List<String> childList = entry.getValue();

                //获取字列表数据
                childList = childList.stream().map(childPath -> childPath.substring(entry.getKey().length() + 1)).collect(Collectors.toList());

                //生成Tree
                DirTreeVo dirTreeVo = new DirTreeVo();
                dirTreeVo.setLabel(entry.getKey());
                final String fullName = currentName + "." + entry.getKey();
                if (fullName.startsWith(".")) {
                    dirTreeVo.setFullName(fullName.substring(1, fullName.length()));
                } else {
                    dirTreeVo.setFullName(fullName);
                }

                //关键，设置children
                List<DirTreeVo> dirTreeVoList = new ArrayList<>();
                dirTreeVo.setChildren(dealTree(childList, dirTreeVoList, currentName + "." + entry.getKey()));

                dirTreeVos.add(dirTreeVo);
            }
        } else {

        }
        return dirTreeVos;
    }
}
