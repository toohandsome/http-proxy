//package io.github.toohandsome.demo.config;
//
//
//import io.github.toohandsome.classassist.annotation.ClassAssist;
//import io.github.toohandsome.classassist.core.IClassPatch;
//import io.github.toohandsome.classassist.core.MethodMeta;
//
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//
///**
// * @author hudcan
// */
//@ClassAssist(className = "org.springframework.web.client.RestTemplate")
//public class MyPatch  implements IClassPatch {
//
//
//    @Override
//    public ArrayList<MethodMeta> getConstructorsMethodList() {
//        final MethodMeta methodMeta = new MethodMeta();
//
//        methodMeta.setName("RestTemplate");
//        methodMeta.setInsertAfter(" this.getInterceptors().add(new TLogRestTemplateInterceptor()); ");
//        final ArrayList<MethodMeta> objects1 = new ArrayList<>();
//        objects1.add(methodMeta);
//
//        return objects1;
//    }
//
//
//    @Override
//    public ArrayList<MethodMeta> getEditMethodList() {
//        return null;
//    }
//
//    @Override
//    public List<String> getImprotPackages() {
//        final ArrayList<String> objects = new ArrayList<>();
//        objects.add("io.github.toohandsome.demo.config");
//        return objects;
//    }
//}