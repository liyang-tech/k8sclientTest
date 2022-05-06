//package com.k8sclient.demo.service;
//
//import com.k8sclient.demo.utils.MyClassLoader;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.lang.reflect.Method;
//
///**
// * @author liyang(leonasli)
// * @className MyClassLoaderTest
// * @description TODO
// * @create 2022/4/26 16:39
// **/
//@RunWith(SpringRunner.class)
//public class MyClassLoaderTest {
//
//    @Test
//    public void loadjarTest() throws Exception {
//        MyClassLoader classLoader = new MyClassLoader("E:\\workspace\\k8sclientTest\\lib2");
//        //尝试用自己改写类加载机制去加载自己写的java.lang.String.class
//        Class clazz = classLoader.loadClass("io.kubernetes.client.openapi.apis.CoreApi");
//        Object obj = clazz.newInstance();
//
//        Method method = clazz.getDeclaredMethod("listNamespacedPod", null);
//        method.invoke(obj, null);
//
//        System.out.println(clazz.getClassLoader().getClass().getName());
//    }
//
//
//}