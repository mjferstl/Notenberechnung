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
import utils.Validator;

public class SchoolClass {

	private List<Student> studentList = new ArrayList<>();
	private String className;
	private final Pattern studentNamePattern = Pattern.compile("(\\w+[^\\t,]*)(\\s*(,|\\t)+\\s*)+(\\w+[^\\t]*)");

	/**
	 * Constructor with no arguments. The name of the class is set to "Klasse"
	 * 
	 * @author Mathias Ferstl
	 */
	public SchoolClass() {
		this("Klasse");
	}

	/**
	 * Constructor with the class name as argument.
	 * 
	 * @param className Name of the school class. E.g. Klasse 9b
	 * 
	 * @author Mathias Ferstl
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
	 */
	public void addStudent(@NonNull Student student) {
		this.studentList.add(student);
	}

	/**
	 * Add a list of students to the student list of this class
	 * 
	 * @param studentList list of student objects
	 * 
	 * @author Mathias Ferstl
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
	 */
	public int getSize() {
		return getStudentList().size();
	}

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
	 */
	public boolean hasNoStudents() {
		return getStudentList().isEmpty();
	}

	/**
	 * Method to load a list of students from a file.
	 * 
	 * @param file File, which contains the student names
	 * @return Error which contains information about the process
	 * 
	 * @author Mathias Ferstl
	 */
	public Error loadStudentsFromFile(File file) {
		if (file == null) {
			throw new NullPointerException("Invalid argument null for file.");
		} else if (!file.exists() || !file.canRead()) {
			return new Error(Error.ERROR,
					String.format("Die Datei \"%s\" kann nicht gelesen werden", file.getAbsolutePath()));
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
	 */
	private Error parseStudentsFile(File file) {
		Error err = new Error();

		// create a new list
		this.studentList = new ArrayList<>();

		// Get the file name without the file extension
		String fileName = file.getName();
		String[] fileParts = fileName.split("\\.");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < fileParts.length - 1; i++) {
			stringBuilder.append(fileParts[i]);
		}
		setClassName(stringBuilder.toString());

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
	public Error readClassList(@NonNull String pathToFile) {
		return parseStudentsFile(new File(pathToFile));

	}

	public void setClassName(@NonNull String className) {
		if (Validator.isValidString(className)) {
			this.className = className;
		} else {
			throw new IllegalArgumentException(String.format("Invalid value for className: \"%s\". The name must not be null or an empty String.", className));
		}
	}


	public String getClassName() {
		return this.className;
	}

	/**
	 * Set the students list. The current student list will be overwritten.
	 * 
	 * @param studentList list of Student objets
	 * 
	 * @author Mathias Ferstl
	 */
	public void setStudentList(List<Student> studentList) {
		this.studentList = studentList;
	}

}
