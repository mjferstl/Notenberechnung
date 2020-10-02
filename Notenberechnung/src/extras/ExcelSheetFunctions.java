package extras;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

public class ExcelSheetFunctions {
	
	
	/**
	 * set text in an excel cell
	 * 
	 * @param sheet: excel sheet
	 * @param row: row index of the cell
	 * @param col: column index of the cell
	 * @param text: cell content
	 */
	public static void setCellText(Sheet sheet, int row, int col, String text) {
		Row r = sheet.getRow(row);
		Cell cell = r.getCell(col);
		if (cell == null)
			cell = r.createCell(col);
		cell.setCellValue(text);
	}

	/**
	 * set text in an excel cell
	 * 
	 * @param sheet: excel sheet
	 * @param row: row index of the cell
	 * @param col: column index of the cell
	 * @param value: number as the cells content
	 */
	public static void setCellText(Sheet sheet, int row, int col, double value) {
		Row r = sheet.getRow(row);
		Cell cell = r.getCell(col);
		if (cell == null)
			cell = r.createCell(col);
		cell.setCellValue(value);
	}

	/**
	 * set text in an excel cell
	 * 
	 * @param sheet: excel sheet
	 * @param row: row index of the cell
	 * @param col: column index of the cell
	 * @param formula: excel formula to be inserted
	 */
	public static void setCellTextAsFormula(Sheet sheet, int row, int col, String formula) {
		Row r = sheet.getRow(row);
		Cell cell = r.getCell(col);
		if (cell == null)
			cell = r.createCell(col);
		cell.setCellFormula(formula);
	}
	
	/**
	 * set a thin border around a specified cell-range
	 * 
	 * @param region: object of type CellRangeAdress
	 * @param sheet: excel sheet
	 */
	public static void setRegionBorderThin(CellRangeAddress region, Sheet sheet) {
		RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
	}
	
	
	public static List<String> getExcelColumnNames() {

		List<String> columnNames = new ArrayList<>();
		final String[] alphabet = { "", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
				"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

		// Bezeichnungen der Spalten in MS-Excel
		if (columnNames.size() == 0) {
			for (int i = 0; i < alphabet.length; i++) {
				for (int j = 1; j < alphabet.length; j++) {
					if (i == 0) {
						columnNames.add(alphabet[j] + alphabet[i]);
					} else {
						columnNames.add(alphabet[i] + alphabet[j]);
					}
				}
			}
		}
		
		return columnNames;
	}

}
