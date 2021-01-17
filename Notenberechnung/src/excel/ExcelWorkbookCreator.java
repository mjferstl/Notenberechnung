package excel;

import extras.Chart;
import extras.ExcelSheetFunctions;
import log.LogType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import school.*;
import school.exercise.Exercise;
import school.exercise.NormalExercise;
import school.exercise.TextproductionExercise;
import utils.UpdatePublisher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelWorkbookCreator {

	private final int startRow = 4; // funefte Zeile
	private final int NOT_SET = -1;
	private int columnIndexGrades = NOT_SET;
	private int rowIndexFirstGrade = NOT_SET;

	private final String DIAMETER = "\u2300";

	private int totalNotenColumnindex, notenBereicheEndeColumnIndex, totalPointsColumnIndex;
	private StringBuilder totalPointsFormulaBuilder = new StringBuilder();
	private final List<String> resultColumns = new ArrayList<>();

	private SchoolClass schoolClass;
	private List<Exercise> exerciseList;
	private Sheet sheet;

	private final UpdatePublisher parent;

	public ExcelWorkbookCreator(UpdatePublisher parent, SchoolClass schoolClass, List<Exercise> exercises) {

		if (parent == null) throw new NullPointerException("The argument parent is null. It needs to be an object, which implements the interface UpdatePublisher");

		this.parent = parent;
		setSchoolClass(schoolClass);
		setExerciseList(exercises);
	}

	public SchoolClass getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(@NonNull SchoolClass schoolClass) {
		this.schoolClass = schoolClass;
	}

	public List<Exercise> getExerciseList() {
		return exerciseList;
	}

	public void setExerciseList(List<Exercise> exerciseList) {
		this.exerciseList = exerciseList;
	}

	/**
	 * Create the Excel-file
	 */
	public File createXlsxFile() {
		
		File xlsxFile = null;

		if (getSchoolClass() == null || getSchoolClass().hasNoStudents()) {
			printLogMessage("Keine Klassenliste ausgewählt...", LogType.ERROR);
		} else if (getExerciseList().isEmpty()) {
			printLogMessage("Keine Aufgaben angelegt...", LogType.ERROR);
		} else {
			// remove all current item from the lists and formulas
			resultColumns.clear();
			totalPointsFormulaBuilder = new StringBuilder();

			Workbook workbook = new XSSFWorkbook();
			this.sheet = workbook.createSheet(getSchoolClass().getClassName());

			for (int i = 0; i < startRow; i++) {
				sheet.createRow(i);
			}

			int rowNum = startRow - 1;
			int columnIndex = 1;

			// print the names of all students
			printSchoolClass(sheet, rowNum, columnIndex);
			columnIndex += StudentExcelDecorator.getHeaderLength();

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
			addCalculationGrades(sheet, startRow+1, columnIndex);

			// Diagramm hizufuegen
			addGradingChart(sheet, startRow, columnIndex);

			// Resize all columns to fit the content size
			for (int i = 0; i < 6; i++) {
				sheet.autoSizeColumn(i);
			}

			// Select a filename to save the file
			xlsxFile = saveWorkbook(workbook);
		}
		
		return xlsxFile;
	}

	private void printSchoolClass(Sheet sheet, int startRow, int startColumn) {

		StudentExcelDecorator.printHeader(sheet, startRow, startColumn);

		startRow++;

		// borders
		ExcelSheetFunctions
				.setRegionBorderThin(new CellRangeAddress(startRow, startRow + getSchoolClass().getSize() - 1,
						startColumn, startColumn + StudentExcelDecorator.getHeaderLength() - 1), sheet);

		int temp_rowNum = startRow;
		for (Student student : getSchoolClass().getStudentList()) {
			ExcelSheetFunctions.setCellText(sheet, temp_rowNum, startColumn, student.getSurname());
			ExcelSheetFunctions.setCellText(sheet, temp_rowNum, startColumn + 1, student.getFirstName());
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

	private int printExercises(List<Exercise> exerciseList, int startRow, int startColumn) {

		int column = startColumn;
		int numRows = getSchoolClass().getSize();
		ExcelStdReturn returnValue;

		for (Exercise exercise : exerciseList) {
			if (exercise != null) {
				switch (exercise.getExerciseType()) {
					case NORMAL_TASK: {
						returnValue = ExcelExercisePrinter.printNormalExercise(sheet, (NormalExercise) exercise, startRow,
								column, numRows);
						column += ExcelExercisePrinter.COLUMN_RANGE_NORMAL_EXERCISE;
						break;
					}
					case TEXT_PRODUCTION: {
						returnValue = ExcelExercisePrinter.printTextproductionExercise(sheet,
								(TextproductionExercise) exercise, startRow, column, numRows);
						column += ExcelExercisePrinter.COLUMN_RANGE_TEXTPRODUCTION_EXERCISE;
						break;
					}
					default: returnValue = null;
				}

				if (returnValue != null) {
					totalPointsFormulaBuilder.append(returnValue.getFormula()).append("+");
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

		String totalPointsFormula = totalPointsFormulaBuilder.toString();
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
			StringBuilder stringBuilder = new StringBuilder();
			for (String resultColumn : resultColumns) {
				stringBuilder.append(resultColumn)
						.append(rowIdx + 1)
						.append("+");
			}
			string = stringBuilder.toString();
			string = string.substring(0, string.length() - 1);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowIdx, startColumn, string);
			rowIdx++;
		}

		totalPointsColumnIndex = startColumn;
	}

	/**
	 * add formulas for the calculation of the students grades
	 * 
	 * @param sheet: excel sheet
	 * @param startRow: 
	 */
	private void addCalculationGrades(Sheet sheet, int startRow, int startColumn) {

		List<String> excelColumnNames = ExcelSheetFunctions.getExcelColumnNames();

		String formulaGrade, formulaPlus, formulaMinus, totalScoreCellName;

		for (int i = 0; i < getSchoolClass().getSize(); i++) {

			// Name of the cell, which contains the total score. E.g. "L41"
			totalScoreCellName = excelColumnNames.get(totalPointsColumnIndex) + (startRow + i);

			formulaGrade = "IF(NOT(ISNUMBER(" + totalScoreCellName + ")),\"\",";
			// Ausgangsbasis fuer die + und - Berechnung
			formulaPlus = formulaGrade + "IF(OR(";
			formulaMinus = formulaGrade + "IF(OR(";
			
			// Loop over all grades
			for (int j = 0; j < 6; j++) {
				
				// If the indices for the row and column of the grade cells (1,2,3,4,5,6) are set, then the cells are referenced.
				// Otherwise the grades are printed hardcoded into the formula
				if ((this.columnIndexGrades != NOT_SET) && (this.rowIndexFirstGrade != NOT_SET)) {
					String columnNameGrades = excelColumnNames.get(this.columnIndexGrades);

					formulaGrade = formulaGrade + "IF(" + totalScoreCellName + ">=$" + excelColumnNames.get(notenBereicheEndeColumnIndex)
					+ "$" + (startRow + j) + ",$" + columnNameGrades + "$" + (this.rowIndexFirstGrade+j+1) + ",";
				} else {
					formulaGrade = formulaGrade + "IF(" + totalScoreCellName + ">=$" + excelColumnNames.get(notenBereicheEndeColumnIndex)
					+ "$" + (startRow + j) + "," + (j+1) + ",";
				}
				
				// Formulas for displaying a plus or minus sign
				formulaPlus = formulaPlus + totalScoreCellName + "=$" + excelColumnNames.get(notenBereicheEndeColumnIndex - 2)
						+ "$" + (startRow + j) + ",";
				formulaMinus = formulaMinus + totalScoreCellName + "=$" + excelColumnNames.get(notenBereicheEndeColumnIndex) + "$"
						+ (startRow + j) + ",";
			}
			formulaGrade = formulaGrade + "\"\")))))))";
			formulaPlus = formulaPlus.substring(0, formulaPlus.length() - 1) + "),\"+\",\"\"))";
			formulaMinus = formulaMinus.substring(0, formulaMinus.length() - 1) + "),\"-\",\"\"))";

			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i -1, totalNotenColumnindex, formulaGrade);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i -1, totalNotenColumnindex - 1, formulaPlus);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i -1, totalNotenColumnindex + 1, formulaMinus);
		}

		/*
		 * conditional formatting for the pupils grades 1 = green, 3 = orange, 6 = red
		 */
		SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
		ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingColorScaleRule();
		ColorScaleFormatting clrFmt = rule1.getColorScaleFormatting();
		//assertEquals(3, clrFmt.getNumControlPoints());
		String[] colors = { "00b034", "ffc000", "ff0000" };
		//assertEquals(3, clrFmt.getColors().length);
		//assertEquals(3, clrFmt.getThresholds().length);

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
				CellRangeAddress.valueOf(excelColumnNames.get(totalNotenColumnindex) + (startRow) + ":"
						+ excelColumnNames.get(totalNotenColumnindex) + (startRow -1 + getSchoolClass().getSize())) };
		sheetCF.addConditionalFormatting(regions, rule1);

		// count the amount of all grades
		String formula;
		for (int i = 1; i <= 6; i++) {
			formula = "COUNTIF(" + excelColumnNames.get(totalNotenColumnindex) + (startRow) + ":"
					+ excelColumnNames.get(totalNotenColumnindex) + (startRow -1 + getSchoolClass().getSize()) + ","
					+ excelColumnNames.get(notenBereicheEndeColumnIndex + 2) + (startRow + i -1) + ")";
			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i - 1 -1, notenBereicheEndeColumnIndex + 3,
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

		int pointsMinColumn = startColumn + 2;
		int percentColumn = startColumn + 3;
		int gradesColumn = startColumn + 4;
		this.columnIndexGrades = gradesColumn;
		int countGradesColumn = startColumn + 5;

		// headers
		ExcelSheetFunctions.setCellText(sheet, rowIdx, startColumn, "Punktebereich");
		sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, startColumn, pointsMinColumn));
		sheet.setColumnWidth(startColumn + 1, 3 * 256); // Minus-Zeichen
		ExcelSheetFunctions.setCellText(sheet, rowIdx, percentColumn, "Prozent");
		sheet.setColumnWidth(percentColumn, 10 * 256);
		ExcelSheetFunctions.setCellText(sheet, rowIdx, gradesColumn, "Note");
		sheet.setColumnWidth(gradesColumn, 10 * 256);
		ExcelSheetFunctions.setCellText(sheet, rowIdx, countGradesColumn, "Anzahl");

		sheet.getRow(rowIdx).getCell(startColumn).setCellStyle(boldCenteredStyle);
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
		this.rowIndexFirstGrade = rowIdx;
		for (int i = 1; i <= 6; i++) {
			int rowNumber = rowIdx + i - 1;
		 	ExcelSheetFunctions.setCellText(sheet, rowNumber, gradesColumn, i);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNumber, percentColumn, percentageFormulas[i - 1]);
			if (i == 1) {
				formula = cellNameTotalPoints;
			} else {
				formula = "ROUNDDOWN(" + excelColumnNames.get(percentColumn) + rowNumber + "*"
						+ cellNameTotalPoints + "/100*2,0)/2-0.5";
			}
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNumber, startColumn, formula);
			sheet.getRow(rowNumber).getCell(startColumn).setCellStyle(rightAlignStyle);

			ExcelSheetFunctions.setCellText(sheet, rowNumber, startColumn + 1, "-");
			sheet.getRow(rowNumber).getCell(startColumn + 1).setCellStyle(centeredStyle);

			formula = "ROUNDDOWN(" + excelColumnNames.get(percentColumn) + (rowIdx + i) + "*" + cellNameTotalPoints
					+ "/100*2,0)/2";
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNumber, pointsMinColumn, formula);
			sheet.getRow(rowNumber).getCell(pointsMinColumn).setCellStyle(leftAlignStyle);
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
	private File saveWorkbook(Workbook workbook) {
		FileDialog fsd = new FileDialog(parent.getShell(), SWT.SAVE);
		fsd.setText("Speichern unter...");
		String[] filterExt = { "*.xlsx" };
		fsd.setFilterExtensions(filterExt);
		fsd.setOverwrite(true);
		fsd.setFileName(getSchoolClass().getClassName());
		String selected = fsd.open();
		
		File excelFile = null;

		if (selected == null) {
			printLogMessage("Keine Datei ausgewählt. Liste wurde nicht gespeichert.", LogType.ERROR);
		} else {
			// Write the output to a file
			FileOutputStream fileOut;
			try {
				fileOut = new FileOutputStream(selected);
				workbook.write(fileOut);
				workbook.close();
				fileOut.close();
				printLogMessage("Excel-Datei erfolgreich erstellt", LogType.INFO);
				excelFile = new File(selected);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				printLogMessage("Die Datei konnte nicht erstellt werden", LogType.ERROR);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return excelFile;
	}

	private void printLogMessage(String message, LogType logType) {
		if (parent != null) {
			parent.publishUpdate(message, logType);
		} else {
			throw new RuntimeException("The variable \"parent\" has not been initialized.");
		}
	}

}
