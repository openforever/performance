package performance.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * final�������ϣ���ʾ���಻�ܱ��̳�
 * final���ڷ����ϣ���ʾ�÷������ܱ���д(override)
 * final���ڱ����ϣ� ��ʾ�ñ���һ����ֵ������ֵ�����ܸı�
 * 
 * final���͵ĳ�Ա�����ĳ�ʼ����ʽ��
 * �٣���������ʱֱ�Ӹ�ֵ
 * �ڣ��ڹ��췽������ɸ�ֵ
 * �ۣ�����static��final���͵ĳ�Ա������ֻ��ͨ���ڱ�������ʱֱ�Ӹ�ֵ�������ڹ��췽����
 * 		��Ϊ��static�ı����������ڹ�����ִ�е�
 * 		static��final��Ա����������ʱ�е����⣺���ᱻ����ΪĬ��ֵ
 * �ܣ�����final���͵����ñ�����˵�������޸�ָ���Ǹ����ò��ܸı䣬�����Ǹ����õ����ݲ��ܸı�
 * �ݣ����final����û��������ʱֱ�Ӹ�ֵ����ôÿ�����췽�����������ֵ
 * @author snow
 *
 * @param <A>
 * @param <V>
 */
public class Memoizer1<A, V> implements Computable<A, V>{
	
	//��hashMap(�����̰߳�ȫ��)������֮ǰ����Ľ��
	private final Map<A, V> cache = new HashMap<A, V>();
	//final ���εģ������Ƴٵ��������г�ʼ��
	private final Computable<A, V> c;
	
	public Memoizer1(Computable<A, V> c) {
		this.c = c;
	}
	
	/*
	 * ������compute����ͬ��
	 * �����������⣺ÿ��ֻ����һ���߳��ܹ�ִ��cmopute�������߳̿��ܻ�ȴ��ܳ�ʱ��
	 * ����ж���߳����ŶӼ��㣬���ܱ�û��"����"��ʱ�仹��
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
