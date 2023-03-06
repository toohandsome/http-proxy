//package io.github.toohandsome.proxy.patch;
//
//import io.github.toohandsome.classassist.annotation.ClassAssist;
//import io.github.toohandsome.classassist.core.IClassPatch;
//import io.github.toohandsome.classassist.core.MethodMeta;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@ClassAssist(className = "org.apache.http.client.config.RequestConfig")
//public class HttpClientProxyPatch implements IClassPatch {
//    @Override
//    public ArrayList<MethodMeta> getEditMethodList() {
//        MethodMeta methodMeta = new MethodMeta();
//        methodMeta.setBody("return new org.apache.http.HttpHost(\"127.0.0.1\",9999);");
//        methodMeta.setName("getProxy");
//        ArrayList<MethodMeta> arrayList = new ArrayList<>();
//        arrayList.add(methodMeta);
//        return arrayList;
//    }
//
//    @Override
//    public ArrayList<MethodMeta> getAddMethodList() {
//        return null;
//    }
//
//    @Override
//    public List<String> getAddFieldList() {
//        return null;
//    }
//
//    @Override
//    public List<String> getImprotPackages() {
//        return null;
//    }
//}
