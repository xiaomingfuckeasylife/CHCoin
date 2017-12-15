package nxt;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * decorate with count .
 * 
 * @author clark
 * 
 * 2017年12月15日 上午9:43:49
 * 
 */
public class CountingOutputStream extends FilterOutputStream{
	
	private long count ;
	
	public CountingOutputStream(OutputStream out) {
		super(out);
	}
	
	// all comes to this method in the end 
	@Override
	public void write(int b) throws IOException {
		super.write(b);
		count += 1;
	}
	
	public long getCount(){
		return count;
	};
	
}
