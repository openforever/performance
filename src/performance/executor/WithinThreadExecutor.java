package performance.executor;

import java.util.concurrent.Executor;

public class WithinThreadExecutor implements Executor{
	/*�ڵ����߳�����ͬ���ķ�ʽִ�����������Executor*/
	@Override
	public void execute(Runnable command) {
		command.run();
	}
}
