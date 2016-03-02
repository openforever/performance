package performance.nio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 400万int数据，读写文件测试性能
 * @author snow
 *
 */
public class IOTest {
	private int numOfInts = 400000;
	
	public void ioWrite(String path) throws FileNotFoundException{//缓冲组件
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
			new	FileOutputStream(new File(path))));
		try {
			for (int i = 0; i < numOfInts; i++) {
				dos.writeInt(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (dos != null){
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void ioRead(String path) throws FileNotFoundException	{
		DataInputStream dis = new DataInputStream(
				new BufferedInputStream(new FileInputStream(path)));
		try {//在循环里面捕获异常，性能灰常低
			for (int i = 0; i < numOfInts; i++) {
				dis.readInt();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (dis != null){
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * nio基于buffer的，虽然每次都有数据转换的开销，但是性能任然优于普通的ioStream
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void nioWrite(String path) throws FileNotFoundException{
		FileOutputStream fos = new FileOutputStream(new File(path));
		FileChannel fc = fos.getChannel();
		//int占四个字节，所以*4
		ByteBuffer byteBuffer = ByteBuffer.allocate(numOfInts * 4);
		for (int i = 0; i < numOfInts; i++) {
			byteBuffer.put(Tools.int2byte(i));//将int整数转换成字节数组
		}
		byteBuffer.flip();//准备写
		try {
			fc.write(byteBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void nioRead(String path) throws FileNotFoundException{
		FileInputStream fis = new FileInputStream(new File(path));
		FileChannel fc = fis.getChannel();
		//IntBuffer intBuffer = IntBuffer.allocate(numOfInts);
		ByteBuffer byteBuffer = ByteBuffer.allocate(numOfInts * 4);
		try {
			fc.read(byteBuffer);
			fc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byteBuffer.flip();
		while (byteBuffer.hasRemaining()){//将字节数组转换成int
			Tools.byte2int(byteBuffer.get(), byteBuffer.get(), 
					byteBuffer.get(), byteBuffer.get());
			
		}
	}
	
	/**
	 * 将文件直接映射到内存
	 * @throws IOException 
	 */
	public void nioMapWrite(String path) throws IOException{
		FileChannel fc = new RandomAccessFile(path, "rw").getChannel();
		//文件映射内存
		IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, 
				numOfInts * 4).asIntBuffer();//Buffer类的子类
		for (int i = 0; i < numOfInts; i++) {
			ib.put(i);//写入文件
		}
		if (fc != null){
			fc.close();
		}
	}
	
	public void nioMapRead(String path) throws IOException{
		FileChannel fc = new RandomAccessFile(path, "rw").getChannel();
		/*
		 * 使用MappedByteBuffer可以大大提高读取和写入文件的速度
		 * byteBuffer是将文件一次性读入内存，然后在做后续操作
		 * stream方式是边读文件边处理数据，也使用了缓冲组件，BufferedＩｎｐｕｔＳｔｒｅａｍ
		 * 但性能差距还是很巨大，一个数量级
		 */
		IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, 
				fc.size()).asIntBuffer();
		//文件映射内存
		while (ib.hasRemaining()){
			ib.get();
		}
		if (fc != null){
			fc.close();
		}
	}
}
