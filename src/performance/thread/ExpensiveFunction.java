package performance.thread;

import java.math.BigInteger;

public class ExpensiveFunction implements Computable<String	, BigInteger> {

	@Override
	public BigInteger compute(String arg) throws InterruptedException {
		//������ʱ��ļ����
		return new BigInteger(arg);
	}

}
