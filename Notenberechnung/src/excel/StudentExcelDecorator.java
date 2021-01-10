package excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import extras.ExcelSheetFunctions;
import org.eclipse.jdt.annotation.NonNull;
import school.Student;

public class StudentExcelDecorator {
	
	private final static String[] HEADER = { "Nachname", "Vorname" };
	
	private final Student student;
	
	public StudentExcelDecorator(@NonNull Student student) {
		this.student = student;
	}
	
	public static void printHeader(Sheet sheet, int startRow, int startColumn) {
		
		CellStyle boldStyle = CellStyles.getCellStyleBold(sheet);
		
		for (int i = 0; i < HEADER.length; i++) {
			ExcelSheetFunctions.setCellText(sheet, startRow, startColumn + i, HEADER[i]);
			sheet.getRow(startRow).getCell(startColumn + i).setCellStyle(boldStyle);
		}
		ExcelSheetFunctions.setRegionBorderThin(
				new CellRangeAddress(startRow-2, startRow, startColumn, startColumn + HEADER.length - 1), sheet);
	}
	
	public void printStudent(Sheet sheet, int startRow, int startColumn) {
		ExcelSheetFunctions.setCellText(sheet, startRow, startColumn, this.student.getSurname());
		ExcelSheetFunctions.setCellText(sheet, startRow, startColumn + 1, this.student.getFirstName());
	}
	
	public static int getHeaderLength() {
		return HEADER.length;
	}

}
