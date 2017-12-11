package nxt;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import fr.cryptohash.Shabal256;
import nxt.util.Listeners;
import nxt.util.Logger;
import nxt.util.MiningPlot;
import nxt.util.ThreadPool;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 下午3:18:34
 * 
 * implementation of Genarator 
 */
public class GeneratorImpl implements Generator {
	
	// listener of GeneratorImpl
	private static final Listeners<Generator, Event> listeners = new Listeners<>();
	
	// using to store GeneratorState value with specific generator number 
	private static final ConcurrentMap<Long,GeneratorStateImpl> generators = new ConcurrentHashMap<>();
	
	// hold all the generators 
	private static final Collection<? extends GeneratorState> allGenerators = Collections.unmodifiableCollection(generators.values());
	
	// using this thread to forge a block 
	private static final Runnable generateBlockThread = new  Runnable() {
		
		@Override
		public void run() {
			
			try{
				// if current is scanning block return . 
				if(Nxt.getBlockchainProcessor().isScannning()){
					return ;
				}
				
				try{
					long currBlock = Nxt.getBlockchain().getLastBlock().getHeight();
					Iterator<Entry<Long,GeneratorStateImpl>> it =  generators.entrySet().iterator();
					while(it.hasNext()){
						Entry<Long,GeneratorStateImpl>  generator = it.next();
						GeneratorStateImpl gsi = generator.getValue();
						if(generator.getValue().getBlock() > currBlock){
							gsi.forge();
						}else{
							it.remove();
						}
					}
				}catch(Exception ex){
					Logger.logDebugMessage("Error in block generation thread " , ex);
				}
			}catch(Exception ex){
				Logger.logMessage("critical error , please report to developer .\n" + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}
		}
	};
	
	static {
		// delay 500 milliseconds 
		ThreadPool.scheduleThread("GenerateBlocks", generateBlockThread, 500, TimeUnit.MILLISECONDS);
		
	}
	
	/**
	 * generate block 
	 * @author clark
	 * 2017年12月11日 下午3:37:00
	 */
	class GeneratorStateImpl implements GeneratorState{
		
		private final Long accountId;
		private final String secretPhrase;
		private final byte[] publicKey;
		private volatile BigInteger deadline;
		private final long nonce;
		private final long block;
	
		public GeneratorStateImpl(String secretPhrase,long nonce , byte[] publicKey , long accountId) {
			this.accountId = accountId;
			this.secretPhrase = secretPhrase;
			this.publicKey = publicKey;
			this.nonce = nonce;
			Block lastBlock = Nxt.getBlockchain().getLastBlock();
			block = lastBlock.getHeight()+1;
			
			byte[] lastGenSig = lastBlock.getGenerationSignature();
			long lastGeneratorId = lastBlock.getGeneratorId();
			
			byte[] newGenSig  = calculateGeneratorSignature(lastGenSig, lastGeneratorId);
			
			int scoopNum = calculateScoop(newGenSig,this.block);
			
			deadline = calculateDeadline(accountId,nonce,newGenSig,scoopNum,lastBlock.getBaseTarget());
		}
		
		
		public void forge(){
			long elapsedTime =Nxt.getEpochTime() - 
					Nxt.getBlockchain().getLastBlock().getTimestamp();
			if(elapsedTime - deadline.intValue() > 0 ){
				BlockchainProcessorImpl.getInstance().generatorBlock(publicKey,secretPhrase,nonce);
			}
		}
		
		public Long getAccountId() {
			return accountId;
		}


		public String getSecretPhrase() {
			return secretPhrase;
		}


		public byte[] getPublicKey() {
			return publicKey;
		}


		public BigInteger getDeadline() {
			return deadline;
		}


		public long getNonce() {
			return nonce;
		}


		public int getBlock(){
			
			return 0;
		}
		
	}
	
	
	/**
	 * calculate if hit or not 
	 * @param accountId
	 * @param nonce
	 * @param genSig
	 * @param scoop
	 * @return
	 */
	public BigInteger calculateHit(long accountId, long nonce, byte[] genSig , int scoop){
		MiningPlot plot = new MiningPlot(accountId, nonce);
		Shabal256 md = new Shabal256();
		md.update(genSig);
		plot.hashScoop(md, scoop);
		byte[] hash = md.digest();
		return new BigInteger(new byte[]{hash[7],hash[6],hash[5],hash[4],hash[3],hash[2],hash[1],hash[0]});
	}
	
	/**
	 * 
	 */
	public BigInteger calculateDeadline(long accountId,long nonce , byte[] genSig , int scoop , long baseTarget){
		BigInteger hit = calculateHit(accountId, nonce, genSig, scoop);
		return hit.divide(BigInteger.valueOf(baseTarget));
	}
	
	/**
	 * calculate scoop value . 
	 */
	public int calculateScoop(byte[] genSig , long height){
		ByteBuffer buf = ByteBuffer.allocate(32 + 8);
		buf.put(genSig);
		buf.putLong(height);
		Shabal256 sha = new  Shabal256();
		sha.update(buf.array());
		byte[] hashVal = sha.digest();
		BigInteger hashNum = new BigInteger(1, hashVal);
		return hashNum.mod(BigInteger.valueOf(MiningPlot.SCOOPS_PER_PLOT)).intValue();
	}
	
	/**
	 * how to calculate a generator Block signature . 
	 * by last blog generatorSignature and lastGeneratorId
	 */
	@Override
	public byte[] calculateGeneratorSignature(byte[] lastGenSig, long lastGenId) {
		
		ByteBuffer gensigBuf = ByteBuffer.allocate(32 + 8);
		gensigBuf.put(lastGenSig);
		gensigBuf.putLong(lastGenId);
		
		Shabal256 md = new Shabal256();
		md.update(gensigBuf.array());
		return md.digest();
	}
	
}
