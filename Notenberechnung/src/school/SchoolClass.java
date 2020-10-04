package school;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extras.Error;
import utils.TextFileReader;

public class SchoolClass {
	
	private List<Student> studentList = new ArrayList<Student>();
	private String className;
	private final Pattern studentNamePattern = Pattern.compile("(\\w+[^\\t]*)(,?\\t+)(\\w+[^\\t]*)");
	
	public SchoolClass() {
		this.className = "Klasse";
	}
	
	public SchoolClass(String className) {
		this.className = className;
	}
	
	public void addStudent(Student student) {
		this.studentList.add(student);
	}
	
	public int getSize() {
		return this.studentList.size();
	}
	
	public List<Student> getStudentList() {
		return this.studentList;
	}
	
	public boolean isEmpty() {
		if (getStudentList().size() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public Error readClassList(String pathToFile) {
		
		Error err = new Error();
		
		// create a new list 
		this.studentList = new ArrayList<>();
		
		// Get the file name without the file extension
		String fileName = new File(pathToFile).getName();
		String[] fileParts = fileName.split("\\.");
		String className = "";
		for (int i=0; i<fileParts.length-1; i++) {
			className += fileParts[i];
		}
		setClassName(className);
		System.out.println("Class name: " + className);
		
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
				
				Student student = new Student(lastname,firstname);
				addStudent(student);
			}
			else {
				err.setErrorId(10);
				err.setErrorMsg(String.format("Fehler beim Auslesen des Namens aus \"%s\". Trennzeichen zw. Vor -und Nachname müssen Tabs (und Kommas) sein.", line));
				return err;
			}
		}
		
		// No Error
		err.setErrorId(0);
		err.setErrorMsg("Klassenliste mit " + this.studentList.size() + " Schülernnamen erfolgreich eingelesen");
		return err;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getClassName() {
		return this.className;
	}

}
