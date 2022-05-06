//package com.k8sclient.demo.utils;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.Channels;
//import java.nio.channels.FileChannel;
//import java.nio.channels.WritableByteChannel;
//
///**
// * @author liyang(leonasli)
// * @className MyClassLoader
// * @description TODO
// * @create 2022/4/26 16:19
// **/
//public class MyClassLoader extends ClassLoader {
//
//    private String rootDir;
//
//    public MyClassLoader(String rootDir) {
//        this.rootDir = rootDir;
//    }
//
//    public Class<?> findClass(String className) throws ClassNotFoundException {
//        Class clazz = this.findLoadedClass(className);
//        FileChannel fileChannel = null;
//        WritableByteChannel outChannel = null;
//        if (null == clazz) {
//            try {
//                String classFile = getClassFile(className);
//                FileInputStream fis = new FileInputStream(classFile);
//                fileChannel = fis.getChannel();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                outChannel = Channels.newChannel(baos);
//                ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
//                while (true) {
//                    int i = fileChannel.read(buffer);
//                    if (i == 0 || i == -1) {
//                        break;
//                    }
//                    buffer.flip();
//                    outChannel.write(buffer);
//                    buffer.clear();
//                }
//
//                byte[] bytes = baos.toByteArray();
//                clazz = defineClass(className, bytes, 0, bytes.length);
//
//
//            } catch (FileNotFoundException e) {
//                // ...
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                // ...
//            }
//        }
//        return clazz;
//    }
//
//    /**
//     * 类文件的完全路径
//     */
//    private String getClassFile(String className) {
//        return rootDir + "\\" + className.replace('.', '\\') + ".class";
//    }
//
//
//
//}
