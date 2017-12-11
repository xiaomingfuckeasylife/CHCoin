package nxt.util;

import java.nio.ByteBuffer;

import fr.cryptohash.Shabal256;

/**
 * 
 * @author clark
 * 
 * 2017年12月11日 下午4:17:26
 * 
 */
public class MiningPlot {
	
	// hash value size 32 byte 
	public static int HASH_SIZE = 32;
	// every scoop has two hash value 
	public static int HASHES_PER_SCOOP = 2;
	// scoop size is 64 byte
	public static int SCOOP_SIZE = HASHES_PER_SCOOP * HASH_SIZE;
	// every plot has 4096 scoop 
	public static int SCOOPS_PER_PLOT =  4096;
	// so the plot size is 4096 * 64 
	public static int PLOT_SIZE = SCOOPS_PER_PLOT * SCOOP_SIZE;
	// the plot 
	public byte[] data = new byte[PLOT_SIZE];
	
	public static int HASH_CAP = 4096;
	
	/**
	 * initial Mining plot using accountId and nonce.
	 * @param addr accountId
	 * @param nonce 
	 */
	public MiningPlot(long addr , long nonce){
		ByteBuffer base_buffer = ByteBuffer.allocate(16);
		base_buffer.putLong(addr);
		base_buffer.putLong(nonce);
		byte[] base = base_buffer.array();
		Shabal256 md = new Shabal256();
		byte[] gendata = new byte[PLOT_SIZE + base.length];
		System.arraycopy(base, 0, gendata, PLOT_SIZE, base.length);
		for(int i = PLOT_SIZE; i > 0; i -= HASH_SIZE) {
			md.reset();
			int len = PLOT_SIZE + base.length - i;
			if(len > HASH_CAP) {
				len = HASH_CAP;
			}
			md.update(gendata, i, len);
			md.digest(gendata, i - HASH_SIZE, HASH_SIZE);
		}
		md.reset();
		md.update(gendata);
		byte[] finalhash = md.digest();
		for(int i = 0; i < PLOT_SIZE; i++) {
			data[i] = (byte) (gendata[i] ^ finalhash[i % HASH_SIZE]);
		}
	}
	
	/**
	 * using scoop to hashing
	 * @param md
	 * @param pos
	 */
	public void hashScoop(Shabal256 md , int pos){
		md.update(data,pos * SCOOP_SIZE , SCOOP_SIZE);
	}
	
	
}
