package school;

public interface ExerciseInterface {

	ExerciseType getType();
	String getTypeShortName();
	
	String getConfig();
	
	String getName();
	double getWeighting();
	
	String getFirstParam();
	String getSecondParam();
	String getThirdParam();
}
