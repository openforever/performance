package performance.executor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * ֧�ֹرղ�����WebServer
 * JVMֻ��������(���ػ�)�߳�ȫ����ֹ��Ż��˳�
 * ExecutorService�������ڣ����С��رա���ֹͣ
 * 	shutdown:ִ��ƽ���Ĺرղ���
 * 		���ٽ���������ͬʱ�ȴ��Ѿ��ύ������ִ�����--������Щ��δ��ʼִ�е�����
 *  shutdownNow:�ֱ�
 *  	ȡ�����������е����񣬲��Ҳ���������������δ��ʼִ�е�����
 *  
 * @author snow
 *
 */
public class LifeCycleWebServer {
	private final ExecutorService exec = Executors.newFixedThreadPool(100);
	
	public void start() throws IOException{
		ServerSocket socket = new ServerSocket(80);
		while (!exec.isShutdown()){
			try {
				final Socket conn = socket.accept();
				exec.execute(new Runnable() {
					@Override
					public void run() {
						handleRequest(conn);
					}
				});
			} catch (RejectedExecutionException e) {
				if (!exec.isShutdown()){
					//log("task submission rejected", e);
				}
			}
		}
	}

	/**
	 * ��ExecutorService�رպ��ύ�����񣬽���"�ܾ�ִ�� ������"ִ��
	 * �ᶪ�����񣬻���ʹ��execute()�����׳�һ��δ�����쳣RejectedExecutionException
	 */
	public void stop(){
		exec.shutdown();
	}
	protected void handleRequest(Socket conn) {
		/*Request req = readRequest(conn);
		if (isShutdownRequest(req)){
			stop();
		}else {
			dispatchRequest(req);
		}*/
	}
}
