package performance.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * InputStream��OutputStream�����ֽ�Ϊ��λ�������ݣ����׽������ֹ�����
 * nio, jdk1.4������  �ǻ��ڿ�ģ��Կ�Ϊ������λ��������
 * ���ԣ�
 *  1.Ϊ���е�ԭ�������ṩbuffer����֧��
 *      ������һ���������ڴ�飬��nio��д���ݵ���תվ
 *  2.ʹ��Java.nio.charset.Charset��Ϊ�ַ����������֧��
 *  3.����channelͨ��������Ϊ�µ�ԭʼI/O����
 *      ��ʾ�������ݵ�Դͷ ����Ŀ�ĵأ��Ƿ��ʻ���Ľӿ�
 *      channel��һ��˫���ͨ�����ȿɶ����ֿ�д��Stream�ǵ����
 *      Ӧ�ó�����ֱ�Ӷ�channel����������ͨ��buffer������
 *  4.֧�������ڴ�ӳ���ļ����ļ����ʽӿ�
 *  5.�ṩ����Selector���첽����I/O
 * @author snow
 *
 */
public class Anio {
	/**
	 * JDKΪÿ��javaԭ�����Ͷ�������һ��Buffer,����������buffer�ĳ�����
	 * ����ByteBuffer(����������ǻ��ڱ�׼��IO������)�⣬������Buffer��������ȫһ���Ĳ�����Ψһ�������Ƕ�Ӧ���������Ͳ�ͬ
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
		FileChannel readChannel = fis.getChannel();//���ļ�ͨ��
		FileChannel writeChannel = fos.getChannel();//д�ļ�ͨ��
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);// ����  
		while (true){
			buffer.clear();//position = 0   limit = capacity  Ϊ����buffer��׼��
			try {
				int len = readChannel.read(buffer);//��������, д��buffer
				if (len == -1){
					break;//��ȡ���
				}
				/**
				 * ����position ����buffer��дģʽת���ɶ�ģʽ��Ҫ����
				 * ���������˵�ǰ��positionΪ0 �����ҽ�limit���õ���ǰposition��λ�ã���ֹ�ڶ�ģʽ��
				 * ����Ӧ�ó������û�н��в���������
				 * 
				 * positionʼ��ָ����һ������������λ��
				 */
				buffer.flip();//limit = position   position = 0
				writeChannel.write(buffer);//д���ļ�
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
