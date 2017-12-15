package nxt;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * 
 * @author clark
 * 
 * 2017年12月15日 上午9:46:40
 * 
 * decorate input stream with count method 
 */
public class CountingInputStream extends FilterInputStream{
	
	private int count;
	
	public CountingInputStream(InputStream in) {
		super(in);
	}
	
	// comes to this method 
	@Override
	public int read() throws IOException {
		int n = super.read();
		if(n > 0){
			count += n;
		}
		return n;
	}
	
	// comes to this method 
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int n = super.read(b, off, len);
		if(n > 0){
			count += n;
		}
		return n;
	}
	
	@Override
	public long skip(long n) throws IOException {
		long num = super.skip(n);
		if(num > 0){
			count -= num;
		}
		return num;
	}
	
	public int getCount(){
		return count;
	}
}
