package performance.executor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 基于线程池的Web服务器
 * 通过有界队列来防止高负荷的应用程序耗尽内存
 * Executor基于生产者-消费者模式
 * 		将请求处理任务的提交过程与执行过程解耦开来，并用Runnable来表示任务
 * 		以异步方式执行任务，任何时刻，之前提交任务的状态不是立即可见的
 * @author snow
 *
 */
public class TaskExecutionWebServer {
	private static final int NTHREADS = 100;
	/*
	 * 通过限制并发任务的数量，可以确保应用程序不会因为资源耗尽而失败，
	 * 或者由于在稀缺资源上竞争而严重影响性能
	 * newFixedThreadPool：
	 * 创建一个固定长度的线程池，100个线程
	 * 每当提交一个任务就创建一个线程，直到到达线程池的最大值
	 * 如果某个线程发生了异常而结束，线程池会补充一个线程
	 * 
	 * 返回ThreadPoolExecutor实例
	 */
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);
	
	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(80);
		while (true){
			final Socket connection = socket.accept();
			Runnable task = new Runnable() {
				@Override
				public void run() {
					//handleRquest(connection); 处理请求
					System.out.println("服务器处理每个请求");
				}
			};
			exec.execute(task);
		}
	}
}
