package performance.executor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * �����̳߳ص�Web������
 * ͨ���н��������ֹ�߸��ɵ�Ӧ�ó���ľ��ڴ�
 * Executor����������-������ģʽ
 * 		��������������ύ������ִ�й��̽����������Runnable����ʾ����
 * 		���첽��ʽִ�������κ�ʱ�̣�֮ǰ�ύ�����״̬���������ɼ���
 * @author snow
 *
 */
public class TaskExecutionWebServer {
	private static final int NTHREADS = 100;
	/*
	 * ͨ�����Ʋ������������������ȷ��Ӧ�ó��򲻻���Ϊ��Դ�ľ���ʧ�ܣ�
	 * ����������ϡȱ��Դ�Ͼ���������Ӱ������
	 * newFixedThreadPool��
	 * ����һ���̶����ȵ��̳߳أ�100���߳�
	 * ÿ���ύһ������ʹ���һ���̣߳�ֱ�������̳߳ص����ֵ
	 * ���ĳ���̷߳������쳣���������̳߳ػᲹ��һ���߳�
	 * 
	 * ����ThreadPoolExecutorʵ��
	 */
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);
	
	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(80);
		while (true){
			final Socket connection = socket.accept();
			Runnable task = new Runnable() {
				@Override
				public void run() {
					//handleRquest(connection); ��������
					System.out.println("����������ÿ������");
				}
			};
			exec.execute(task);
		}
	}
}
