package extras;

import org.eclipse.jdt.annotation.NonNull;

public class Error {
	
	/*
	 * Error flags
	 */
	
	/**
	 * Integer which represents a successful execution of a task.
	 * Use this for indication that everything went fine.
	 */
	public static final int SUCCESS = 0;
	
	/**
	 * Integer which represents a warning.
	 * Use this e.g. to indicate that a task has been executed but minor problems have been detected
	 */
	public static final int WARNING = 10;
	
	/**
	 * Integer which represents an error.
	 * Use this for problems like: file does not exist, file cannot be read, task can't be executed
	 */
	public static final int ERROR = 100;
	
	/**
	 * Integer which represents a fatal error. 
	 * A restart of the program may be possible if this error occurs
	 */
	public static final int FATAL_ERROR = 1000;
	
	/**
	 * Integer indicating the error level.
	 * Examples for error levels: Error.SUCCESS, Error.WARNING, Error.ERROR, Error.FATAL_ERROR
	 */
	private int errorLevel;
	
	/**
	 * 
	 */
	private String message;
	
	/**
	 * Constructor with no arguments.
	 * The error level is set to Error.SUCCESS and the message is ""
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public Error() {
		setErrorLevel(SUCCESS);
		setMessage("");
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorLevel Integer indicating the error level. Examples: Error.SUCCESS, Error.WARNING, Error.ERROR, Error.FATAL_ERROR
	 * @param message String containing optional 
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public Error(int errorLevel, String message) {
		setErrorLevel(errorLevel);
		setMessage(message);
	}

	public int getErrorLevel() {
		return this.errorLevel;
	}

	public void setErrorLevel(int errorLevel) {
		this.errorLevel = errorLevel;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(@NonNull String message) {
		this.message = message;
	}
}