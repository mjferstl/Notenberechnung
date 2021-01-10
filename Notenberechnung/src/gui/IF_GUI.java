package gui;

import log.LogType;
import school.Exercise;

public interface IF_GUI {

	public void addTask(Exercise exercise);
	public void updateTask(Exercise exercise, int tableIndex);
	public void addLogMessage(String text, LogType logType);
	
}
