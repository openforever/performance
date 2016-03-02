package performance.nio;

public class Tools {
	private Tools(){
		
	}
	
	/**
	 * int转换成byte字节数组
	 * 一个字节byte，表示范围为-128 ~ 127,一个整数分为4个字节，可能有超过127的情况，即最高位是1
	 * & 0xff就不会用补码表示？还是原样 
	 * @param num
	 * @return
	 */
	public static byte[] int2byte(int num){
		byte[] targets = new byte[4];
		targets[3] = (byte) (num & 0xff);//最低位，类型转换为byte，超出的位直接舍掉
		targets[2] = (byte) ((num >> 8) & 0xff);//次低位
		targets[1] = (byte) ((num >> 16) & 0xff);//次高位
		targets[0] = (byte) (num >>> 24);//最高位，无符号右移   最高位可能是负数  int本来就有负数
		return targets;
	}
	
	/**
	 * 直接使用位运算，将4个字节转换成int
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
