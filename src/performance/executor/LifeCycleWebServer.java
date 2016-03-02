package performance.executor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * 支持关闭操作的WebServer
 * JVM只有在所有(非守护)线程全部终止后才会退出
 * ExecutorService生命周期：运行、关闭、已停止
 * 	shutdown:执行平缓的关闭操作
 * 		不再接收新任务，同时等待已经提交的任务执行完成--包括那些还未开始执行的任务
 *  shutdownNow:粗暴
 *  	取消所有运行中的任务，并且不再启动队列中尚未开始执行的任务
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
	 * 在ExecutorService关闭后提交的任务，将有"拒绝执行 处理器"执行
	 * 会丢弃任务，或者使得execute()方法抛出一个未检查的异常RejectedExecutionException
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
