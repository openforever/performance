package performance.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memoizer<A, V> implements Computable<A, V>{
	
	private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
	//final ���εģ������Ƴٵ��������г�ʼ��
	private final Computable<A, V> c;
	
	public Memoizer(Computable<A, V> c) {
		this.c = c;
	}
	
	/*
	 * û�н�������������⣺����ͨ��FutureTask�����������
	 * �������У�Ϊÿ�����ָ��һ������ʱ�䣬������ɨ�軺���е����ڵ�Ԫ��
	 * û�н��������������⣺�Ƴ��ɵĽ���Ա�Ϊ�µĽ���ڳ��ռ�
	 */
	
	@Override
	public V compute(final A arg) throws InterruptedException {
		while (true){
			Future<V> f = cache.get(arg);
			if (f == null){
				Callable<V> eval = new Callable<V>() {
					@Override
					public V call() throws Exception {
						return c.compute(arg);
					}
				};
				FutureTask<V> ft = new FutureTask<V>(eval);
				//ԭ�ӷ���
				f = cache.putIfAbsent(arg, ft);
				if (f == null){
					f = ft;
					ft.run();//�����ｫ�����c.compute
				}
			}
			try {
				return f.get();
			} catch (CancellationException e) {
				/*
				 * ���������Future������ֵ��ʱ�򣬻ᵼ�»�����Ⱦ���⣺
				 * ���ĳ�����㱻ȡ������ʧ�ܣ���ô����������ʱ��ָ�����㱻ȡ����ʧ��
				 * �����ȡ������ô��Future�ӻ������Ƴ�
				 * �����鵽RuntimeException��Ҳ���Ƴ�
				 * 
				 */
				cache.remove(arg, f);
			} catch (ExecutionException e) {
				//throw launderThrowable(e.getCause());
				e.printStackTrace();
			}
			return null;//��д������
		}
	}

}
