package school;

public class Aufgabe {
	
	private String bezeichnung;
	private double be, gewichtung;
	private final String BE = "BE";
	private final String GEWICHTUNG = "Gewichtung";
	public final static String CONFIG_PATTERN = "BE: (\\d*.*\\d*), Gewichtung: (\\d*.*\\d*)";
	
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
