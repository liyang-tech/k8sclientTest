package com.k8sclient.demo.utils;


import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author liyang(leonasli)
 * @className K8sClientTest
 * @description TODO
 * @create 2022/4/22 15:34
 **/
@Slf4j
public class K8sClient {

    /**
     * k8s-api客户端
     */
    private ApiClient apiClient;

    public K8sClient() {
        try {
            this.apiClient = ClientBuilder.cluster().build();
        } catch (IOException e) {
            log.error("构建K8s-Client异常", e);
            throw new RuntimeException("构建K8s-Client异常");
        }
    }

    public K8sClient(String kubeConfigPath) {
        try {
            this.apiClient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        } catch (IOException e) {
            log.error("读取kubeConfigPath异常", e);
            throw new RuntimeException("读取kubeConfigPath异常");
        } catch (Exception e) {
            log.error("构建K8s-Client异常", e);
            throw new RuntimeException("构建K8s-Client异常");
        }
    }


    public V1Service createService(String namespace, String serviceName, Integer port, Map<String, String> selector) {
        //构建service的yaml对象
        V1Service svc = new V1ServiceBuilder()
                .withNewMetadata()
                .withName(serviceName)
                .endMetadata()
                .withNewSpec()
                .addNewPort()
                .withProtocol("TCP")
                .withPort(port)
                .withTargetPort(new IntOrString(port))
                .endPort()
                .withSelector(selector)
                .endSpec()
                .build();

        // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
        CoreV1Api api = new CoreV1Api(apiClient);
        V1Service v1Service = null;
        try {
            v1Service = api.createNamespacedService(namespace, svc,  null, null, null);
        } catch (ApiException e) {
            log.error("创建service异常:" + e.getResponseBody(), e);
        } catch (Exception e) {
            log.error("创建service系统异常:", e);
        }
        return v1Service;
    }



//    public static void main(String[] args) throws ApiException, IOException {
//        //直接写config path
//        String kubeConfigPath = "config";
//
//        //加载k8s, config
//        ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
//
//        //将加载config的client设置为默认的client
//        Configuration.setDefaultApiClient(client);
//
//        //创建一个api
//        CoreV1Api api = new CoreV1Api();
//
////        //打印所有的pod
////        V1PodList list = api.listPodForAllNamespaces(null, null, null, null,
////                null, null, null, null, null, null);
////        for (V1Pod item : list.getItems()) {
////            System.out.println("pod======" + item.toString());
////        }
//
//        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("pod.json");
//        JSONObject json = JSONObject.parseObject(IOUtils.toString(is, "utf-8"));
//
//        V1Pod pod = JSONObject.toJavaObject(json, V1Pod.class);
//
//        //打印一个pod
////        V1Pod pod = api.readNamespacedPod("aaa", "aaaa", null, null, null);
//        //创建一个pod
//        V1Pod namespacedPod = api.createNamespacedPod("default", pod, null, null, null);
//        System.out.println("result=====" + namespacedPod);
////        //打印所有的service
////        V1ServiceList serviceList = api.listServiceForAllNamespaces(null, null, null, null, null,
////                null, null, null, null);
////
////        for (V1Service item : serviceList.getItems()) {
////            System.out.println(item);
////        }
//
////        //打印所有的namespace
////        V1NamespaceList namespaceList = api.listNamespace(null, null, null, null, null,
////                null, null, null, null, null);
////
////        for (V1Namespace item : namespaceList.getItems()) {
////            System.out.println(item);
////        }
//
//
//    }


}
