//package io.github.toohandsome.attach.classloader;
//
//import java.net.URL;
//import java.net.URLClassLoader;
//
///**
// *
// * @author hengyunabc 2020-06-22
// *
// */
//public class AttachClassloader extends URLClassLoader {
//    public AttachClassloader(URL[] urls) {
//        super(urls, ClassLoader.getSystemClassLoader().getParent());
//    }
//
//    @Override
//    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//        final Class<?> loadedClass = findLoadedClass(name);
//        if (loadedClass != null) {
//            return loadedClass;
//        }
//        try {
//            Class<?> aClass = findClass(name);
//            if (resolve) {
//                resolveClass(aClass);
//            }
//            return aClass;
//        } catch (Exception e) {
//            // ignore
//        }
//        return super.loadClass(name, resolve);
//    }
//}
