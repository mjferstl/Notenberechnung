package utils;

import log.LogType;
import org.eclipse.swt.widgets.Shell;

/**
 * Interface providing methods to publish information to the calling object.
 * Methods: publishUpdate, getShell
 * 
 * @author Mathias Ferstl
 * @version 1.0
 */
public interface UpdatePublisher {
	void publishUpdate(String message, LogType logType);

	Shell getShell();
}
