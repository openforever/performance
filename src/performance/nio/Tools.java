package performance.nio;

public class Tools {
	private Tools(){
		
	}
	
	/**
	 * intת����byte�ֽ�����
	 * һ���ֽ�byte����ʾ��ΧΪ-128 ~ 127,һ��������Ϊ4���ֽڣ������г���127������������λ��1
	 * & 0xff�Ͳ����ò����ʾ������ԭ�� 
	 * @param num
	 * @return
	 */
	public static byte[] int2byte(int num){
		byte[] targets = new byte[4];
		targets[3] = (byte) (num & 0xff);//���λ������ת��Ϊbyte��������λֱ�����
		targets[2] = (byte) ((num >> 8) & 0xff);//�ε�λ
		targets[1] = (byte) ((num >> 16) & 0xff);//�θ�λ
		targets[0] = (byte) (num >>> 24);//���λ���޷�������   ���λ�����Ǹ���  int�������и���
		return targets;
	}
	
	/**
	 * ֱ��ʹ��λ���㣬��4���ֽ�ת����int
	 * @param b1
	 * @param b2
	 * @param b3
	 * @param b4
	 * @return
	 */
	public static int byte2int(byte b1, byte b2, byte b3, byte b4){
		return ((b1 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b3 & 0xff) << 8) | (b4 & 0xff); 
	}
}
