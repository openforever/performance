package performance.thread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memoizer2<A, V> implements Computable<A, V>{
	
	private final Map<A, V> cache = new ConcurrentHashMap<A, V>();
	//final ���εģ������Ƴٵ��������г�ʼ��
	private final Computable<A, V> c;
	
	public Memoizer2(Computable<A, V> c) {
		this.c = c;
	}
	
	/*
	 * û�м�������ΪConcurrentHashMap���̰߳�ȫ��
	 * ���߳̿��Բ�����ʹ����
	 * ©���������߳�ͬʱ����computeʱ����һ��©�������ܻᵼ�¼���õ���ͬ��ֵ
	 * (����������ǣ�������ͬ�����ݱ�������)
	 * ����ֻ�ṩ���γ�ʼ���Ķ��󻺴���˵�����©���������ȫ����
	 * 
	 * 
	 * ���ĳ���߳�������ĳ�������ܴ�ļ��㣬�������̲߳���֪������߳�����ִ�У�
	 * ��ô�ܿ����ظ�����(A �����ʱ�򣬻����в�û�н��������BҲ��ʼ����)
	 * ���Բ���FutureTask
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
