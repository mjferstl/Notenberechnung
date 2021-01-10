package gui;

import log.LogType;
import school.exercise.Exercise;

public interface IF_GUI {

	void addTask(Exercise exercise);
	void updateTask(Exercise exercise, int tableIndex);
	void addLogMessage(String text, LogType logType);
	
}
