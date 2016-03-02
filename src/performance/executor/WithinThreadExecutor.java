package performance.executor;

import java.util.concurrent.Executor;

public class WithinThreadExecutor implements Executor{
	/*在调用线程中以同步的方式执行所有任务的Executor*/
	@Override
	public void execute(Runnable command) {
		command.run();
	}
}
