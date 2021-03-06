package blxt.qjava.autovalue.reflect;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 获取包中所有的类
 * @author
 *
 */
public class PackageUtil {

    /**
     * 某个类是否存在
     * @param classPackagename  org.apache.log4j.Logger
     * @return
     */
    public static boolean isClassExist(String classPackagename){
        try {
            Class<?> classzs = Class.forName(classPackagename);
            return classzs != null;
        } catch (ClassNotFoundException e) {
        }
        return false;
    }

    /**
     * 判断摸个类是否集成了指定接口
     * @param object      需要判断的类
     * @param interfaces  需要判断的接口
     * @return
     */
    public static boolean isInterfaces(Class<?> object, Class<?> interfaces){
        //获取这个类的所以接口类数组
        Class<?>[] interfacesArray = object.getInterfaces();
        for (Class<?> item : interfacesArray) {
            //判断是否有继承的接口
            if (item == interfaces) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取包括父类的所有字段元素
     * @param classes
     * @return
     */
    public static List<Field> getfieldList(Class classes){
        List<Field> fieldList = new ArrayList<>() ;
        Class tempClass = classes;
        //当父类为null的时候说明到达了最上层的父类(Object类).
        while (tempClass !=null && !tempClass.equals(Object.class)) {
            fieldList.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            //得到父类,然后赋给自己
            tempClass = tempClass.getSuperclass();
        }
        return fieldList;
    }

    public static Field findField(Class classes, String fieldName){
        return findField(classes, fieldName, false);
    }
    /**
     * 查找 Field
     * @param classes
     * @param fieldName
     * @param toLowerCase    忽略大小写
     * @return
     */
    public static Field findField(Class classes, String fieldName, Boolean ...toLowerCase){
        Class tempClass = classes;
        //当父类为null的时候说明到达了最上层的父类(Object类).
        while (tempClass !=null && !tempClass.equals(Object.class)) {
            try {
                return tempClass.getField(fieldName);
            } catch (NoSuchFieldException e) {
                try {
                    return tempClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException noSuchFieldException) {
                    //得到父类,然后赋给自己
                    tempClass = tempClass.getSuperclass();
                }
            }
        }
        return null;
    }


    /**
     * 根据方法获取真实参数名称
     *
     * @param method 方法反射对象
     * @return String[]
     */
    public static String[] getRealParamNames(Method method) {
        if (method == null) {
            return new String[0];
        }
        try {
            //类的反射信息
            CtClass cc = ClassPool.getDefault().get(method.getDeclaringClass().getName());

            Class<?>[] parameterTypes = method.getParameterTypes();
            CtClass[] ctClParamTypes = new CtClass[parameterTypes.length];
            for (int i = 0, length = parameterTypes.length; i < length; i++) {
                ctClParamTypes[i] = ClassPool.getDefault().get(parameterTypes[i].getName());
            }
            CtMethod ctMethod = cc.getDeclaredMethod(method.getName(), ctClParamTypes);

            // 使用javaAssist的反射方法获取方法的参数名
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                return new String[0];
            }
            String[] realParamNames = new String[ctMethod.getParameterTypes().length];
            int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            for (int i = 0; i < realParamNames.length; i++) {
                realParamNames[i] = attr.variableName(i + pos);
            }
            return realParamNames;
        } catch (NotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 获取某包下（包括该包的所有子包）所有类
     *
     * @param packageName
     *            包名
     * @return 类的完整名称
     */
    @Deprecated
    public static List<String> getClassName(String packageName) {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName
     *            包名
     * @param childPackage
     *            是否遍历子包
     * @return 类的完整名称
     */
    @Deprecated
    public static List<String> getClassName(String packageName, boolean childPackage) {
        List<String> fileNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if (type.equals("file")) {
                fileNames = getClassNameByFile(url.getPath(), childPackage);
            } else if (type.equals("jar")) {
                fileNames = getClassNameByJar(url.getPath(), childPackage);
            }
        } else {
            fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
        }
        return fileNames;
    }

    /**
     * 通过google.guava的getTopLevelClasses,获取class
     * @param classloader
     * @param packageName
     * @return
     * @throws IOException
     */
    public static List<String> getClassName(ClassLoader classloader, String packageName) throws IOException {
        List<String> fileNames = new ArrayList<>();;
        ClassPath classpath = ClassPath.from(classloader);;
        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses(packageName)) {
            fileNames.add(classInfo.getName());
        }
        return fileNames;
    }

    public static ImmutableSet<ClassPath.ClassInfo> getClassInfo(ClassLoader classloader, String packageName) throws IOException {
        ClassPath classpath = ClassPath.from(classloader);
        return classpath.getTopLevelClasses(packageName);
    }

    /**
     * 通过google.guava的getTopLevelClasses,获取class,递归获取子类
     * @param classloader
     * @param packageName
     * @return
     * @throws IOException
     */
    public static List<String> getClassNameAll(ClassLoader classloader, String packageName) throws IOException {
        List<String> fileNames = new ArrayList<>();;
        ClassPath classpath = ClassPath.from(classloader);;
        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClassesRecursive(packageName)) {
            fileNames.add(classInfo.getName());
        }
        return fileNames;
    }
    public static ImmutableSet<ClassPath.ClassInfo> getClassInfoAll(ClassLoader classloader, String packageName) throws IOException {
        ClassPath classpath = ClassPath.from(classloader);
        return classpath.getTopLevelClassesRecursive(packageName);
    }


    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath
     *            文件路径
     * @param childPackage
     *            是否遍历子包
     * @return 类的完整名称
     */
    @Deprecated
    private static List<String> getClassNameByFile(String filePath, boolean childPackage) {
        List<String> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9,
                            childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    myClassName.add(childFilePath);
                }
            }
        }

        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     *
     * @param jarPath
     *            jar文件路径
     * @param childPackage
     *            是否遍历子包
     * @return 类的完整名称
     */
    private static List<String> getClassNameByJar(String jarPath, boolean childPackage) {
        List<String> myClassName = new ArrayList<>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    if (childPackage) {
                        if (entryName.startsWith(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            myClassName.add(entryName);
                        }
                    } else {
                        int index = entryName.lastIndexOf("/");
                        String myPackagePath;
                        if (index != -1) {
                            myPackagePath = entryName.substring(0, index);
                        } else {
                            myPackagePath = entryName;
                        }
                        if (myPackagePath.equals(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            myClassName.add(entryName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myClassName;
    }

    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     *
     * @param urls
     *            URL集合
     * @param packagePath
     *            包路径
     * @param childPackage
     *            是否遍历子包
     * @return 类的完整名称
     */
    private static List<String> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) {
        List<String> myClassName = new ArrayList<>();
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + packagePath;
                myClassName.addAll(getClassNameByJar(jarPath, childPackage));
            }
        }
        return myClassName;
    }

    /**
     * 获取jar运行路径
     *
     * @return
     */
    public static String getPath(Class<?> objClass) {
        String path = objClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1);
        }
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path.replace("/classes/", "")
                .replace("/test-classes/", "")
                .replace("/target", "/src/main");
    }

}