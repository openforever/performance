package performance.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * InputStream和OutputStream是以字节为单位处理数据，容易建立各种过滤器
 * nio, jdk1.4中引入  是基于块的，以块为基本单位处理数据
 * 特性：
 *  1.为所有的原生类型提供buffer缓冲支持
 *      缓冲是一块连续的内存块，是nio读写数据的中转站
 *  2.使用Java.nio.charset.Charset作为字符集编码解码支持
 *  3.增加channel通道对象，作为新的原始I/O抽象
 *      表示缓冲数据的源头 或者目的地，是访问缓冲的接口
 *      channel是一个双向的通道，既可读，又可写，Stream是单向的
 *      应用程序不能直接对channel操作，必须通过buffer来操作
 *  4.支持锁和内存映射文件的文件访问接口
 *  5.提供基于Selector的异步网络I/O
 * @author snow
 *
 */
public class Anio {
	/**
	 * JDK为每种java原生类型都创建了一个Buffer,是所有其他buffer的抽象类
	 * 除了ByteBuffer(大多数操作是基于标准的IO操作的)外，其他的Buffer都具有完全一样的操作，唯一的区别是对应的数据类型不同
	 * 
	 */
	private Buffer buffer = null;
	
	public void aboutBuffer(){
		ByteBuffer buffer = ByteBuffer.allocate(15); 
	}
	public static void nioCopyFile(String resource, String destination) 
			throws FileNotFoundException{
		FileInputStream fis = new FileInputStream(resource);
		FileOutputStream fos = new FileOutputStream(destination);
		FileChannel readChannel = fis.getChannel();//读文件通道
		FileChannel writeChannel = fos.getChannel();//写文件通道
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);// 缓冲  
		while (true){
			buffer.clear();//position = 0   limit = capacity  为操作buffer做准备
			try {
				int len = readChannel.read(buffer);//读入数据, 写入buffer
				if (len == -1){
					break;//读取完毕
				}
				/**
				 * 重置position ，将buffer从写模式转换成读模式需要调用
				 * 不仅重置了当前的position为0 ，而且将limit设置到当前position的位置，防止在读模式中
				 * 读到应用程序根本没有进行操作的区域
				 * 
				 * position始终指向下一个即将操作的位置
				 */
				buffer.flip();//limit = position   position = 0
				writeChannel.write(buffer);//写入文件
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					readChannel.close();
					writeChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
