package demo.util;

import com.alibaba.fastjson2.JSONObject;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Component
public class SpringUtil implements ApplicationContextAware {

    public static ApplicationContext applicationContext; // Spring应用上下文环境

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, ?> getBeansOfType(Class<?> clz) throws BeansException {

        return applicationContext.getBeansOfType(clz);


    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<?> clz) throws Exception {
        return (T) applicationContext.getBean(clz);
    }

    public static JSONObject getTomcatE() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {

        AnnotationConfigServletWebServerApplicationContext applicationContext1 = (AnnotationConfigServletWebServerApplicationContext) applicationContext;
        final String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {

            String source = applicationContext1.getBeanDefinition(beanDefinitionName).getResourceDescription();
            if (source != null) {
                System.out.println(beanDefinitionName + " 来源:" + source);
            }
        }
         RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
         RedisConnectionFactory requiredConnectionFactory = redisTemplate.getRequiredConnectionFactory();
        LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) requiredConnectionFactory;
        Field connectionProvider = lettuceConnectionFactory.getClass().getDeclaredField("connectionProvider");
        connectionProvider.setAccessible(true);
        LettuceConnectionProvider lettuceConnectionProvider = (LettuceConnectionProvider) connectionProvider.get(lettuceConnectionFactory);
        final Field connectionProvider1 = lettuceConnectionProvider.getClass().getDeclaredField("connectionProvider");

        final WebServer webServer = applicationContext1.getWebServer();
        TomcatWebServer tomcatWebServer = (TomcatWebServer) webServer;

        final Tomcat tomcat = tomcatWebServer.getTomcat();
        ThreadPoolExecutor executor1 = (ThreadPoolExecutor) tomcat.getConnector().getProtocolHandler().getExecutor();
        final int corePoolSize = executor1.getCorePoolSize();
        final int activeCount = executor1.getActiveCount();
        final long completedTaskCount = executor1.getCompletedTaskCount();
        final int largestPoolSize = executor1.getLargestPoolSize();
        final int maximumPoolSize = executor1.getMaximumPoolSize();
        final int poolSize = executor1.getPoolSize();
        final BlockingQueue<Runnable> queue = executor1.getQueue();
        final long threadRenewalDelay = executor1.getThreadRenewalDelay();
        final ThreadPoolExecutor.RejectedExecutionHandler rejectedExecutionHandler = executor1.getRejectedExecutionHandler();
        final long taskCount = executor1.getTaskCount();

        final Field workersField = executor1.getClass().getDeclaredField("workers");
        workersField.setAccessible(Boolean.TRUE);
        HashSet workers = (HashSet) workersField.get(executor1);
        for (Object worker : workers) {
            final Field threadField = worker.getClass().getDeclaredField("thread");
            threadField.setAccessible(true);
            Thread thread = (Thread) threadField.get(worker);

            System.out.println(printMyStatckTrace(thread));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("线程池中的常驻核心线程数 corePoolSize: ", corePoolSize);
        jsonObject.put("活动线程数 activeCount: ", activeCount);
        jsonObject.put("已完成任务数量 completedTaskCount: ", completedTaskCount);
        jsonObject.put("历史最大线程数 largestPoolSize: ", largestPoolSize);
        jsonObject.put("线程池中能够容纳同时执行的最大线程数 maximumPoolSize: ", maximumPoolSize);
        jsonObject.put("线程池中当前线程的数量 poolSize: ", poolSize);
        jsonObject.put("排队队列 queue: ", queue.size());
        jsonObject.put("拒绝策略 rejectedExecutionHandler: ", rejectedExecutionHandler.getClass().getName());
        jsonObject.put("线程池已经执行的和未执行的任务总数 taskCount: ", taskCount);
        jsonObject.put("重建线程的时间间隔 threadRenewalDelay: ", threadRenewalDelay);
        return jsonObject;

    }


    public static String printMyStatckTrace(Thread thread) {

        StackTraceElement[] stackElements = thread.getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append("thread: " + thread.getName());
        if (null != stackElements) {
            for (int i = 0; i < stackElements.length; i++) {
                sb.append("\t\t" + stackElements[i].getClassName());
                sb.append(".").append(stackElements[i].getMethodName());
                sb.append("(").append(stackElements[i].getFileName()).append(":");
                sb.append(stackElements[i].getLineNumber() + ")").append("\n");
            }
        }

        return sb.toString();
    }
}
