package school;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextproductionExercise implements ExerciseInterface {

	private String name;
	private double pointsContent, pointsLanguage, weighting;
	public final static String CONTENT = "Inhalt";
	public final static String LANGUAGE = "Sprache";
	public final static String WEIGHTING = NormalExercise.WEIGHTING;
	public final static String SHORT_KEY = "T";
	private final static String CONFIG_PATTERN = "Inhalt: (\\d*.*\\d*), Sprache: (\\d*.*\\d*), Gewichtung: (\\d*.*\\d*)";
	private final static Pattern PATTERN = Pattern.compile(CONFIG_PATTERN);

	public TextproductionExercise(String name, double pointsContent, double pointsLanguage) {
		// Call another constructor
		this(name, pointsContent, pointsLanguage, 1.0);
	}

	public TextproductionExercise(String name, double pointsContent, double pointsLanguage, double weighting) {
		setName(name);
		setPointsContent(pointsContent);
		setPointsLanguage(pointsLanguage);
		setWeighting(weighting);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPointsContent() {
		return this.pointsContent;
	}

	public void setPointsContent(double pointsContent) {
		this.pointsContent = pointsContent;
	}

	public double getPointsLanguage() {
		return this.pointsLanguage;
	}

	public void setPointsLanguage(double pointsLanguage) {
		this.pointsLanguage = pointsLanguage;
	}

	@Override
	public double getWeighting() {
		return this.weighting;
	}

	public void setWeighting(double weighting) {
		this.weighting = weighting;
	}
	
	public String getConfig() {
		return "Inhalt: " + this.pointsContent + ", Sprache: " + this.pointsLanguage + ", Gewichtung: " + this.weighting;
	}
	
	public int getType() {
		return TYPE_TEXTPRODUCTION_EXERCISE;
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

		TextproductionExercise tp = new TextproductionExercise(name, 0, 0);
		
		Matcher m = PATTERN.matcher(text);
		
		if (m.matches()) {
			if (m.groupCount() == 3) {
				try {
					double content = Double.parseDouble(m.group(1));
					double language = Double.parseDouble(m.group(2));
					double weighting = Double.parseDouble(m.group(3));
					
					tp.setPointsContent(content);
					tp.setPointsLanguage(language);
					tp.setWeighting(weighting);
					return tp;
				} catch (NumberFormatException e) {
					//Notenberechnung_GUI.updateLog("Bitte nur Zahlen eingeben","red");
					return null;
				}								
			} else {
				//Notenberechnung_GUI.updateLogwindow("Fehler beim Erstellen der Aufagben im *.xlsx", "red");
				return null;
			}							
		} else {
			return null;
		}	
	}
	
	public String getFirstParam() {
		String s = String.valueOf(getPointsContent());
		return s;
	}

	public String getSecondParam() {
		String s = String.valueOf(getPointsLanguage());
		return s;
	}

	public String getThirdParam() {
		String s = String.valueOf(getWeighting());
		return s;
	}

	@Override
	public String getTypeShortName() {
		return SHORT_KEY;
	}
}