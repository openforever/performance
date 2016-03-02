package performance.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Buffer是NIO最为核心的对象
 * @author snow
 *
 */
public class ABuffer {
	public Buffer createBuffer(){
		//创建有两种方法，使用静态方法从堆中分配缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		//从既有数组中创建缓冲区
		byte array[] = new byte[1024];
		ByteBuffer buffer2 = ByteBuffer.wrap(array);
		
		return buffer;
	}
	
	/**
	 * 3种方法重置buffer，重置只是重置了Buffer的各项标志位，并不真正清空Buffer的内容
	 */
	public void fun(){
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		/**
		 * 将position设置为0 ，并清除标志位mark
		 * limit未改动
		 * 为读取buffer中有效数据做准备
		 */
		
		buffer.rewind();
		/**
		 * position = 0 , limit = capacity,并清楚了标志位mark
		 * 由于清空了limit，所以无法得知buffer里面的数据那些是有效的
		 * 为重新写入buffer做准备
		 */
		buffer.clear();
		
		/**
		 * limit = position ， position = 0，并清除标志位mark
		 * 通常在读写转换中使用
		 */
		buffer.flip();
	}

	public void position(){
		ByteBuffer buffer = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			buffer.put((byte) i);
		}
		buffer.flip();//准备读
		for (int i = 0, limit = buffer.limit(); i < limit;  i++) {
			System.out.print(buffer.get());
			if (i == 4){
				buffer.mark();//在4这个位置做标记
				System.out.println("mark at " + i );
			}
		}
		buffer.reset();//回到mark的位置，处理后续的数据
		System.out.println("/nreset to mark");
		while (buffer.hasRemaining()){
			System.out.print(buffer.get());//后续的所有 数据都将被处理
		}
	}
	
	public void duplicate(){//复制缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			buffer.put((byte) i);
		}
		/**
		 * 复制当前缓冲区,新生成的缓冲区和原来共享相同的数据，任意一方的数据改动都是可见的
		 * 但两者又独立 维护各自的position，limit和mark
		 * 大大增加了程序的灵活性，为多方同时处理数据提供了可能
		 */
		ByteBuffer c = buffer.duplicate();
		System.out.println("after b.duplicate()");
		//java.nio.HeapByteBuffer[pos=10 lim=15 cap=15]
		System.out.println(buffer);
		System.out.println(c);
		c.flip();//重置缓冲区c
		System.out.println("after c.flip()");
		System.out.println(buffer);
		//java.nio.HeapByteBuffer[pos=0 lim=10 cap=15]  说明各自维护各自的标志
		System.out.println(c);
		
		c.put((byte) 100);//向缓冲区c存入数据
		System.out.println("after c.put((byte) 100)");
		System.out.println("buffer.get(0): " + buffer.get(0));//100
		System.out.println("c.get(0): " + c.get(0));//100
		
		//说明共享数据
	}
	
	public void slice(){//缓冲区分片
		ByteBuffer buffer = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {// 默认其他没有初始化的为0
			buffer.put((byte) i);
		}
		buffer.position(2);
		buffer.limit(6);
		/**
		 * 将一个大缓冲区进行分片处理，得到的子缓冲区都具有完整的缓冲模型结构
		 * 有利于系统的模块化
		 * 2345
		 */
		ByteBuffer subBuffer = buffer.slice();
		
		for (int i = 0, len = subBuffer.capacity(); i < len; i++) {
			//在子缓冲区中将每个元素都*10
			subBuffer.put(i, (byte) (subBuffer.get(i) * 10));
		}
		System.out.println(buffer);//java.nio.HeapByteBuffer[pos=2 lim=6 cap=15]
		//需要重置父缓冲区 直接输出是输出的subBuffer的内容
		buffer.position(0);
		buffer.limit(buffer.capacity());
		System.out.println(buffer);//java.nio.HeapByteBuffer[pos=0 lim=15 cap=15]
		while (buffer.hasRemaining()){
			System.out.print(buffer.get() + "  ");
		}
	}
	
	public void readOnly(){
		ByteBuffer buffer = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {// 默认其他没有初始化的为0
			buffer.put((byte) i);
		}
		/**
		 * 创建只读缓冲区,得到一个与当前缓冲区一致，并且只读    数据安全
		 * 当缓冲区作为参数传递给对象的某个方法时 ，由于无法确定该方法是否会修改缓冲区，使用只读
		 * 能确保缓冲区数据不会被修改
		 * 对原始缓冲区的修改，只读缓冲区也是可见的
		 */
		ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();
		readOnlyBuffer.flip();
		while (readOnlyBuffer.hasRemaining()){
			System.out.print(readOnlyBuffer.get());//0 ~ 9  后续没有数据 limit = 10
		}
		System.out.println();
		buffer.put(2, (byte) 20);//修改原始缓冲区的数据
		System.out.println(readOnlyBuffer);//java.nio.HeapByteBufferR[pos=10 lim=10 cap=15]
		readOnlyBuffer.flip();
		while (readOnlyBuffer.hasRemaining()){
			System.out.print(readOnlyBuffer.get());
		}
		/**
		 * 只读缓冲区并不是原始缓冲区在某一时刻的快照，而是和原始缓冲区共享内存数据，不可写的缓冲区
		 */
		
	}
	
	public void fileMapMemory() throws IOException{//文件映射内存
		RandomAccessFile raf = new RandomAccessFile(""
				+ "F:/eclipseworkspace/designPattern/performance/src/文件映射内存.txt", "rw");
		FileChannel fc = raf.getChannel();
		MappedByteBuffer mappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 
				0, raf.length());
		while (mappedByteBuffer.hasRemaining()){
			/**
			 * 这里按找字节读出来，然后转换成char，有字母就是字母
			 * 字节内部表示为ASCII，转成char，就能输出abc，否则就是输出ASCII
			 * 不过char占两个字节啊。一个字符
			 */
			System.out.print((char) mappedByteBuffer.get());
		}
		System.out.println("\n" + mappedByteBuffer);
		mappedByteBuffer.put(0, (byte) 98);//98一个字节，ASCII为b
		raf.close();
		
	}
	public static void main(String[] args) {
		ABuffer ab = new ABuffer();
		//ab.position();
		//ab.duplicate();
		//ab.slice();
		//ab.readOnly();
		try {
			ab.fileMapMemory();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
