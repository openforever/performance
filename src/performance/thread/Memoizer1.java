package performance.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * final用在类上，表示该类不能被继承
 * final用在方法上，表示该方法不能被重写(override)
 * final用在变量上， 表示该变量一旦赋值，他的值将不能改变
 * 
 * final类型的成员变量的初始化方式：
 * ①：声明变量时直接赋值
 * ②：在构造方法中完成赋值
 * ③：对于static的final类型的成员变量，只能通过在变量声明时直接赋值，不能在构造方法中
 * 		因为：static的变量是优先于构造器执行的
 * 		static的final成员变量在声明时有点特殊：不会被设置为默认值
 * ④：对于final类型的引用变量来说，不能修改指的是该引用不能改变，而不是该引用的内容不能改变
 * ⑤：如果final变量没有在声明时直接赋值，那么每个构造方法必须对他赋值
 * @author snow
 *
 * @param <A>
 * @param <V>
 */
public class Memoizer1<A, V> implements Computable<A, V>{
	
	//用hashMap(不是线程安全的)来保存之前计算的结果
	private final Map<A, V> cache = new HashMap<A, V>();
	//final 修饰的，可以推迟到构造器中初始化
	private final Computable<A, V> c;
	
	public Memoizer1(Computable<A, V> c) {
		this.c = c;
	}
	
	/*
	 * 对整个compute方法同步
	 * 可伸缩性问题：每次只能有一个线程能够执行cmopute，其他线程可能会等待很长时间
	 * 如果有多个线程在排队计算，可能比没有"记忆"的时间还长
	 * 
	 */
	@Override
	public synchronized V compute(A arg) throws InterruptedException {
		V result = cache.get(arg);
		if (result == null){
			result = c.compute(arg);
			cache.put(arg, result);
		}
		return result;
	}

}
