package com.k8sclient.demo.service;

import com.k8sclient.demo.utils.ClassLoaderSwapper;
import com.k8sclient.demo.utils.JarLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;

/**
 * @author liyang(leonasli)
 * @className JarClassloaderTests
 * @description TODO
 * @create 2022/4/26 10:39
 **/
@RunWith(SpringRunner.class)
public class JarClassloaderTests {

    String jar1001 = "E:\\workspace\\k8sclientTest\\lib2";
    String jar1200 = "lib\\client-java-api-12.0.0.jar";


    @Test
    public void loadjarTest() throws Exception {
        System.out.println("=========================开始第一次测试，读取指定jar包10.0.1版本:=========================");
        JarLoader jarLoader = new JarLoader(new String[]{jar1001});
        ClassLoaderSwapper classLoaderSwapper = ClassLoaderSwapper.newCurrentThreadClassLoaderSwapper();
        classLoaderSwapper.setCurrentThreadClassLoader(jarLoader);

        Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("io.kubernetes.client.openapi.apis.CoreV1Api");
        classLoaderSwapper.restoreCurrentThreadClassLoader();
        Object o = aClass.newInstance();
//        CoreV1Api coreV1Api = (CoreV1Api) aClass.forName("CoreV1Api").newInstance();

        Method isEmptyMethod = aClass.getDeclaredMethod("listPodForAllNamespaces", Boolean.class, String.class,
                String.class, String.class, Integer.class, String.class, String.class, String.class, Integer.class, Boolean.class);
        Object invoke = isEmptyMethod.invoke(o, null, null, null, null, null, null, null, null, null, null);

//        V1PodList v1PodList = coreV1Api.listPodForAllNamespaces(null, null, null, null, null, null,
//                null, null, null, null);
        System.out.println(invoke);

    }



}
