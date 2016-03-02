package performance.thread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memoizer2<A, V> implements Computable<A, V>{
	
	private final Map<A, V> cache = new ConcurrentHashMap<A, V>();
	//final 修饰的，可以推迟到构造器中初始化
	private final Computable<A, V> c;
	
	public Memoizer2(Computable<A, V> c) {
		this.c = c;
	}
	
	/*
	 * 没有加锁，因为ConcurrentHashMap是线程安全的
	 * 多线程可以并发的使用他
	 * 漏洞：两个线程同时调用compute时存在一个漏洞，可能会导致计算得到相同的值
	 * (缓存的作用是：避免相同的数据被计算多次)
	 * 对于只提供单次初始化的对象缓存来说，这个漏洞会带来安全风险
	 * 
	 * 
	 * 如果某个线程启动了某个开销很大的计算，而其他线程并不知道这个线程正在执行，
	 * 那么很可能重复计算(A 计算的时候，缓存中并没有结果，所以B也开始计算)
	 * 可以采用FutureTask
	 */
	@Override
	public V compute(A arg) throws InterruptedException {
		V result = cache.get(arg);
		if (result == null){
			result = c.compute(arg);
			cache.put(arg, result);
		}
		return result;
	}

}
