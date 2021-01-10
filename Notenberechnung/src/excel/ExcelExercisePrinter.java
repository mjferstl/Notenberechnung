package excel;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import extras.ExcelSheetFunctions;
import school.exercise.NormalExercise;
import school.exercise.TextproductionExercise;

public class ExcelExercisePrinter {

	public static final int COLUMN_RANGE_NORMAL_EXERCISE = 2;
	public static final int COLUMN_RANGE_TEXTPRODUCTION_EXERCISE = 3;

	public static ExcelStdReturn printTextproductionExercise(Sheet sheet, TextproductionExercise textproductionExercise,
			int startRow, int startColumn, int numRows) {

		CellStyle boldCenteredStyle = CellStyles.getCellStyleBoldCentered(sheet);
		CellStyle centeredStyle = CellStyles.getCellStyleCentered(sheet);

		List<String> excelColumnNames = ExcelSheetFunctions.getExcelColumnNames();

		int rowNum = startRow - 3;

		// header
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn, textproductionExercise.getName());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, startColumn, startColumn + 2));
		sheet.getRow(rowNum).getCell(startColumn).setCellStyle(boldCenteredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn, startColumn + 2),
				sheet);

		rowNum++;

		// legend of all columns
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn, textproductionExercise.getNameContent());
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn + 1, textproductionExercise.getNameLanguage());
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn + 2, textproductionExercise.getNameWeighting());
		sheet.getRow(rowNum).getCell(startColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(startColumn + 1).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(startColumn + 2).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn, startColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn + 1, startColumn + 1),
				sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn + 2, startColumn + 2),
				sheet);

		sheet.setColumnWidth(startColumn, 10 * 256);
		sheet.setColumnWidth(startColumn + 1, 10 * 256);
		sheet.setColumnWidth(startColumn + 2, 12 * 256);

		rowNum++;

		// coefficients
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn, textproductionExercise.getEvaluation().getPointsContent());
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn + 1, textproductionExercise.getEvaluation().getPointsLanguage());
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn + 2, textproductionExercise.getEvaluation().getWeighting());
		sheet.getRow(rowNum).getCell(startColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(startColumn + 1).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(startColumn + 2).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn, startColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn + 1, startColumn + 1),
				sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn + 2, startColumn + 2),
				sheet);

		rowNum++;

		// borders
		ExcelSheetFunctions.setRegionBorderThin(
				new CellRangeAddress(rowNum, rowNum + numRows - 1, startColumn, startColumn + 2), sheet);

		for (int i = 0; i < numRows; i++) {
			String formula = "(" + excelColumnNames.get(startColumn) + (rowNum + 1) + "+"
					+ excelColumnNames.get(startColumn + 1) + (rowNum + 1) + ")*" + excelColumnNames.get(startColumn + 2)
					+ startRow;
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum, startColumn + 2, formula);
			rowNum++;
		}

		String totalPointsFormula = "(" + excelColumnNames.get(startColumn) + (startRow) + "+"
				+ excelColumnNames.get(startColumn + 1) + (startRow) + ")*" + excelColumnNames.get(startColumn + 2)
				+ (startRow);
		String exercisePointsColumnName = excelColumnNames.get(startColumn + 2);

		return new ExcelStdReturn(totalPointsFormula, exercisePointsColumnName);
	}

	public static ExcelStdReturn printNormalExercise(Sheet sheet, NormalExercise exercise, int startRow,
			int startColumn, int numRows) {

		CellStyle boldCenteredStyle = CellStyles.getCellStyleBoldCentered(sheet);
		CellStyle centeredStyle = CellStyles.getCellStyleCentered(sheet);

		List<String> excelColumnNames = ExcelSheetFunctions.getExcelColumnNames();

		int rowNum = startRow - 3;

		// header
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn, exercise.getName());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, startColumn, startColumn + 1));
		sheet.getRow(rowNum).getCell(startColumn).setCellStyle(boldCenteredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn, startColumn + 1),
				sheet);

		rowNum++;

		// legend of all columns
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn, exercise.getNameBe());
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn + 1, exercise.getNameWeighting());
		sheet.getRow(rowNum).getCell(startColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(startColumn + 1).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn, startColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn + 1, startColumn + 1),
				sheet);

		sheet.setColumnWidth(startColumn, 10 * 256);
		sheet.setColumnWidth(startColumn + 1, 12 * 256);

		rowNum++;

		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn, exercise.getEvaluation().getBE());
		ExcelSheetFunctions.setCellText(sheet, rowNum, startColumn + 1, exercise.getEvaluation().getWeighting());
		sheet.getRow(rowNum).getCell(startColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(startColumn + 1).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn, startColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, startColumn + 1, startColumn + 1),
				sheet);

		rowNum++;

		// borders
		ExcelSheetFunctions.setRegionBorderThin(
				new CellRangeAddress(rowNum, rowNum + numRows - 1, startColumn, startColumn + 1), sheet);

		for (int i = 0; i < numRows; i++) {
			String formula = excelColumnNames.get(startColumn) + (rowNum + 1) + "*"
					+ excelColumnNames.get(startColumn + 1) + startRow;
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum, startColumn + 1, formula);
			rowNum++;
		}

		String exercisePointsColumnName = excelColumnNames.get(startColumn + 1);
		String formula = excelColumnNames.get(startColumn) + (startRow) + "*" + excelColumnNames.get(startColumn + 1)
				+ (startRow);

		return new ExcelStdReturn(formula, exercisePointsColumnName);
	}

}
