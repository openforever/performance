package performance.thread;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memoizer3<A, V> implements Computable<A, V>{
	
	private final Map<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
	//final ���εģ������Ƴٵ��������г�ʼ��
	private final Computable<A, V> c;
	
	public Memoizer3(Computable<A, V> c) {
		this.c = c;
	}
	
	/*
	 * �лҳ��õĲ����ԣ�������Ȼ���������̼߳������ֵͬ��©��
	 * ���ĳ���߳�������ĳ�������ܴ�ļ��㣬�������̲߳���֪������߳�����ִ�У�
	 * ��ô�ܿ����ظ�����(A �����ʱ�򣬻����в�û�н��������BҲ��ʼ����)
	 */
	@Override
	public V compute(final A arg) throws InterruptedException {
		Future<V> f = cache.get(arg);
		/*
		 * if����飬��Ȼ�Ƿ�ԭ�ӵ�"�ȼ���ִ��"����
		 * �����߳���Ȼ������ͬһʱ�����compute��������ͬ��ֵ
		 * ���Բ���ConcurrentHashMap�е�putIfAbsentԭ�ӷ��������
		 */
		if (f == null){
			Callable<V> eval = new Callable<V>() {
				@Override
				public V call() throws Exception {
					return c.compute(arg);
				}
			};
			FutureTask<V> ft = new FutureTask<V>(eval);
			f = ft;
			cache.put(arg, ft);
			ft.run();//�����ｫ�����c.compute
		}
		try {
			return f.get();
		} catch (ExecutionException e) {
			//throw launderThrowable(e.getCause());
			e.printStackTrace();
		}
		return null;//��д������
	}

}
