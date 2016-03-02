package performance.thread;

/**
 * 构建高效 ，可伸缩的结果缓存
 * 重用之前的计算结果，能降低延迟，提高吞吐量，但却需要更多的内存
 * @author snow
 *
 */
public interface Computable<A, V> {
	
	/*输入类型为A，输出类型为V*/
	V compute(A arg) throws InterruptedException;
}
