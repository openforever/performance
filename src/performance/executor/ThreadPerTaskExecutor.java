package performance.executor;

import java.util.concurrent.Executor;

public class ThreadPerTaskExecutor implements Executor{
	
	/*Ϊÿ�����󶼴���һ�����̵߳�Executor*/
	@Override
	public void execute(Runnable command) {
		new Thread(command).start();
	}

}
