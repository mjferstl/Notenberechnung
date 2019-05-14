package school;

public class Textproduktion {

	private String Bezeichnung;
	private double punkteInhalt, punkteSprache, gewichtung;
	private final String INHALT = "Inhalt";
	private final String SPRACHE = "Sprache";
	private final String GEWICHTUNG = "Gewichtung";
	public final static String CONFIG_PATTERN = "Inhalt: (\\d*.*\\d*), Sprache: (\\d*.*\\d*), Gewichtung: (\\d*.*\\d*)";

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
		return "Textproduktion";
	}
	
	public String getNameInhalt() {
		return INHALT;
	}
	
	public String getNameSprache() {
		return SPRACHE;
	}
	
	public String getNameGewichtung() {
		return GEWICHTUNG;
	}


}