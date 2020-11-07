package gui;

import school.ExerciseInterface;

public interface IF_GUI {

	public void addTask(ExerciseInterface exercise);
	public void updateTask(ExerciseInterface exercise, int tableIndex);
	public void addLogMessage(String text, int logLevel);
	
}
