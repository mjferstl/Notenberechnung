package excel;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import extras.ExcelSheetFunctions;
import school.NormalExercise;
import school.TextproductionExercise;

public class ExcelExercisePrinter {

	public static final int COLUMN_RANGE_NORMAL_EXERCISE = 2;
	public static final int COLUMN_RANGE_TEXTPRODUCTION_EXERCISE = 3;

	public static ExcelStdReturn printTextproductionExercise(Sheet sheet, TextproductionExercise textproductionExercise,
			int startRow, int startColumn, int numRows) {

		CellStyle boldCenteredStyle = CellStyles.getCellStyleBoldCentered(sheet);
		CellStyle centeredStyle = CellStyles.getCellStyleCentered(sheet);

		List<String> excelColumnNames = ExcelSheetFunctions.getExcelColumnNames();

		int nextColumn = startColumn;
		int rowNum = startRow - 3;

		// header
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, textproductionExercise.getName());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn + 2));
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn + 2),
				sheet);

		rowNum++;

		// legend of all columns
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, textproductionExercise.getNameContent());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 1, textproductionExercise.getNameLanguage());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 2, textproductionExercise.getNameWeighting());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 1).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 2).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 1, nextColumn + 1),
				sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 2, nextColumn + 2),
				sheet);

		sheet.setColumnWidth(nextColumn, 10 * 256);
		sheet.setColumnWidth(nextColumn + 1, 10 * 256);
		sheet.setColumnWidth(nextColumn + 2, 12 * 256);

		rowNum++;

		// coefficients
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, textproductionExercise.getPointsContent());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 1, textproductionExercise.getPointsLanguage());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 2, textproductionExercise.getWeighting());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 1).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 2).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 1, nextColumn + 1),
				sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 2, nextColumn + 2),
				sheet);

		rowNum++;

		// borders
		ExcelSheetFunctions.setRegionBorderThin(
				new CellRangeAddress(rowNum, rowNum + numRows - 1, nextColumn, nextColumn + 2), sheet);

		for (int i = 0; i < numRows; i++) {
			String formula = "(" + excelColumnNames.get(nextColumn) + (rowNum + 1) + "+"
					+ excelColumnNames.get(nextColumn + 1) + (rowNum + 1) + ")*" + excelColumnNames.get(nextColumn + 2)
					+ startRow;
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum, nextColumn + 2, formula);
			rowNum++;
		}

		String totalPointsFormula = "(" + excelColumnNames.get(nextColumn) + (startRow) + "+"
				+ excelColumnNames.get(nextColumn + 1) + (startRow) + ")*" + excelColumnNames.get(nextColumn + 2)
				+ (startRow);
		String exercisePointsColumnName = excelColumnNames.get(nextColumn + 2);

		ExcelStdReturn returnObject = new ExcelStdReturn(totalPointsFormula, exercisePointsColumnName);
		return returnObject;
	}

	public static ExcelStdReturn printNormalExercise(Sheet sheet, NormalExercise exercise, int startRow,
			int startColumn, int numRows) {

		CellStyle boldCenteredStyle = CellStyles.getCellStyleBoldCentered(sheet);
		CellStyle centeredStyle = CellStyles.getCellStyleCentered(sheet);

		List<String> excelColumnNames = ExcelSheetFunctions.getExcelColumnNames();

		int nextColumn = startColumn;
		int rowNum = startRow - 3;

		// header
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, exercise.getName());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn + 1));
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn + 1),
				sheet);

		rowNum++;

		// legend of all columns
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, exercise.getNameBe());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 1, exercise.getNameWeighting());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 1).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 1, nextColumn + 1),
				sheet);

		sheet.setColumnWidth(nextColumn, 10 * 256);
		sheet.setColumnWidth(nextColumn + 1, 12 * 256);

		rowNum++;

		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, exercise.getBe());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 1, exercise.getWeighting());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 1).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 1, nextColumn + 1),
				sheet);

		rowNum++;

		// borders
		ExcelSheetFunctions.setRegionBorderThin(
				new CellRangeAddress(rowNum, rowNum + numRows - 1, nextColumn, nextColumn + 1), sheet);

		for (int i = 0; i < numRows; i++) {
			String formula = excelColumnNames.get(nextColumn) + (rowNum + 1) + "*"
					+ excelColumnNames.get(nextColumn + 1) + startRow;
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum, nextColumn + 1, formula);
			rowNum++;
		}

		String exercisePointsColumnName = excelColumnNames.get(nextColumn + 1);
		String formula = excelColumnNames.get(nextColumn) + (startRow) + "*" + excelColumnNames.get(nextColumn + 1)
				+ (startRow);

		ExcelStdReturn returnObject = new ExcelStdReturn(formula, exercisePointsColumnName);
		return returnObject;
	}

}
