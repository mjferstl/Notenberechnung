package excel;

import org.apache.poi.ss.usermodel.Sheet;

import school.SchoolClass;
import school.Student;

public class ExcelSchoolClass {
	
	private SchoolClass schoolClass;
	
	public ExcelSchoolClass(SchoolClass schoolClass) {
		setSchoolClass(schoolClass);
	}

	public SchoolClass getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(SchoolClass schoolClass) {
		this.schoolClass = schoolClass;
	}
	
	public void printSchoolClass(Sheet sheet, int startRow, int startColumn) {
		
		for (Student student : getSchoolClass().getStudentList()) {
			StudentExcelDecorator excelStudent = new StudentExcelDecorator(student);
			excelStudent.printStudent(sheet, startRow, startColumn);
			startRow++;
		}
	}
}
