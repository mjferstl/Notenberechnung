package excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;

public class CellStyles {
	
	public static Font getHeaderFont(Sheet sheet) {
		Font headerFont = sheet.getWorkbook().createFont();
		headerFont.setBold(true);
		headerFont.setCharSet(Font.ANSI_CHARSET);
		headerFont.setFontHeightInPoints((short) 12);
		return headerFont;
	}
	
	public static CellStyle getCellStyleBold(Sheet sheet) {
		CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
		boldStyle.setFont(getHeaderFont(sheet));
		return boldStyle;
	}
	
	public static CellStyle getCellStyleNoColor(Sheet sheet) {
		CellStyle noColor = sheet.getWorkbook().createCellStyle();
		noColor = sheet.getWorkbook().createCellStyle();
		noColor.setFillPattern(FillPatternType.NO_FILL);
		return noColor;
	}
	
	public static CellStyle getCellStyleBoldCentered(Sheet sheet) {
		CellStyle boldCenteredStyle = getCellStyleBold(sheet);
		boldCenteredStyle.setFont(getHeaderFont(sheet));
		boldCenteredStyle.setAlignment(HorizontalAlignment.CENTER);
		return boldCenteredStyle;
	}
	
	public static CellStyle getCellStyleCentered(Sheet sheet) {
		CellStyle centeredStyle = sheet.getWorkbook().createCellStyle();
		centeredStyle.setAlignment(HorizontalAlignment.CENTER);
		return centeredStyle;
	}
	
	public static CellStyle getCellStyleLeftAlign(Sheet sheet) {
		CellStyle leftAlignCellStyle = sheet.getWorkbook().createCellStyle();
		leftAlignCellStyle.setAlignment(HorizontalAlignment.LEFT);
		return leftAlignCellStyle;
	}
	
	public static CellStyle getCellStyleRightAlign(Sheet sheet) {
		CellStyle rightAlignStyle = sheet.getWorkbook().createCellStyle();
		rightAlignStyle.setAlignment(HorizontalAlignment.RIGHT);
		return rightAlignStyle;
	}
}
