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
 * 400��int���ݣ���д�ļ���������
 * @author snow
 *
 */
public class IOTest {
	private int numOfInts = 400000;
	
	public void ioWrite(String path) throws FileNotFoundException{//�������
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
		try {//��ѭ�����沶���쳣�����ܻҳ���
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
	 * nio����buffer�ģ���Ȼÿ�ζ�������ת���Ŀ���������������Ȼ������ͨ��ioStream
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void nioWrite(String path) throws FileNotFoundException{
		FileOutputStream fos = new FileOutputStream(new File(path));
		FileChannel fc = fos.getChannel();
		//intռ�ĸ��ֽڣ�����*4
		ByteBuffer byteBuffer = ByteBuffer.allocate(numOfInts * 4);
		for (int i = 0; i < numOfInts; i++) {
			byteBuffer.put(Tools.int2byte(i));//��int����ת�����ֽ�����
		}
		byteBuffer.flip();//׼��д
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
		while (byteBuffer.hasRemaining()){//���ֽ�����ת����int
			Tools.byte2int(byteBuffer.get(), byteBuffer.get(), 
					byteBuffer.get(), byteBuffer.get());
			
		}
	}
	
	/**
	 * ���ļ�ֱ��ӳ�䵽�ڴ�
	 * @throws IOException 
	 */
	public void nioMapWrite(String path) throws IOException{
		FileChannel fc = new RandomAccessFile(path, "rw").getChannel();
		//�ļ�ӳ���ڴ�
		IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, 
				numOfInts * 4).asIntBuffer();//Buffer�������
		for (int i = 0; i < numOfInts; i++) {
			ib.put(i);//д���ļ�
		}
		if (fc != null){
			fc.close();
		}
	}
	
	public void nioMapRead(String path) throws IOException{
		FileChannel fc = new RandomAccessFile(path, "rw").getChannel();
		/*
		 * ʹ��MappedByteBuffer���Դ����߶�ȡ��д���ļ����ٶ�
		 * byteBuffer�ǽ��ļ�һ���Զ����ڴ棬Ȼ��������������
		 * stream��ʽ�Ǳ߶��ļ��ߴ������ݣ�Ҳʹ���˻��������Buffered�ɣ������ӣ������
		 * �����ܲ�໹�Ǻܾ޴�һ��������
		 */
		IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, 
				fc.size()).asIntBuffer();
		//�ļ�ӳ���ڴ�
		while (ib.hasRemaining()){
			ib.get();
		}
		if (fc != null){
			fc.close();
		}
	}
}
