package school.exercise;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NormalExercise extends Exercise {

	public final static String BE = "BE";
	public final static String WEIGHTING = "Gewichtung";
	public final static String SHORT_KEY = "A";
	private final static String CONFIG_PATTERN = "BE: (\\d*.*\\d*), Gewichtung: (\\d*.*\\d*)";
	private final static Pattern PATTERN = Pattern.compile(CONFIG_PATTERN);
	
	public NormalExercise(String name, NormalExerciseEvaluation evaluation) {
		super(name, evaluation);
	}
	
	public static NormalExercise parseTextToAufgabe(String name, String text) {

		Matcher m = PATTERN.matcher(text);
		
		if (m.matches() && m.groupCount() == 2) {
			try {
				double be = Double.parseDouble(m.group(1));
				double weighting = Double.parseDouble(m.group(2));

				return new NormalExercise(name, new NormalExerciseEvaluation(weighting, be));
			} catch (NumberFormatException e) {
				throw new NumberFormatException(String.format("Cannot convert the Strings %s and %s to double", m.group(1), m.group(2)));
			}
		} else {
			throw new RuntimeException(String.format("The String \"%s\" does not represent a TextproductionExercise in the format: %s", text, PATTERN.pattern()));
		}		
	}

	@Override
	public String getConfigString() {
		return "BE: " + this.getEvaluation().getBE() + ", Gewichtung: " + this.getEvaluation().getWeighting();
	}

	@Override
	public ExerciseType getExerciseType() {
		return ExerciseType.NORMAL_TASK;
	}
	
	public String getNameBe() {
		return BE;
	}
	
	public String getNameWeighting() {
		return WEIGHTING;
	}

	public String getKey() {
		return SHORT_KEY;
	}

	@Override
	public NormalExerciseEvaluation getEvaluation() {
		return (NormalExerciseEvaluation) super.getEvaluation();
	}
}
