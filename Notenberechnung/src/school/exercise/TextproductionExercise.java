package school.exercise;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextproductionExercise extends Exercise {

	public final static String CONTENT = "Inhalt";
	public final static String LANGUAGE = "Sprache";
	public final static String SHORT_KEY = "T";


	private final static String CONFIG_PATTERN = String.format("%s: (\\d*.*\\d*), %s: (\\d*.*\\d*), %s: (\\d*.*\\d*)", CONTENT, LANGUAGE, WEIGHTING);
	private final static Pattern PATTERN = Pattern.compile(CONFIG_PATTERN);

	public TextproductionExercise(String name, TextProductionEvaluation evaluation) {
		super(name);
		setEvaluation(evaluation);
	}

	@Override
	public String getConfigString() {
		return "Inhalt: " + this.getEvaluation().getPointsContent() + ", Sprache: " + this.getEvaluation().getPointsLanguage() + ", Gewichtung: " + this.getEvaluation().getWeighting();
	}

	@Override
	public String getKey() {
		return SHORT_KEY;
	}

	public String getNameContent() {
		return CONTENT;
	}
	
	public String getNameLanguage() {
		return LANGUAGE;
	}
	
	public String getNameWeighting() {
		return WEIGHTING;
	}

	public static TextproductionExercise parseTextToTextproduktion(String name, String text) {
		
		Matcher m = PATTERN.matcher(text);
		
		if (m.matches() && m.groupCount() == 3) {
			try {
				double content = Double.parseDouble(m.group(1));
				double language = Double.parseDouble(m.group(2));
				double weighting = Double.parseDouble(m.group(3));

				TextProductionEvaluation evaluation = new TextProductionEvaluation(weighting, content, language);
				evaluation.setWeighting(weighting);
				evaluation.setPointsContent(content);
				evaluation.setPointsLanguage(language);

				return new TextproductionExercise(name, evaluation);
			} catch (NumberFormatException e) {
				throw new NumberFormatException(String.format("Cannot convert the Strings %s, %s and %s to double", m.group(1), m.group(2), m.group(3)));
			}
		} else {
			String msg = String.format("The String \"%s\" does not represent a TextproductionExercise in the format: %s", text, PATTERN.pattern());
			throw new IllegalArgumentException(msg);
		}	
	}

	@Override
	public TextProductionEvaluation getEvaluation() {
		return (TextProductionEvaluation) super.getEvaluation();
	}

	@Override
	public ExerciseType getExerciseType() {
		return ExerciseType.TEXT_PRODUCTION;
	}
}