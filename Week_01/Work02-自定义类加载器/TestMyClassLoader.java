import java.lang.reflect.Method;

/**
 * @author Deecyn
 * @version 1.1
 * Description: 测试自定义的类加载器 HelloClassLoader 。
 */
public class TestMyClassLoader {
    public static void main(String[] args) throws Exception {

        HelloClassLoader helloClassLoader = new HelloClassLoader("./Hello/Hello.xlass");

        // 调用 Class.forName() 方法，使用指定的类加载器加载指定的类
        Class<?> clazz = Class.forName("Hello", true, helloClassLoader);

        // 使用反射调用方法
        Object obj = clazz.newInstance();
        Method method = clazz.getMethod("hello");
        method.invoke(obj);
    }
}
