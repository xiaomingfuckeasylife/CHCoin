package nxt.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * 
 * @author clark
 * 
 * 2017年12月6日 上午11:18:57
 * 
 */
public class BriefLogFormat extends Formatter {
	
	private static ThreadLocal<MessageFormat> messageFormat = new ThreadLocal<MessageFormat>(){
		protected MessageFormat initialValue() {
			return new MessageFormat("ChC : {0,date,yyyy-MM-dd HH:mm:ss} {1}:{2}\n{3}");
		};
	};

	// root log we only need to set this log instance then we are good to go 
	private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger("");
	
	//
	private static BriefLogFormat me = new BriefLogFormat();
	
	// reset new log formaat 
	static void init(){
		Handler[] hArr = log.getHandlers();
		for(int i=0;i<hArr.length;i++){
			hArr[i].setFormatter(me);
		}
	}
	
	// this is the output method of format.
	@Override
	public String format(LogRecord lr) {
		Object[] msgObj = new Object[4];
		msgObj[0] = lr.getMillis();
		msgObj[1] = lr.getLevel();
		msgObj[2] = lr.getMessage();
		Throwable tr = lr.getThrown();
		
		if(tr != null){
			try(PrintWriter writer = new PrintWriter(new StringWriter())){
				tr.printStackTrace(writer);
				msgObj[3] = writer.toString();
			};
		}else{
			msgObj[3] = "";
		}
		
		return messageFormat.get().format(msgObj);
	}
	
	private BriefLogFormat(){}
}
