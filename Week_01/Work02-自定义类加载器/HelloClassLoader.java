import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

/**
 * @author Deecyn
 * @version 1.1
 * Description: 作业二：自定义类加载器 HelloClassLoader：加载一个 Hello.xlass 文件，执行 hello 方法，
 * 此文件内容是一个 Hello.class 文件所有字节（x = 255-x）处理后的文件。
 */
public class HelloClassLoader extends ClassLoader {

    /** 需要加载的文件路径  */
    private String classFilePath;
    /**  类文件字节偏移量 */
    private static final int OFFSET = 255;


    public HelloClassLoader(ClassLoader parent) {
        super(parent);
    }

    public HelloClassLoader(String classFilePath) {
        this.classFilePath = classFilePath;
    }

    /**
     * 自定义类的加载：这里不需要打破双亲委派模型，那么只需要重写 findClass() 方法即可;
     *（如果想打破双亲委派模型，那么就需要重写整个 loadClass() 方法）。
     * @param className 类名称
     * @return Class 文件
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {

        File file = new File(classFilePath);
        try {
            // 获取原始 class 文件的字节数组
            byte[] classBytes = getOriginalClassBytes(file);

            // 需要按位进行反转偏移
            for (int i = 0; i < Objects.requireNonNull(classBytes).length; i++) {
                classBytes[i] = (byte) (OFFSET - classBytes[i]);
            }

            // 调用 defineClass() 方法将把二进制流字节组成的文件转换为一个 java.lang.Class 对象
            return defineClass(className, classBytes, 0, classBytes.length);

        } catch (IOException e) {
            // 抛出异常，调用父类加载器的加载方法
            e.printStackTrace();
            return super.findClass(className);
        }
    }


    /**
     * 读取原始的字节码文件
     * @param classFile 类文件
     * @return 字节数组
     * @throws IOException class 文件的 IO 操作异常
     */
    private byte[] getOriginalClassBytes(File classFile) throws IOException {

        FileInputStream fis = new FileInputStream(classFile);
        FileChannel fc = fis.getChannel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        WritableByteChannel wbc = Channels.newChannel(baos);
        ByteBuffer by = ByteBuffer.allocate(1024);

        while (true)
        {
            int i = fc.read(by);
            if (i == 0 || i == -1)
                break;
            by.flip();
            wbc.write(by);
            by.clear();
        }
        fis.close();
        return baos.toByteArray();
    }


}
