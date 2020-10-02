package school;

public interface ExerciseInterface {
	
	final int TYPE_NORMAL_EXERCISE = 1;
	final int TYPE_TEXTPRODUCTION_EXERCISE = 2;
	
	public int getType();
	public String getTypeShortName();
	
	public String getConfig();
	
	public String getName();
	public double getWeighting();
	
	public String getFirstParam();
	public String getSecondParam();
	public String getThirdParam();
	
}
