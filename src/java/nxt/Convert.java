package nxt;

import java.math.BigInteger;

/**
 * 
 * @author clark
 * 
 * 2017年12月13日 上午9:48:04
 * 
 */
public final class Convert {
	
	private static final char[] hexChars = { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };
	
	public static final BigInteger two64 = new BigInteger("18446744073709551616");
	
	
	/**
	 * parse hex string into byte . the convert method of toHexString();
	 * @param hex
	 * @return
	 */
	public static byte[] parseHexString(String hex) {
        if (hex == null) {
            return null;
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int char1 = hex.charAt(i * 2);
            char1 = char1 > 0x60 ? char1 - 0x57 : char1 - 0x30;
            int char2 = hex.charAt(i * 2 + 1);
            char2 = char2 > 0x60 ? char2 - 0x57 : char2 - 0x30;
            if (char1 < 0 || char2 < 0 || char1 > 15 || char2 > 15) {
                throw new NumberFormatException("Invalid hex number: " + hex);
            }
            bytes[i] = (byte)((char1 << 4) + char2);
        }
        return bytes;
    }
	
	/**
	 * convert byte to hex String  double the length 
	 * @param bytes
	 * @return
	 */
	public static String toHexString(byte[] bytes){
		if(bytes == null){
			return null;
		}
		char[] charbuf =  new char[bytes.length * 2];
		for(int i=0;i<bytes.length;i++){
			charbuf[i*2] = hexChars[((bytes[i] >> 4) & 0xf)];
			charbuf[i*2+1]=hexChars[(bytes[i] & 0xf)];
		}
		return String.valueOf(charbuf);
	}
	
	/**
	 * convert hash to long id . fetch the first 8 digital . and 
	 * @param hash
	 * @return
	 */
	public static long fullHashToId(byte[] hash){
		if(hash == null || hash.length < 8){
			throw new IllegalArgumentException("hash is null or size less than 8");
		}
		BigInteger bigInteger = new BigInteger(1, new byte[]{hash[7],hash[6],hash[5],hash[4],hash[3],hash[2],hash[1],hash[0]});
		return bigInteger.longValue();
	}
	
	/**
	 * check overflow . 
	 * @param left
	 * @param right
	 * @return
	 */
	public long SafeAdd(long left , long right){
		if(right > 0 ? left > Long.MAX_VALUE - right : left < Long.MIN_VALUE - right){
			throw new RuntimeException("Long value overflow");
		}
		return left + right;
	}
	
	public static String truncate(String s, String replaceNull, int limit, boolean dots) {
        return s == null ? replaceNull : s.length() > limit ? (s.substring(0, dots ? limit - 3 : limit) + (dots ? "..." : "")) : s;
    }
	
	
	public static  long parseUnsignedLong(String blockId){
		
		if(blockId == null){
			return 0;
		}
		
		BigInteger i = new BigInteger(blockId);
		
		if(i.signum() <0 || i.compareTo(two64) != -1){
			throw new IllegalStateException("value over flow ");
		}
		
		return i.longValue();
	}
	
	public static String toUnsignedLong(long objectId){
		if (objectId >= 0) {
            return String.valueOf(objectId);
        }
        BigInteger id = BigInteger.valueOf(objectId).add(two64);
        return id.toString();
	}
	
	public static void main(String[] args) {
		
//		System.out.println(new String(parseHexString(toHexString("helloworld".getBytes()))));
//		
//		long left =  Long.MAX_VALUE;
//		long right = -1;;
//		System.out.println(Long.MAX_VALUE);
//		System.out.println(Long.MIN_VALUE);
//		System.out.println((Long.MAX_VALUE - right));
//		System.out.println(left > (Long.MAX_VALUE - right));
//		
//		// 00000010 => 00100000 
//		//			   00000001
//		//			   00100001 = 33
//		System.out.println((2 << 4) | 1);
//		byte[] hash = new byte[]{1,0,1,1,2,2,2,2,2}; // 144680349920722945 144680345659310081
//		System.out.println(fullHashToId(hash));
		
//		System.out.println(parseUnsignedLong(-1L));
	}
	
}
