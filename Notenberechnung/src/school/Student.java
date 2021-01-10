package school;

import org.eclipse.jdt.annotation.NonNull;

public class Student {

	private final String firstName;
	private final String surname;
	
	public Student(@NonNull String firstName, @NonNull String surname) {
		this.firstName = firstName;
		this.surname = surname;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getSurname() {
		return this.surname;
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
			throw new IllegalArgumentException("Cannot compare " + obj + " with an object of type <Student>.");
		}
	}
	
}