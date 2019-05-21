package school;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gui.Notenberechnung_GUI;

public class Aufgabe {
	
	private String bezeichnung;
	private double be, gewichtung;
	private final String BE = "BE";
	private final String GEWICHTUNG = "Gewichtung";
	private final static String CONFIG_PATTERN = "BE: (\\d*.*\\d*), Gewichtung: (\\d*.*\\d*)";
	private final static Pattern pa = Pattern.compile(CONFIG_PATTERN);
	
	public Aufgabe(String Bezeichnung, double BE) {
		this.bezeichnung = Bezeichnung;
		this.be = BE;
		this.gewichtung = 1.0;
	}
	
	public Aufgabe(String Bezeichnung, double BE, double Gewichtung) {
		this.bezeichnung = Bezeichnung;
		this.be = BE;
		this.gewichtung = Gewichtung;
	}
	
	public static Aufgabe parseTextToAufgabe(String text) {
		
		Matcher m;
		Aufgabe a = new Aufgabe("Aufgabe", 0);
		
		m = pa.matcher(text);
		
		if (m.matches()) {
			if (m.groupCount() == 2) {
				try {
					double be = Double.parseDouble(m.group(1));
					double gewichtung = Double.parseDouble(m.group(2));
					a.setBe(be);
					a.setGewichtung(gewichtung);
					return a;
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

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public double getBe() {
		return be;
	}

	public void setBe(double be) {
		this.be = be;
	}

	public double getGewichtung() {
		return gewichtung;
	}

	public void setGewichtung(double gewichtung) {
		this.gewichtung = gewichtung;
	}
	
	public String getConfig() {
		return "BE: " + this.be + ", Gewichtung: " + this.gewichtung;
	}
	
	public String getType() {
		return "Aufgabe";
	}
	
	public String getNameBe() {
		return BE;
	}
	
	public String getNameGewichtung() {
		return GEWICHTUNG;
	}
	
}
