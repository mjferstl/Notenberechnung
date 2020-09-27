package school;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gui.Notenberechnung_GUI;

public class Textproduktion implements Aufgabentyp{

	private String Bezeichnung;
	private double punkteInhalt, punkteSprache, gewichtung;
	public final static String CONTENT = "Inhalt";
	public final static String LANGUAGE = "Sprache";
	public final static String GEWICHTUNG = Aufgabe.WEIGHTING;
	public final static String TYPE = "T";
	private final static String CONFIG_PATTERN = "Inhalt: (\\d*.*\\d*), Sprache: (\\d*.*\\d*), Gewichtung: (\\d*.*\\d*)";
	private final static Pattern pt = Pattern.compile(CONFIG_PATTERN);

	public Textproduktion(String Bezeichnung, double punkteInhalt, double punkteSprache) {
		this.Bezeichnung = Bezeichnung;
		this.punkteInhalt = punkteInhalt;
		this.punkteSprache = punkteSprache;
		this.gewichtung = 1.0;
	}

	public Textproduktion(String Bezeichnung, double punkteInhalt, double punkteSprache, double gewichtung) {
		this.Bezeichnung = Bezeichnung;
		this.punkteInhalt = punkteInhalt;
		this.punkteSprache = punkteSprache;
		this.gewichtung = gewichtung;
	}

	public String getBezeichnung() {
		return Bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		Bezeichnung = bezeichnung;
	}

	public double getPunkteInhalt() {
		return punkteInhalt;
	}

	public void setPunkteInhalt(double punkteInhalt) {
		this.punkteInhalt = punkteInhalt;
	}

	public double getPunkteSprache() {
		return punkteSprache;
	}

	public void setPunkteSprache(double punkteSprache) {
		this.punkteSprache = punkteSprache;
	}

	public double getGewichtung() {
		return gewichtung;
	}

	public void setGewichtung(double gewichtung) {
		this.gewichtung = gewichtung;
	}
	
	public String getConfig() {
		return "Inhalt: " + this.punkteInhalt + ", Sprache: " + this.punkteSprache + ", Gewichtung: " + this.gewichtung;
	}
	
	public String getType() {
		return Textproduktion.TYPE;
	}
	
	public String getNameInhalt() {
		return CONTENT;
	}
	
	public String getNameSprache() {
		return LANGUAGE;
	}
	
	public String getNameGewichtung() {
		return GEWICHTUNG;
	}

	public static Textproduktion parseTextToTextproduktion(String bezeichnung, String text) {
		Matcher m;
		Textproduktion tp = new Textproduktion(bezeichnung, 0, 0);
		
		m = pt.matcher(text);
		
		if (m.matches()) {
			if (m.groupCount() == 3) {
				try {
					double inhalt = Double.parseDouble(m.group(1));
					double sprache = Double.parseDouble(m.group(2));
					double gewichtung = Double.parseDouble(m.group(3));
					
					tp.setPunkteInhalt(inhalt);
					tp.setPunkteSprache(sprache);
					tp.setGewichtung(gewichtung);
					return tp;
				} catch (NumberFormatException e) {
					Notenberechnung_GUI.updateLogwindow("Bitte nur Zahlen eingeben","red");
					return null;
				}								
			} else {
				Notenberechnung_GUI.updateLogwindow("Fehler beim Erstellen der Aufagben im *.xlsx", "red");
				return null;
			}							
		} else {
			return null;
		}	
	}
	
	public String getFirstParam() {
		String s = String.valueOf(getPunkteInhalt());
		return s;
	}

	public String getSecondParam() {
		String s = String.valueOf(getPunkteSprache());
		return s;
	}

	public String getThirdParam() {
		String s = String.valueOf(getGewichtung());
		return s;
	}


}