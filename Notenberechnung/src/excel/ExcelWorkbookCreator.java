package excel;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import extras.Chart;
import extras.ExcelSheetFunctions;
import gui.IF_Log;
import school.ExerciseInterface;
import school.NormalExercise;
import school.SchoolClass;
import school.Student;
import school.TextproductionExercise;

public class ExcelWorkbookCreator {

	private final int startRow = 4; // funefte Zeile

	private final String DIAMETER = "\u2300";

	private int totalNotenColumnindex, notenBereicheEndeColumnIndex, totalPointsColumnIndex;
	private String totalPointsFormula = "";
	private List<String> resultColumns = new ArrayList<String>();

	private SchoolClass schoolClass;
	private List<ExerciseInterface> exerciseList;
	private Sheet sheet;

	private UpdatePublisher parent;

	/**
	 * Interface providing methods to publish information to the calling object.
	 * Methods: printUpdate
	 * 
	 * @author Mathias
	 * @date 04.10.2020
	 * @version 1.0
	 */
	public interface UpdatePublisher {
		void printUpdate(String message, int logLevel);

		Shell getShell();
	}

	public ExcelWorkbookCreator(UpdatePublisher parent, SchoolClass schoolClass, List<ExerciseInterface> exercises) {
		setSchoolClass(schoolClass);
		setExerciseList(exercises);

		if (parent instanceof UpdatePublisher) {
			this.parent = parent;
		} else {
			throw new RuntimeException(
					"The class " + parent.toString() + " needs to implement ExcelWorkbookCreator.UpdatePublisher");
		}
	}

	public SchoolClass getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(SchoolClass schoolClass) {
		this.schoolClass = schoolClass;
	}

	public List<ExerciseInterface> getExerciseList() {
		return exerciseList;
	}

	public void setExerciseList(List<ExerciseInterface> exerciseList) {
		this.exerciseList = exerciseList;
	}

	/**
	 * Create the Excel-file
	 */
	public void createXlsxFile() {

		if (getSchoolClass() == null || getSchoolClass().isEmpty()) {
			printLogMessage("Keine Klassenliste ausgewählt...", IF_Log.LOG_ERROR);
		} else if (getExerciseList().isEmpty()) {
			printLogMessage("Keine Aufgaben angelegt...", IF_Log.LOG_ERROR);
		} else {
			// remove all current item from the lists and formulas
			resultColumns.clear();
			totalPointsFormula = "";

			Workbook workbook = new XSSFWorkbook();
			this.sheet = workbook.createSheet(getSchoolClass().getClassName());

			for (int i = 0; i < startRow; i++) {
				sheet.createRow(i);
			}

			int rowNum = startRow - 1;
			int columnIndex = 1;

			// print the names of all students
			printSchoolClass(sheet, rowNum, columnIndex);
			columnIndex += ExcelStudent.getHeaderLength();

			// add columns for displaying the grade
			addGradesColumn(sheet, rowNum, columnIndex);
			columnIndex += 3;

			// Alle angelegten Aufgaben einfuegen
			columnIndex = printExercises(exerciseList, startRow, columnIndex);

			// Berechnung der Gesamtpunktzahl
			addTotalScore(sheet, startRow, columnIndex);
			columnIndex += 2;

			// Uebersicht ueber alle Noten
			addGrading(sheet, startRow, columnIndex);

			// Berechnung der GesamtNoten
			addCalculationGrades(sheet, startRow, columnIndex);

			// Diagramm hizufuegen
			addGradingChart(sheet, startRow, columnIndex);

			// Resize all columns to fit the content size
			for (int i = 0; i < 6; i++) {
				sheet.autoSizeColumn(i);
			}

			// Select a filename to save the file
			saveWorkbook(workbook);
		}
	}

