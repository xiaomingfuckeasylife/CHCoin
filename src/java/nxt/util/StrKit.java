package nxt.util;

/**
 * 
 * @author clark
 * 
 * 2017年12月8日 下午4:53:00
 * 
 */
public final class StrKit {
	
	public static String rmvPrefixBlank(String str){
		char cArr[] = str.toCharArray();
		int index = 0;
		for(int i=0;i<cArr.length;i++){
			if(cArr[i] != ' '){
				index = i;
				break;
			}
		}
		return str.substring(index);
	}
	
}
