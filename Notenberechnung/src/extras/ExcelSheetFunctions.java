package extras;

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

}