	private void printSchoolClass(Sheet sheet, int startRow, int startColumn) {

		ExcelStudent.printHeader(sheet, startRow, startColumn);

		startRow++;

		// borders
		ExcelSheetFunctions
				.setRegionBorderThin(new CellRangeAddress(startRow, startRow + getSchoolClass().getSize() - 1,
						startColumn, startColumn + ExcelStudent.getHeaderLength() - 1), sheet);

		int temp_rowNum = startRow;
		for (Student schueler : getSchoolClass().getStudentList()) {
			ExcelSheetFunctions.setCellText(sheet, temp_rowNum, startColumn, schueler.getSurname());
			ExcelSheetFunctions.setCellText(sheet, temp_rowNum, startColumn + 1, schueler.getFirstName());
			temp_rowNum++;
		}

		ExcelSchoolClass excelSchoolClass = new ExcelSchoolClass(getSchoolClass());
		excelSchoolClass.printSchoolClass(sheet, startRow, startColumn);
	}

	private void addGradesColumn(Sheet sheet, int startRow, int startColumn) {

		CellStyle boldCenteredStyle = CellStyles.getCellStyleBoldCentered(sheet);

		// grades
		ExcelSheetFunctions.setCellText(sheet, startRow, startColumn, "  Noten  ");
		sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, startColumn, startColumn + 2));
		sheet.getRow(startRow).getCell(startColumn).setCellStyle(boldCenteredStyle);
		ExcelSheetFunctions
				.setRegionBorderThin(new CellRangeAddress(startRow - 2, startRow, startColumn, startColumn + 2), sheet);
		startRow++;

		// set top border for cell below class list
		// when using the last cell of the class, the conditional formatting gets lost
		RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(startRow + getSchoolClass().getSize(),
				startRow + getSchoolClass().getSize(), startColumn, startColumn + 2), sheet);

		this.totalNotenColumnindex = startColumn + 1;
	}

	private int printExercises(List<ExerciseInterface> exerciseList, int startRow, int startColumn) {

		int column = startColumn;
		int numRows = getSchoolClass().getSize();
		ExcelStdReturn returnValue;

		for (ExerciseInterface exercise : exerciseList) {
			if (exercise != null) {
				switch (exercise.getType()) {
				case ExerciseInterface.TYPE_NORMAL_EXERCISE:
					returnValue = ExcelExercisePrinter.printNormalExercise(sheet, (NormalExercise) exercise, startRow,
							startColumn, numRows);
					column += ExcelExercisePrinter.COLUMN_RANGE_NORMAL_EXERCISE;
					break;
				case ExerciseInterface.TYPE_TEXTPRODUCTION_EXERCISE:
					returnValue = ExcelExercisePrinter.printTextproductionExercise(sheet,
							(TextproductionExercise) exercise, startRow, column, numRows);
					column += ExcelExercisePrinter.COLUMN_RANGE_TEXTPRODUCTION_EXERCISE;
					break;
				default:
					returnValue = null;
				}

				if (returnValue != null) {
					totalPointsFormula = totalPointsFormula + returnValue.getFormula() + "+";
					resultColumns.add(returnValue.getColumnName());
				}
			}
		}

		// return the next free column
		return column;
	}

	/**
	 * insert formulas for calculating the total scores in the excel file
	 * 
	 * @param sheet: excel sheet
	 */
	private void addTotalScore(Sheet sheet, int startRow, int startColumn) {

		CellStyle boldStyle = CellStyles.getCellStyleBold(sheet);
		CellStyle boldCenteredStyle = CellStyles.getCellStyleBoldCentered(sheet);

		int rowIdx = startRow - 3;
		String string;

		ExcelSheetFunctions.setCellText(sheet, rowIdx, startColumn, "Gesamt");
		sheet.getRow(rowIdx).getCell(startColumn).setCellStyle(boldCenteredStyle);

		// increment row index
		rowIdx += 2;

		String formula = totalPointsFormula.substring(0, totalPointsFormula.length() - 1);
		ExcelSheetFunctions.setCellTextAsFormula(sheet, rowIdx, startColumn, formula);
		sheet.getRow(rowIdx).getCell(startColumn).setCellStyle(boldStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowIdx - 2, rowIdx, startColumn, startColumn),
				sheet);

		// increment row index
		rowIdx++;

		// borders
		ExcelSheetFunctions.setRegionBorderThin(
				new CellRangeAddress(rowIdx, rowIdx + getSchoolClass().getSize() - 1, startColumn, startColumn), sheet);

		for (int i = 0; i < getSchoolClass().getSize(); i++) {
			string = "";
			for (int j = 0; j < resultColumns.size(); j++) {
				string = string + resultColumns.get(j) + (rowIdx + 1) + "+";
			}
			string = string.substring(0, string.length() - 1);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowIdx, startColumn, string);
			rowIdx++;
		}

		totalPointsColumnIndex = startColumn;
	}

	/**
	 * add formulas for calculation the grades of all pupils
	 * 
	 * @param sheet: excel sheet
	 */
	private void addCalculationGrades(Sheet sheet, int startRow, int startColumn) {

		List<String> excelColumnNames = ExcelSheetFunctions.getExcelColumnNames();

		String formulaNote, formulaPlus, formulaMinus, aktCell;

		for (int i = 0; i < getSchoolClass().getSize(); i++) {

			aktCell = excelColumnNames.get(totalPointsColumnIndex) + startRow + 1;

			formulaNote = "IF(NOT(ISNUMBER(" + aktCell + ")),\"\",";
			// Ausgangsbasis fuer die + und - Berechnung
			formulaPlus = formulaNote + "IF(OR(";
			formulaMinus = formulaNote + "IF(OR(";

			for (int j = 1; j <= 6; j++) {
				formulaNote = formulaNote + "IF(" + aktCell + ">=$" + excelColumnNames.get(notenBereicheEndeColumnIndex)
						+ "$" + (startRow + j) + "," + j + ",";
				formulaPlus = formulaPlus + aktCell + "=$" + excelColumnNames.get(notenBereicheEndeColumnIndex - 2)
						+ "$" + (startRow + j) + ",";
				formulaMinus = formulaMinus + aktCell + "=$" + excelColumnNames.get(notenBereicheEndeColumnIndex) + "$"
						+ (startRow + j) + ",";
			}
			formulaNote = formulaNote + "\"\")))))))";
			formulaPlus = formulaPlus.substring(0, formulaPlus.length() - 1) + "),\"+\",\"\"))";
			formulaMinus = formulaMinus.substring(0, formulaMinus.length() - 1) + "),\"-\",\"\"))";

			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i, totalNotenColumnindex, formulaNote);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i, totalNotenColumnindex - 1, formulaPlus);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i, totalNotenColumnindex + 1, formulaMinus);
		}

		/*
		 * conditional formatting for the pupils grades 1 = green, 3 = orange, 6 = red
		 */
		SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
		ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingColorScaleRule();
		ColorScaleFormatting clrFmt = rule1.getColorScaleFormatting();
		assertEquals(3, clrFmt.getNumControlPoints());
		String[] colors = { "00b034", "ffc000", "ff0000" };
		assertEquals(3, clrFmt.getColors().length);
		assertEquals(3, clrFmt.getThresholds().length);

		for (int c = 0; c < 3; c++) {
			((ExtendedColor) clrFmt.getColors()[c]).setARGBHex(colors[c]);
		}

		clrFmt.getThresholds()[0].setRangeType(RangeType.NUMBER);
		clrFmt.getThresholds()[0].setValue(1d);
		clrFmt.getThresholds()[1].setRangeType(RangeType.NUMBER);
		clrFmt.getThresholds()[1].setValue(3d);
		clrFmt.getThresholds()[2].setRangeType(RangeType.NUMBER);
		clrFmt.getThresholds()[2].setValue(6d);

		CellRangeAddress[] regions = {
				CellRangeAddress.valueOf(excelColumnNames.get(totalNotenColumnindex) + (startRow + 1) + ":"
						+ excelColumnNames.get(totalNotenColumnindex) + (startRow + getSchoolClass().getSize())) };
		sheetCF.addConditionalFormatting(regions, rule1);

		// count the amount of all grades
		String formula;
		for (int i = 1; i <= 6; i++) {
			formula = "COUNTIF(" + excelColumnNames.get(totalNotenColumnindex) + (startRow + 1) + ":"
					+ excelColumnNames.get(totalNotenColumnindex) + (startRow + getSchoolClass().getSize()) + ","
					+ excelColumnNames.get(notenBereicheEndeColumnIndex + 2) + (startRow + i) + ")";
			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i - 1, notenBereicheEndeColumnIndex + 3,
					formula);
		}
	}

	/**
	 * add a grading overview of all pupils
	 * 
	 * @param sheet: excel sheet
	 */
	private void addGrading(Sheet sheet, int startRow, int startColumn) {

		List<String> excelColumnNames = ExcelSheetFunctions.getExcelColumnNames();

		CellStyle leftAlignStyle = CellStyles.getCellStyleLeftAlign(sheet);
		CellStyle centeredStyle = CellStyles.getCellStyleCentered(sheet);
		CellStyle rightAlignStyle = CellStyles.getCellStyleRightAlign(sheet);
		CellStyle boldCenteredStyle = CellStyles.getCellStyleBoldCentered(sheet);

		int rowIdx = startRow - 1;

		int pointsMaxColumn = startColumn;
		int pointsMinColumn = startColumn + 2;
		int percentColumn = startColumn + 3;
		int gradesColumn = startColumn + 4;
		int countGradesColumn = startColumn + 5;

		// headers
		ExcelSheetFunctions.setCellText(sheet, rowIdx, pointsMaxColumn, "Punktebereich");
		sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, pointsMaxColumn, pointsMinColumn));
		sheet.setColumnWidth(pointsMaxColumn + 1, 3 * 256); // Minus-Zeichen
		ExcelSheetFunctions.setCellText(sheet, rowIdx, percentColumn, "Prozent");
		sheet.setColumnWidth(percentColumn, 10 * 256);
		ExcelSheetFunctions.setCellText(sheet, rowIdx, gradesColumn, "Note");
		sheet.setColumnWidth(gradesColumn, 10 * 256);
		ExcelSheetFunctions.setCellText(sheet, rowIdx, countGradesColumn, "Anzahl");

		sheet.getRow(rowIdx).getCell(pointsMaxColumn).setCellStyle(boldCenteredStyle);
		sheet.getRow(rowIdx).getCell(percentColumn).setCellStyle(boldCenteredStyle);
		sheet.getRow(rowIdx).getCell(gradesColumn).setCellStyle(boldCenteredStyle);
		sheet.getRow(rowIdx).getCell(countGradesColumn).setCellStyle(boldCenteredStyle);

		this.notenBereicheEndeColumnIndex = pointsMinColumn;

		CellRangeAddress cra = new CellRangeAddress(rowIdx, rowIdx, startColumn, startColumn + 5);
		ExcelSheetFunctions.setRegionBorderThin(cra, sheet);

		// increment row index
		rowIdx++;

		String[] percentageFormulas = { "87.5", "75.0", "62.5", "50.0", "100/3", "0" };
		String formula;
		String cellNameTotalPoints = excelColumnNames.get(totalPointsColumnIndex) + startRow;

		// Noten
		for (int i = 1; i <= 6; i++) {
			ExcelSheetFunctions.setCellText(sheet, rowIdx + i - 1, gradesColumn, i);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowIdx + i - 1, percentColumn, percentageFormulas[i - 1]);
			if (i == 1) {
				formula = cellNameTotalPoints;
			} else {
				formula = "ROUNDDOWN(" + excelColumnNames.get(percentColumn) + (rowIdx + i - 1) + "*"
						+ cellNameTotalPoints + "/100*2,0)/2-0.5";
			}
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowIdx + i - 1, pointsMaxColumn, formula);
			sheet.getRow(rowIdx + i - 1).getCell(pointsMaxColumn).setCellStyle(rightAlignStyle);

			ExcelSheetFunctions.setCellText(sheet, rowIdx + i - 1, pointsMaxColumn + 1, "-");
			sheet.getRow(rowIdx + i - 1).getCell(pointsMaxColumn + 1).setCellStyle(centeredStyle);

			formula = "ROUNDDOWN(" + excelColumnNames.get(percentColumn) + (rowIdx + i) + "*" + cellNameTotalPoints
					+ "/100*2,0)/2";
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowIdx + i - 1, pointsMinColumn, formula);
			sheet.getRow(rowIdx + i - 1).getCell(pointsMinColumn).setCellStyle(leftAlignStyle);
		}

		// borders
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowIdx, rowIdx + 5, startColumn, startColumn + 5),
				sheet);

		// average grade
		formula = "(";
		for (int i = 1; i < 7; i++) {
			formula = formula + excelColumnNames.get(countGradesColumn) + (rowIdx + i) + "*"
					+ excelColumnNames.get(gradesColumn) + (rowIdx + i) + "+";
		}
		formula = formula.substring(0, formula.length() - 1);
		formula = formula + ")/SUM(" + excelColumnNames.get(countGradesColumn) + (rowIdx + 1) + ":"
				+ excelColumnNames.get(countGradesColumn) + (rowIdx + 6) + ")";
		ExcelSheetFunctions.setCellTextAsFormula(sheet, rowIdx + 6, countGradesColumn, formula);
		ExcelSheetFunctions.setCellText(sheet, rowIdx + 6, countGradesColumn - 1, DIAMETER);
		sheet.getRow(rowIdx + 6).getCell(countGradesColumn - 1).setCellStyle(rightAlignStyle);

		// percentage of grades of 5 and 6
		formula = "SUM(" + excelColumnNames.get(countGradesColumn) + (rowIdx + 5) + ":"
				+ excelColumnNames.get(countGradesColumn) + (rowIdx + 6) + ")";
		formula = formula + "/SUM(" + excelColumnNames.get(countGradesColumn) + (rowIdx + 1) + ":"
				+ excelColumnNames.get(countGradesColumn) + (rowIdx + 6) + ")*100";
		ExcelSheetFunctions.setCellTextAsFormula(sheet, rowIdx + 7, countGradesColumn, formula);
		ExcelSheetFunctions.setCellText(sheet, rowIdx + 7, countGradesColumn - 1, "% 5 u. 6");
		sheet.getRow(rowIdx + 7).getCell(countGradesColumn - 1).setCellStyle(rightAlignStyle);
	}

	/**
	 * add chart to the excel file showing the grading of all pupils
	 * 
	 */
	private void addGradingChart(Sheet sheet, int startRow, int startColumn) {
		Chart gradeChart = new Chart(sheet);
		gradeChart.setChartTitle("Notenverteilung");
		gradeChart.setxAxisLabel("Note");
		gradeChart.setyAxisLabel("Anzahl");
		gradeChart.setXData(new CellRangeAddress(startRow, startRow + 5, startColumn + 4, startColumn + 4));
		gradeChart.setYData(new CellRangeAddress(startRow, startRow + 5, startColumn + 5, startColumn + 5));
		gradeChart.setPositionInSheet(startRow + 9, startRow + 19, startColumn, startColumn + 6);
		gradeChart.createChart();
	}

	/**
	 * save the current workbook to an *.xlsx file
	 * 
	 * @param workbook: object of type Workbook
	 */
	private void saveWorkbook(Workbook workbook) {
		FileDialog fsd = new FileDialog(parent.getShell(), SWT.SAVE);
		fsd.setText("Speichern unter...");
		String[] filterExt = { "*.xlsx" };
		fsd.setFilterExtensions(filterExt);
		fsd.setOverwrite(true);
		fsd.setFileName(getSchoolClass().getClassName());
		String selected = fsd.open();

		if (selected == null) {
			printLogMessage("Keine Datei ausgewählt. Liste wurde nicht gespeichert.", IF_Log.LOG_ERROR);
		} else {
			// Write the output to a file
			FileOutputStream fileOut;
			try {
				fileOut = new FileOutputStream(selected);
				workbook.write(fileOut);
				workbook.close();
				fileOut.close();
				printLogMessage("Excel-Datei erfolgreich erstellt", IF_Log.LOG_SUCCESS);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				printLogMessage("Die Datei konnte nicht erstellt werden", IF_Log.LOG_ERROR);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void printLogMessage(String message, int logLevel) {
		if (parent != null) {
			parent.printUpdate(message, logLevel);
		} else {
			throw new RuntimeException("The variable \"parent\" has not been initialized.");
		}
	}

}
