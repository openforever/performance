package performance.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Buffer��NIO��Ϊ���ĵĶ���
 * @author snow
 *
 */
public class ABuffer {
	public Buffer createBuffer(){
		//���������ַ�����ʹ�þ�̬�����Ӷ��з��仺����
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		//�Ӽ��������д���������
		byte array[] = new byte[1024];
		ByteBuffer buffer2 = ByteBuffer.wrap(array);
		
		return buffer;
	}
	
	/**
	 * 3�ַ�������buffer������ֻ��������Buffer�ĸ����־λ�������������Buffer������
	 */
	public void fun(){
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		/**
		 * ��position����Ϊ0 ���������־λmark
		 * limitδ�Ķ�
		 * Ϊ��ȡbuffer����Ч������׼��
		 */
		
		buffer.rewind();
		/**
		 * position = 0 , limit = capacity,������˱�־λmark
		 * ���������limit�������޷���֪buffer�����������Щ����Ч��
		 * Ϊ����д��buffer��׼��
		 */
		buffer.clear();
		
		/**
		 * limit = position �� position = 0���������־λmark
		 * ͨ���ڶ�дת����ʹ��
		 */
		buffer.flip();
	}

	public void position(){
		ByteBuffer buffer = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			buffer.put((byte) i);
		}
		buffer.flip();//׼����
		for (int i = 0, limit = buffer.limit(); i < limit;  i++) {
			System.out.print(buffer.get());
			if (i == 4){
				buffer.mark();//��4���λ�������
				System.out.println("mark at " + i );
			}
		}
		buffer.reset();//�ص�mark��λ�ã��������������
		System.out.println("/nreset to mark");
		while (buffer.hasRemaining()){
			System.out.print(buffer.get());//���������� ���ݶ���������
		}
	}
	
	public void duplicate(){//���ƻ�����
		ByteBuffer buffer = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			buffer.put((byte) i);
		}
		/**
		 * ���Ƶ�ǰ������,�����ɵĻ�������ԭ��������ͬ�����ݣ�����һ�������ݸĶ����ǿɼ���
		 * �������ֶ��� ά�����Ե�position��limit��mark
		 * ��������˳��������ԣ�Ϊ�෽ͬʱ���������ṩ�˿���
		 */
		ByteBuffer c = buffer.duplicate();
		System.out.println("after b.duplicate()");
		//java.nio.HeapByteBuffer[pos=10 lim=15 cap=15]
		System.out.println(buffer);
		System.out.println(c);
		c.flip();//���û�����c
		System.out.println("after c.flip()");
		System.out.println(buffer);
		//java.nio.HeapByteBuffer[pos=0 lim=10 cap=15]  ˵������ά�����Եı�־
		System.out.println(c);
		
		c.put((byte) 100);//�򻺳���c��������
		System.out.println("after c.put((byte) 100)");
		System.out.println("buffer.get(0): " + buffer.get(0));//100
		System.out.println("c.get(0): " + c.get(0));//100
		
		//˵����������
	}
	
	public void slice(){//��������Ƭ
		ByteBuffer buffer = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {// Ĭ������û�г�ʼ����Ϊ0
			buffer.put((byte) i);
		}
		buffer.position(2);
		buffer.limit(6);
		/**
		 * ��һ���󻺳������з�Ƭ�����õ����ӻ����������������Ļ���ģ�ͽṹ
		 * ������ϵͳ��ģ�黯
		 * 2345
		 */
		ByteBuffer subBuffer = buffer.slice();
		
		for (int i = 0, len = subBuffer.capacity(); i < len; i++) {
			//���ӻ������н�ÿ��Ԫ�ض�*10
			subBuffer.put(i, (byte) (subBuffer.get(i) * 10));
		}
		System.out.println(buffer);//java.nio.HeapByteBuffer[pos=2 lim=6 cap=15]
		//��Ҫ���ø������� ֱ������������subBuffer������
		buffer.position(0);
		buffer.limit(buffer.capacity());
		System.out.println(buffer);//java.nio.HeapByteBuffer[pos=0 lim=15 cap=15]
		while (buffer.hasRemaining()){
			System.out.print(buffer.get() + "  ");
		}
	}
	
	public void readOnly(){
		ByteBuffer buffer = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {// Ĭ������û�г�ʼ����Ϊ0
			buffer.put((byte) i);
		}
		/**
		 * ����ֻ��������,�õ�һ���뵱ǰ������һ�£�����ֻ��    ���ݰ�ȫ
		 * ����������Ϊ�������ݸ������ĳ������ʱ �������޷�ȷ���÷����Ƿ���޸Ļ�������ʹ��ֻ��
		 * ��ȷ�����������ݲ��ᱻ�޸�
		 * ��ԭʼ���������޸ģ�ֻ��������Ҳ�ǿɼ���
		 */
		ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();
		readOnlyBuffer.flip();
		while (readOnlyBuffer.hasRemaining()){
			System.out.print(readOnlyBuffer.get());//0 ~ 9  ����û������ limit = 10
		}
		System.out.println();
		buffer.put(2, (byte) 20);//�޸�ԭʼ������������
		System.out.println(readOnlyBuffer);//java.nio.HeapByteBufferR[pos=10 lim=10 cap=15]
		readOnlyBuffer.flip();
		while (readOnlyBuffer.hasRemaining()){
			System.out.print(readOnlyBuffer.get());
		}
		/**
		 * ֻ��������������ԭʼ��������ĳһʱ�̵Ŀ��գ����Ǻ�ԭʼ�����������ڴ����ݣ�����д�Ļ�����
		 */
		
	}
	
	public void fileMapMemory() throws IOException{//�ļ�ӳ���ڴ�
		RandomAccessFile raf = new RandomAccessFile(""
				+ "F:/eclipseworkspace/designPattern/performance/src/�ļ�ӳ���ڴ�.txt", "rw");
		FileChannel fc = raf.getChannel();
		MappedByteBuffer mappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 
				0, raf.length());
		while (mappedByteBuffer.hasRemaining()){
			/**
			 * ���ﰴ���ֽڶ�������Ȼ��ת����char������ĸ������ĸ
			 * �ֽ��ڲ���ʾΪASCII��ת��char���������abc������������ASCII
			 * ����charռ�����ֽڰ���һ���ַ�
			 */
			System.out.print((char) mappedByteBuffer.get());
		}
		System.out.println("\n" + mappedByteBuffer);
		mappedByteBuffer.put(0, (byte) 98);//98һ���ֽڣ�ASCIIΪb
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
