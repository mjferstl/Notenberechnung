package log;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * Singleton-Pattern
 */
public class Log implements IF_Log {
	
	private LogFileManager logFileManager;
	private SWTLog swtLog;

	private static Log instance = null;
	
	private Log() {
		initLogFileManager();
	}

	public static Log getInstance() {
		if (instance == null) {
			instance = new Log();
		}
		return instance;
	}

	public void addMessage(String message) {
		this.addMessage(message, LogType.INFO);
	}
	
	public void addMessage(String message, LogType logType) {
		if (this.logFileManager != null) {
			writeLogMessageToFile(message);
		}
		
		if (this.getSwtLog() != null) {
			this.swtLog.addLogMessage(message, getLogColor(logType));
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
	
	private Color getLogColor(LogType logType) {

		return switch (logType) {
			case ERROR -> IF_Log.RED;
			case INFO, WARNING -> IF_Log.BLACK;
		};
	} 

}
