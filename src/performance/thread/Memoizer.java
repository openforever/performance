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
	//final 修饰的，可以推迟到构造器中初始化
	private final Computable<A, V> c;
	
	public Memoizer(Computable<A, V> c) {
		this.c = c;
	}
	
	/*
	 * 没有解决缓存逾期问题：可以通过FutureTask的子类来解决
	 * 在子类中，为每个结果指定一个逾期时间，并定期扫描缓存中的逾期的元素
	 * 没有解决缓存清理的问题：移除旧的结果以便为新的结果腾出空间
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
				//原子方法
				f = cache.putIfAbsent(arg, ft);
				if (f == null){
					f = ft;
					ft.run();//在这里将会调用c.compute
				}
			}
			try {
				return f.get();
			} catch (CancellationException e) {
				/*
				 * 当缓存的是Future而不是值的时候，会导致缓存污染问题：
				 * 如果某个计算被取消或者失败，那么计算这个结果时将指明计算被取消或失败
				 * 如果被取消，那么将Future从缓存中移除
				 * 如果检查到RuntimeException，也会移除
				 * 
				 */
				cache.remove(arg, f);
			} catch (ExecutionException e) {
				//throw launderThrowable(e.getCause());
				e.printStackTrace();
			}
			return null;//不写报错。。
		}
	}

}
