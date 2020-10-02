package school;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gui.Notenberechnung_GUI;

public class NormalExercise implements ExerciseInterface {
	
	private String name;
	private double be, weighting;
	public final static String BE = "BE";
	public final static String WEIGHTING = "Gewichtung";
	public final static String SHORT_KEY = "A";
	private final static String CONFIG_PATTERN = "BE: (\\d*.*\\d*), Gewichtung: (\\d*.*\\d*)";
	private final static Pattern PATTERN = Pattern.compile(CONFIG_PATTERN);
	
	public NormalExercise(String Bezeichnung, double BE) {
		// Call another constructor
		this(Bezeichnung, BE, 1.0);
	}
	
	public NormalExercise(String name, double BE, double weighting) {
		this.name = name;
		this.be = BE;
		this.weighting = weighting;
	}
	
	public static NormalExercise parseTextToAufgabe(String name, String text) {
		
		NormalExercise a = new NormalExercise(name, 0);
		
		Matcher m = PATTERN.matcher(text);
		
		if (m.matches()) {
			if (m.groupCount() == 2) {
				try {
					double be = Double.parseDouble(m.group(1));
					double weighting = Double.parseDouble(m.group(2));
					
					a.setBe(be);
					a.setWeighting(weighting);
					a.setName(name);
					return a;
				} catch (NumberFormatException e) {
					//Notenberechnung_GUI.updateLogwindow("Bitte nur Zahlen eingeben","red");
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBe() {
		return this.be;
	}

	public void setBe(double be) {
		this.be = be;
	}

	public void setWeighting(double weighting) {
		this.weighting = weighting;
	}
	
	@Override
	public double getWeighting() {
		return this.weighting;
	}
	
	public String getConfig() {
		return "BE: " + this.be + ", Gewichtung: " + this.weighting;
	}
	
	public int getType() {
		return TYPE_NORMAL_EXERCISE;
	}
	
	public String getNameBe() {
		return BE;
	}
	
	public String getNameWeighting() {
		return WEIGHTING;
	}

	public String getFirstParam() {
		String s = String.valueOf(getBe());
		return s;
	}

	public String getSecondParam() {
		String s = String.valueOf(getWeighting());
		return s;
	}

	public String getThirdParam() {
		return "";
	}

	@Override
	public String getTypeShortName() {
		return SHORT_KEY;
	}
}
