package blxt.qjava.autovalue.autoload;

import blxt.qjava.autovalue.inter.Autowired;
import blxt.qjava.autovalue.inter.Component;
import blxt.qjava.autovalue.inter.ComponentScan;
import blxt.qjava.autovalue.inter.autoload.AutoLoadFactory;
import blxt.qjava.autovalue.util.ObjectPool;

import java.lang.reflect.Field;

/**
 * 自动装载对象
 * @Author: Zhang.Jialei
 * @Date: 2020/12/4 12:34
 */
@AutoLoadFactory(annotation = Component.class, priority = 2)
public class AutoObject extends AutoLoadBase {

    /**
     * Autowired 注解注册
     *
     * @param object  class class的构造函数必须是public的
     * @return 初始化后的实例对象
     */
    @Override
    public Object inject(Class<?> object){
        Object bean = ObjectPool.getObject(object);

        // 获取f对象对应类中的所有属性域
        Field[] fields = bean.getClass().getDeclaredFields();

        // 遍历属性
        for (Field field : fields)
        {
            // 过滤 final 元素, 获取修饰关键词  Modifier.toString(field.getModifiers())
            if ((field.getModifiers() & 16) != 0){
                continue;
            }
            Autowired valuename = field.getAnnotation(Autowired.class);
            if(valuename == null){
                continue;
            }

            // 获取原来的访问控制权限
            boolean accessFlag = field.isAccessible();
            // 修改访问控制权限
            field.setAccessible(true);

            // 属性值
            Object value;
            try {
                // 根据类型,获取对象
                Class<?> objClass = field.getType();
                /* 递归, 子元素内的Autowired注解实现 value = ObjectPool.getObject(objClass); */
                value = autoWiredRegister(objClass);
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            // 恢复访问控制权限
            field.setAccessible(accessFlag);
        }
        return bean;
    }

    /**
     * Autowired 注解注册
     *
     * @param object  class class的构造函数必须是public的
     * @return 初始化后的实例对象
     */
    public Object autoWiredRegister(Class<?> object){

        return inject(object);
    }


    /**
     * 将对象注入统一的Object管理池.
     *
     * @param object   class class的构造函数必须是public的
     * @return 初始化后的实例对象
     */
    public static Object register(Class<?>  object){
        return  ObjectPool.putObject(object);
    }


    /**
     * 获取启动类的ComponentScan注解路径
     *
     * @param objClass 要扫描的包路径
     * @throws Exception
     */
    @Override
    public String getScanPackageName(Class<?> objClass) {
        ComponentScan annotation = objClass.getAnnotation(ComponentScan.class);
        if (annotation == null) {
            return null;
        }
        return annotation.value();
    }

}