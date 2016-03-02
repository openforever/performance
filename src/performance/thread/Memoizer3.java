package performance.thread;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memoizer3<A, V> implements Computable<A, V>{
	
	private final Map<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
	//final 修饰的，可以推迟到构造器中初始化
	private final Computable<A, V> c;
	
	public Memoizer3(Computable<A, V> c) {
		this.c = c;
	}
	
	/*
	 * 有灰常好的并发性，但是任然存在两个线程计算出相同值的漏洞
	 * 如果某个线程启动了某个开销很大的计算，而其他线程并不知道这个线程正在执行，
	 * 那么很可能重复计算(A 计算的时候，缓存中并没有结果，所以B也开始计算)
	 */
	@Override
	public V compute(final A arg) throws InterruptedException {
		Future<V> f = cache.get(arg);
		/*
		 * if代码块，任然是非原子的"先检查后执行"操作
		 * 两个线程任然可能在同一时间调用compute来计算相同的值
		 * 可以采用ConcurrentHashMap中的putIfAbsent原子方法来完成
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
			ft.run();//在这里将会调用c.compute
		}
		try {
			return f.get();
		} catch (ExecutionException e) {
			//throw launderThrowable(e.getCause());
			e.printStackTrace();
		}
		return null;//不写报错。。
	}

}
