package school;

public class Student {

	private String firstName, surname;
	
	public Student(String firstName, String surname) {
		this.firstName = firstName;
		this.surname = surname;
	}

	public String getFirstName() {
		if (this.firstName == null) {
			return "";
		} else {
			return this.firstName;
		}
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		if (this.surname == null) {
			return "";
		} else {
			return this.surname;
		}
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getName() {
		return getSurname() + ' ' + getFirstName();
	}

	@Override
	public boolean equals(Object obj) {
		// Check if the passed object is an object of this class
		if (obj instanceof Student) {
			// Cast the object
			Student student = (Student) obj;
			
			// Compare the first and last name
			// Return true, if they are equals the ones from the passed object
			return (student.getFirstName().equals(getFirstName())) && (student.getSurname().equals(getSurname()));
			
		} else {
			throw new RuntimeException("Cannot compare " + obj + " with object of type <Student>.");
		}
	}
	
}