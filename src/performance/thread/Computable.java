package performance.thread;

/**
 * ������Ч ���������Ľ������
 * ����֮ǰ�ļ��������ܽ����ӳ٣��������������ȴ��Ҫ������ڴ�
 * @author snow
 *
 */
public interface Computable<A, V> {
	
	/*��������ΪA���������ΪV*/
	V compute(A arg) throws InterruptedException;
}
