package performance.executor;

import java.util.concurrent.Executor;

public class ThreadPerTaskExecutor implements Executor{
	
	/*为每个请求都创建一个新线程的Executor*/
	@Override
	public void execute(Runnable command) {
		new Thread(command).start();
	}

}
