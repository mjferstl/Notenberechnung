package school;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schulklasse {
	
	private List<Schueler> schulklasse = new ArrayList<Schueler>();
	private String klassenName;
	
	public Schulklasse() {
		this.klassenName = "Klasse";
	}
	
	public Schulklasse(String klassenName) {
		this.klassenName = klassenName;
	}
	
	public void addSchueler(Schueler s) {
		this.schulklasse.add(s);
	}
	
	public int getSize() {
		return this.schulklasse.size();
	}
	
	public List<Schueler> getSchueler() {
		return schulklasse;
	}
	
	public boolean isEmpty() {
		if (this.schulklasse.size() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public Error readKlassenliste(String path_to_file) {
		
		Error err = new Error();
		
		try {
			Scanner sc = new Scanner(new File(path_to_file));
			Pattern p = Pattern.compile("(\\w*.?\\s*-?\\w*.?)(,?\\s+)(\\w*\\s*-?\\w*)");
			String line, firstname, lastname;
			
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				Matcher m = p.matcher(line);
				if (m.matches()) {
					firstname = m.group(1).trim();
					lastname = m.group(3).trim();
					Schueler schueler = new Schueler(lastname,firstname);
					addSchueler(schueler);
				}
				else {
					sc.close();
					err.setErrorId(10);
					err.setErrorMsg("Schülerliste falsch formattiert. Trennzeichen zw. Nach -und Vorname müssen Tabs oder Kommas sein.");
					return err;
				}				
			}
			sc.close();
			err.setErrorId(0);
			err.setErrorMsg("Klassenliste erfolgreich eingelesen");
			return err;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// File not found --> Errorcode 100
			err.setErrorId(100);
			err.setErrorMsg("Datei mit Klassenliste nicht gefunden");
			return err;
		}
	}
	
	public void setKlassenname(String klassenName) {
		this.klassenName = klassenName;
	}
	
	public String getKlassenname() {
		return this.klassenName;
	}

}
