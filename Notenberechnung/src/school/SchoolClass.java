package school;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;

import extras.Error;
import utils.TextFileReader;

public class SchoolClass {

	private List<Student> studentList = new ArrayList<Student>();
	private String className;
	private final Pattern studentNamePattern = Pattern.compile("(\\w+[^\\t,]*)(\\s*(,|\\t)+\\s*)+(\\w+[^\\t]*)");

	/**
	 * Constructor with no arguments. The name of the class is set to "Klasse"
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public SchoolClass() {
		setClassName("Klasse");
	}

	/**
	 * Constructor with the class name as argument.
	 * 
	 * @param className Name of the school class. E.g. Klasse 9b
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public SchoolClass(@NonNull String className) {
		setClassName(className);
	}

	/**
	 * Constructor with the class name as argument.
	 * 
	 * @param className   Name of the school class. E.g. Klasse 9b
	 * @param studentList list of students, which are in this school class
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public SchoolClass(@NonNull String className, List<Student> studentList) {
		setClassName(className);
		setStudentList(studentList);
	}

	/**
	 * Add a student to the list of students for this class
	 * 
	 * @param student Student to be added
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public void addStudent(Student student) {
		this.studentList.add(student);
	}

	/**
	 * Add a list of students to the student list of this class
	 * 
	 * @param studentList list of student objects
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public void addStudents(List<Student> studentList) {
		for (Student student : studentList) {
			addStudent(student);
		}
	}

	/**
	 * Get the number of students in the class
	 * 
	 * @return number of students
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public int getSize() {
		return getStudentList().size();
	}

	/**
	 * Get a list containing all stundets of the school class
	 * 
	 * @return list of Student objects
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public List<Student> getStudentList() {
		return this.studentList;
	}

	/**
	 * Check if the school class contains students. If no students are stored in
	 * this school class, then it's empty.
	 * 
	 * @return boolean which indicates if the school class contains any students
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public boolean isEmpty() {
		return getStudentList().isEmpty();
	}

	/**
	 * Method to load a list of students from a file.
	 * 
	 * @param file File, which contains the student names
	 * @return Error which contains information about the process
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public Error loadStudentsFromFile(File file) {
		if (file == null) {
			throw new RuntimeException("Invalid argument null for File.");
		} else if (!file.exists() || !file.canRead()) {
			Error err = new Error(Error.ERROR,
					String.format("Die Datei \"%s\" kann nicht gelesen werden", file.getAbsolutePath()));
			return err;
		} else {
			return parseStudentsFile(file);
		}
	}

	/**
	 * Method to parse the students from a file. The file needs to be a text file,
	 * which contains the student names line-by-line in the form: firstname(s) \t
	 * lastname(s)
	 * 
	 * @param file File (text file) which contains the student names line-by-line
	 * @return Error, which contains information about the parsing process
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	private Error parseStudentsFile(File file) {
		Error err = new Error();

		// create a new list
		this.studentList = new ArrayList<>();

		// Get the file name without the file extension
		String fileName = file.getName();
		String[] fileParts = fileName.split("\\.");
		String className = "";
		for (int i = 0; i < fileParts.length - 1; i++) {
			className += fileParts[i];
		}
		setClassName(className);

		// Read the file line by line
		List<String> fileContentList;
		try {
			fileContentList = new TextFileReader(file).readFile();
		} catch (IOException e) {
			err.setErrorLevel(Error.ERROR);
			err.setMessage(
					String.format("Fehler beim Einlesen der Datei \"%s\".\nDie Datei ist möglicherweise nicht lesbar.",
							file.getAbsolutePath()));
			return err;
		}

		// Parse student names
		String firstname, lastname;
		int counter = 0;
		for (String line : fileContentList) {
			counter++;
			Matcher m = studentNamePattern.matcher(line);
			if (m.find()) {
				firstname = m.group(1).trim();
				lastname = m.group(4).trim();

				Student student = new Student(lastname, firstname);
				addStudent(student);
			} else {
				err.setErrorLevel(Error.ERROR);
				String errorMessage = String.format(
						"Fehler beim Auslesen des Namens in Zeile %d: \"%s\".\nAls Trennzeichen zw. Vor -und Nachname bitte Tabs und/oder Kommas verwenden.",
						counter, line);
				err.setMessage(errorMessage);
				return err;
			}
		}

		// No Error
		err.setErrorLevel(0);
		err.setMessage(this.studentList.size() + " Schülernnamen erfolgreich eingelesen");
		return err;
	}

	@Deprecated
	public Error readClassList(String pathToFile) {
		return parseStudentsFile(new File(pathToFile));

	}

	/**
	 * Method to set the name of the class. If the class name is null, then it will
	 * be ignored.
	 * 
	 * @param className Name of the class to be set
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public void setClassName(@NonNull String className) {
		if (className != null) {
			this.className = className;
		}
	}

	/**
	 * Method to get the class name
	 * 
	 * @return name of the class
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Set the students list. The current student list will be overwritten.
	 * 
	 * @param studentList list of Student objets
	 * 
	 * @author Mathias Ferstl
	 * @date 18.10.2020
	 * @version 1.0
	 */
	public void setStudentList(List<Student> studentList) {
		this.studentList = studentList;
	}

}
