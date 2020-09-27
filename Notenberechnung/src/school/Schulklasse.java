package school;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extras.Error;
import utils.TextFileReader;

public class Schulklasse {
	
	private List<Schueler> schulklasse = new ArrayList<Schueler>();
	private String klassenName;
	private final Pattern studentNamePattern = Pattern.compile("(\\w+[^\\t]*)(,?\\t+)(\\w+[^\\t]*)");
	
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
	
	public Error readKlassenliste(String pathToFile) {
		
		Error err = new Error();
		
		// create a new list 
		this.schulklasse = new ArrayList<>();
		
		// Read the file line by line
		List<String> fileContentList;
		try {
			fileContentList = new TextFileReader(pathToFile).readFile();
		} catch (IOException e1) {
			err.setErrorId(10);
			err.setErrorMsg(String.format("Fehler beim Einlesen der Datei \"%s\". Trennzeichen zw. Vor -und Nachname müssen Tabs (und Kommas) sein.", pathToFile));
			return err;
		}
		
		// Parse student names
		String firstname, lastname;
		for (String line : fileContentList) {
			Matcher m = studentNamePattern.matcher(line);
			if (m.find()) {
				firstname = m.group(1).trim();
				lastname = m.group(3).trim();
				Schueler schueler = new Schueler(lastname,firstname);
				addSchueler(schueler);
			}
			else {
				err.setErrorId(10);
				err.setErrorMsg(String.format("Fehler beim Auslesen des Namens aus \"%s\". Trennzeichen zw. Vor -und Nachname müssen Tabs (und Kommas) sein.", line));
				return err;
			}
			
		}
		
		// No Error
		err.setErrorId(0);
		err.setErrorMsg("Klassenliste mit " + this.schulklasse.size() + " Schülernnamen erfolgreich eingelesen");
		return err;
	}
	
	public void setKlassenname(String klassenName) {
		this.klassenName = klassenName;
	}
	
	public String getKlassenname() {
		return this.klassenName;
	}

}
