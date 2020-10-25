package log;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

public class Log implements IF_Log{
	
	private LogFileManager logFileManager;
	private SWTLog swtLog;
	
	public Log() {
		initLogFileManager();
	}
	
	public Log(Composite parent) {
		initLogFileManager();
		createSwtLog(parent);
	}
	
	public void addMessage(String message) {
		this.addMessage(message, LOG_INFO);
	}
	
	public void addMessage(String message, int level) {
		if (this.logFileManager != null) {
			writeLogMessageToFile(message);
		}
		
		if (this.getSwtLog() != null) {
			this.swtLog.addLogMessage(message, getLogColor(level));
		}
	}
	
	public void createSwtLog(Composite parent) {
		this.swtLog = new SWTLog(parent);
	} 
	
	private void initLogFileManager() {
		this.logFileManager = new LogFileManager();
	}
	
	private LogFileManager getLogFileManager() {
		return this.logFileManager;
	}
	
	private void writeLogMessageToFile(String logMessage) {
		if (this.getLogFileManager() != null) {
			this.getLogFileManager().writeLogMessage(logMessage);
		}
	}
	
	public SWTLog getSwtLog() {
		return this.swtLog;
	}
	
	private Color getLogColor(int logLevel) {
		
		Color logColor;
		
		switch (logLevel) {
		case IF_Log.LOG_ERROR:
			logColor = IF_Log.RED;
			break;
		case IF_Log.LOG_SUCCESS:
			logColor = IF_Log.GREEN;
			break;
		case IF_Log.LOG_INFO:
			logColor = IF_Log.BLACK;
			break;
		default:
			logColor = IF_Log.BLACK;
		}
		
		return logColor;
	} 

}
