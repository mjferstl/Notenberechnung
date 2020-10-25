package utils;

import org.eclipse.swt.widgets.Shell;

/**
 * Interface providing methods to publish information to the calling object.
 * Methods: publishUpdate, getShell
 * 
 * @author Mathias Ferstl
 * @date 25.10.2020
 * @version 1.0
 */
public interface UpdatePublisher {
	void publishUpdate(String message, int logLevel);

	Shell getShell();
}
