package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class handling the log messages. Log messages are written to the log file.
 * This makes it possible to get further information about what was ongoing in
 * the program e.g. before it crashed
 * 
 * @author Mathias
 * @date 13.10.2020
 * @version 1.0
 */
public class LogFileManager {

	private final SimpleDateFormat logSdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
	private final String logFileName = "Notenberechnung.log";

	private File logFile = new File(logFileName);

	public LogFileManager() {

	}

	/**
	 * Get the absolute path of the file, where the log messages are saved This file
	 * can help to investigate errors etc.
	 * 
	 * @return String containing the absolute path to the log file
	 * 
	 * @author Mathias Ferstl
	 * @date 13.10.2020
	 * @version 1.0
	 */
	public String getLogFilePath() {
		if ((logFile != null) && (this.logFile.exists())) {
			return this.logFile.getAbsolutePath();
		} else {
			return null;
		}
	}

	/**
	 * Method to add a message to the programs log file
	 * 
	 * @param logMessage message to be written to the log file
	 * 
	 * @author Mathias Ferstl
	 * @date 13.10.2020
	 * @version 1.0
	 */
	public void writeLogMessage(String logMessage) {
		addMessageToLog(logMessage);
	}

	/**
	 * Method to append a message to the log file The log file will be saved after
	 * writing the message
	 * 
	 * @param logMessage message to be written to the log file
	 * 
	 * @author Mathias Ferstl
	 * @date 13.10.2020
	 * @version 1.0
	 */
	private void addMessageToLog(String logMessage) {
		File logFile = new File(logFileName);
		try {
			FileOutputStream fileStream = new FileOutputStream(logFile, true);
			OutputStreamWriter logFileWriter = new OutputStreamWriter(fileStream, "UTF-8");

			logFileWriter.write(createLogMessage(logMessage));

			logFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to create a String in the specific log file format. The current date
	 * and time are added to the message and newline characters are added at the end
	 * of the String.
	 * 
	 * @param logMessage log message containing the information about what's ongoing
	 *                   in the program
	 * 
	 * @return String containing the message in the log file format
	 * 
	 * @author Mathias Ferstl
	 * @date 13.10.2020
	 * @version 1.0
	 */
	private String createLogMessage(String logMessage) {
		return logSdf.format(new Date()) + " - " + logMessage + "\r\n";
	}

}
