package school;

public class Schueler {

	private String Vorname, Nachname;
	public final static String VORNAME = "Vorname";
	public final static String NACHNAME = "Nachname";
	
	public Schueler(String Vorname, String Nachname) {
		this.Vorname = Vorname;
		this.Nachname = Nachname;
	}

	public String getVorname() {
		return Vorname;
	}

	public void setVorname(String vorname) {
		Vorname = vorname;
	}

	public String getNachname() {
		return Nachname;
	}

	public void setNachname(String nachname) {
		Nachname = nachname;
	}
	
	public String getName() {
		return Nachname + ' ' + Vorname;
	}
	
}
