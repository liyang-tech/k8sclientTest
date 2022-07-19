package com.k8sclient.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.k8sclient.demo.DemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liyang(leonasli)
 * @className K8sClientTest
 * @description TODO
 * @create 2022/4/22 15:34
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplication.class})
public class K8sClientTest {

    String jar1001 = "E:\\workspace\\k8sclientTest\\lib2";

    @Test
    public void createServiceTest() {
        String kubeConfigPath = "config";
        if (!new File(kubeConfigPath).exists()) {
            System.out.println("kubeConfig不存在，跳过");
            return;
        }
        K8sClient k8sClient = new K8sClient(kubeConfigPath);


//        String namespace = "default";
//        String serviceName = "my-nginx-service";
//        Integer port = 80;
//        Map<String, String> selector = new HashMap<>();
//        selector.put("run", "my-nginx");
//        V1Service v1Service = k8sClient.createService(namespace, serviceName, port, selector);
//        System.out.println(v1Service != null ? v1Service.getMetadata() : null);



    }


//    String jar1001 = "E:\\workspace\\k8sclientTest\\lib2";
//    String jar1200 = "E:\\workspace\\k8sclientTest\\lib2";
//    String jar12000 = "E:\\workspace\\k8sclientTest\\libs2";
//
//    @Test
//    public void clientByAnyVersion() throws Exception {
//
////        import io.kubernetes.client.openapi.ApiClient;
////        import io.kubernetes.client.openapi.Configuration;
////        import io.kubernetes.client.util.ClientBuilder;
////        import io.kubernetes.client.util.KubeConfig;
//        System.out.println("=========================开始第一次测试，读取指定jar包12.0.0版本:=========================");
//        JarLoader jarLoader = new JarLoader(new String[]{jar12000});
//        ClassLoaderSwapper classLoaderSwapper = ClassLoaderSwapper.newCurrentThreadClassLoaderSwapper();
//        classLoaderSwapper.setCurrentThreadClassLoader(jarLoader);
//        Class<?> kubeConfigClass = Thread.currentThread().getContextClassLoader().loadClass("io.kubernetes.client.util.KubeConfig");
//        classLoaderSwapper.restoreCurrentThreadClassLoader();
//        Object okubeConfig = kubeConfigClass.newInstance();
//        Method aaa = kubeConfigClass.getDeclaredMethod("loadKubeConfig");
//        Object invoke1 = aaa.invoke(okubeConfig);
//        System.out.println(invoke1);
//
//        JarLoader jarLoader1 = new JarLoader(new String[]{jar12000});
//        ClassLoaderSwapper classLoaderSwapper1 = ClassLoaderSwapper.newCurrentThreadClassLoaderSwapper();
//        classLoaderSwapper1.setCurrentThreadClassLoader(jarLoader1);
//        Class<?> builderClass = Thread.currentThread().getContextClassLoader().loadClass("io.kubernetes.client.util.ClientBuilder");
//        classLoaderSwapper1.restoreCurrentThreadClassLoader();
//
//        JarLoader jarLoader2 = new JarLoader(new String[]{jar1200});
//        ClassLoaderSwapper classLoaderSwapper2 = ClassLoaderSwapper.newCurrentThreadClassLoaderSwapper();
//        classLoaderSwapper2.setCurrentThreadClassLoader(jarLoader2);
//        Class<?> apiClientClass = Thread.currentThread().getContextClassLoader().loadClass("io.kubernetes.client.openapi.ApiClient");
//        classLoaderSwapper2.restoreCurrentThreadClassLoader();
//
//
////        Object okubeConfig = kubeConfigClass.newInstance();
//        Object obuilder = builderClass.newInstance();
//        Object oapiclient = apiClientClass.newInstance();
////        KubeConfig kubeConfig = (KubeConfig) kubeConfigClass.forName("KubeConfig").newInstance();
////        ClientBuilder clientBuilder = (ClientBuilder) builderClass.forName("ClientBuilder").newInstance();
////        ApiClient apiClient = (ApiClient) builderClass.forName("ApiClient").newInstance();
//
//
//        String kubeConfigPath = "config";
//
//        //加载k8s, config
////        ApiClient apiClient= ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
////        //将加载config的client设置为默认的client
////        Configuration.setDefaultApiClient(apiClient);
//
//        JarLoader jarLoader3 = new JarLoader(new String[]{jar1200});
//        ClassLoaderSwapper classLoaderSwapper3 = ClassLoaderSwapper.newCurrentThreadClassLoaderSwapper();
//        classLoaderSwapper3.setCurrentThreadClassLoader(jarLoader3);
//
//        Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("io.kubernetes.client.openapi.apis.CoreV1Api");
//        classLoaderSwapper3.restoreCurrentThreadClassLoader();
//        Object o = aClass.newInstance();
//
//        Method isEmptyMethod = aClass.getDeclaredMethod("listPodForAllNamespaces", Boolean.class, String.class,
//                String.class, String.class, Integer.class, String.class, String.class, String.class, Integer.class, Boolean.class);
//        Object invoke = isEmptyMethod.invoke(o);
//
//        System.out.println(invoke);
//    }


}
