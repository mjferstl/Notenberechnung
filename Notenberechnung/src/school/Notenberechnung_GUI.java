package school;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class Notenberechnung_GUI {

	protected Shell shell;
	private Text editBezeichnung;
	
	private Label logwindow;
	private Label lblKlasseDatei;
	private Schulklasse klasse;
	
	private static String[] schuelerHeader = {"Nachname","Vorname"};
	private static Table table;
	
	private int nextColumn;
	private final int startRow = 4; // funefte Zeile
	
	private final String[] columnNames = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q"};
	private List<String> resultColumns = new ArrayList<String>();
	private String totalPointsFormula = "";
	private int totalPointsColumnIndex;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Notenberechnung_GUI window = new Notenberechnung_GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 223);
		shell.setText("Erstellen einer Excel-Datei zur Notenauswertung");
		shell.setLayout(new GridLayout(3, false));
		
		Label lblKlassenliste = new Label(shell, SWT.NONE);
		lblKlassenliste.setText("Klassenliste");
		
		lblKlasseDatei = new Label(shell, SWT.NONE);
		lblKlasseDatei.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblKlasseDatei.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		Button btnBrowse = new Button(shell, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(new Frame());
				fd.setTitle("Klassenliste ausw�hlen...");
				fd.setVisible(true);
				if (fd.getFile() == null) {
					updateLogwindow("Keine Datei ausgw�hlt","red");
					lblKlasseDatei.setText("Keine Datei ausgw�hlt");
					lblKlasseDatei.requestLayout();
				} else {
					System.out.println("Datei ausgw�hlt: " + fd.getFile().toString());
					String file_name = fd.getFile().toString();
					String file_path = fd.getDirectory().toString();
					
					lblKlasseDatei.setText(file_name);
					lblKlasseDatei.requestLayout();
					
					updateLogwindow("Klassenliste ausgew�hlt","blue");
					
					klasse = new Schulklasse();
					String path_to_file = file_path + file_name;
					Error error = new Error();
					error = klasse.readKlassenliste(path_to_file);
					
					// update logwindow
					if (error.getErrorId() == 0) {
						updateLogwindow(error.getErrorMsg(),"green");
					} else {
						updateLogwindow(error.getErrorMsg(),"red");
					}	
				}
			}
		});
		btnBrowse.setText("Browse");
		
		Label lblBezeichnung = new Label(shell, SWT.NONE);
		lblBezeichnung.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBezeichnung.setText("Bezeichnung");
		
		editBezeichnung = new Text(shell, SWT.BORDER);
		GridData gd_editBezeichnung = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_editBezeichnung.widthHint = 210;
		editBezeichnung.setLayoutData(gd_editBezeichnung);
		new Label(shell, SWT.NONE);
		
		Label lblAufgaben = new Label(shell, SWT.NONE);
		lblAufgaben.setText("Aufgaben");
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gd_table.heightHint = 25;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		String[] titles = { "Aufgabentyp","Bewertung"};
		System.out.println(titles.length);
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}

		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
//		table.setSize(table.computeSize(SWT.DEFAULT, 200));
		
		Button addTask = new Button(shell, SWT.NONE);
		addTask.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				addAufgabe();
//				addTextproduktion();
				NeueAufgabeDialog na = new NeueAufgabeDialog(shell);
				na.open();
			}
		});
		addTask.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		addTask.setText("     +     ");
		new Label(shell, SWT.NONE);
		
		Button btnExcelErstellen = new Button(shell, SWT.NONE);
		btnExcelErstellen.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		btnExcelErstellen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLogwindow("Excel-Datei wird erstellt...","blue");
				createXlsxFile();
			}
		});
		btnExcelErstellen.setText("Excel erstellen");
		
		logwindow = new Label(shell, SWT.NONE);
		logwindow.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		logwindow.setText(" ");

	}
	
	private void updateLogwindow(String text, String color) {		
		logwindow.setText(text);
		switch (color) {
			case "blue":
				logwindow.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
				break;
			case "red":
				logwindow.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
				break;
			case "green":
				logwindow.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
				break;
			default:
				logwindow.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		}
		logwindow.requestLayout();
	}
	
	private void createXlsxFile() {
		
		if (klasse == null || klasse.isEmpty()) {
			updateLogwindow("Keine Klassenliste ausgew�hlt...","red");
		} else if (table.getItemCount() == 0) {
			updateLogwindow("Keine Aufgaben angelegt...","red");			
		} else {		
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Klasse");
			
			Font headerFont = workbook.createFont();
		    headerFont.setBold(true);
		    headerFont.setFontHeightInPoints((short) 12);
		    CellStyle headerCellStyle = workbook.createCellStyle();
		    headerCellStyle.setFont(headerFont);
		    
		    for (int i=0; i<startRow; i++) {
		    	sheet.createRow(i);
		    }
			
			int colNumStart = 1;
			int rowNum = startRow-1;
			
			nextColumn = 1;
			
			// borders
//			setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn+schuelerHeader.length-1), sheet);
//			setRegionBorderWithThin(new CellRangeAddress(3, 3, 1, 2), sheet);
			
			for (int i = 0; i < schuelerHeader.length; i++) {
				  setCellText(sheet, rowNum, nextColumn+i, schuelerHeader[i]);
			}
			
			rowNum++;
			
			// borders
			setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+klasse.getSize()-1, nextColumn, nextColumn+schuelerHeader.length-1), sheet);
			
			for (Schueler schueler : klasse.getSchueler()) {
			      setCellText(sheet, rowNum, nextColumn, schueler.getNachname());
			      setCellText(sheet, rowNum, nextColumn+1, schueler.getVorname());
			      rowNum++;
			}
			nextColumn++;
			nextColumn++;
			
			// alle aufgaben hinzuf�gen
			Pattern pa = Pattern.compile(Aufgabe.CONFIG_PATTERN);
			Pattern pt = Pattern.compile(Textproduktion.CONFIG_PATTERN);
			
			Matcher m;
			
			for (int i=0; i<table.getItemCount(); i++) {
				TableItem ti = table.getItem(i);
				switch (ti.getText(0)) {
					case "Aufgabe":
						m = pa.matcher(ti.getText(1));
						if (m.matches()) {
							if (m.groupCount() == 2) {
								try {
									double be = Double.parseDouble(m.group(1));
									double gewichtung = Double.parseDouble(m.group(2));
									aufgabeToXlsx(sheet, new Aufgabe("Aufgabe", be, gewichtung));
								} catch (NumberFormatException e) {
									updateLogwindow("Bitte nur Zahlen eingeben","red");
								}								
							} else {
								updateLogwindow("Fehler beim Erstellen der Aufagben im *.xlsx", "red");
							}							
						}
						break;
					case "Textproduktion":
						m = pt.matcher(ti.getText(1));
						if (m.matches()) {
							if (m.groupCount() == 3) {
								try {
									double inhalt = Double.parseDouble(m.group(1));
									double sprache = Double.parseDouble(m.group(2));
									double gewichtung = Double.parseDouble(m.group(2));
									TextproduktionToXlsx(sheet, new Textproduktion("Textproduktion", inhalt, sprache, gewichtung));
								} catch (NumberFormatException e) {
									updateLogwindow("Bitte nur Zahlen eingeben","red");
								}
							} else {
								updateLogwindow("Fehler beim Erstellen der Textproduktion im *.xlsx", "red");
							}							
						}
						break;
				}
			}			
			
			// berechnung der Gesamtpunktzahl
			addTotalPoints(sheet);
			nextColumn++;
			
			// Uebersicht ueber alle Noten
			addNotenuebersicht(sheet);
			
			// Resize all columns to fit the content size
		    for (int i = 0; i < 3; i++) {
		      sheet.autoSizeColumn(i);
		    }
			
			// Write the output to a file
		    FileOutputStream fileOut;
			try {
				fileOut = new FileOutputStream("Klasse.xlsx");
				workbook.write(fileOut);
				workbook.close();
			    fileOut.close();
			    updateLogwindow("Excel-Datei erfolgreich erstellt","green");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				updateLogwindow("Die Datei konnte nicht erstellt werden","red");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    
	}
	
	static void addAufgabe(Aufgabe task) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, task.getBezeichnung());
		item.setText(1, task.getConfig());
		fitTableColumnsWidth(table);		
	}
	
	static void addTextproduktion(Textproduktion task) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, task.getBezeichnung());
		item.setText(1, task.getConfig());
		fitTableColumnsWidth(table);		
	}
	
	private static void fitTableColumnsWidth(Table t) {
		for (int i = 0, n = t.getColumnCount(); i < n; i++) {
            t.getColumn(i).pack();
		}
	}
	
	private void aufgabeToXlsx(Sheet sheet, Aufgabe task) {
		
		int rowNum = startRow-3;
		
		// header
		setCellText(sheet, rowNum, nextColumn, task.getBezeichnung());
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,nextColumn,nextColumn+1));
		
		rowNum++;
		
		// legend of all columns
		setCellText(sheet, rowNum, nextColumn, task.getNameBe());
		setCellText(sheet, rowNum, nextColumn+1, task.getNameGewichtung());

		rowNum++;
		
		setCellText(sheet, rowNum, nextColumn, task.getBe());
		setCellText(sheet, rowNum, nextColumn+1, task.getGewichtung());
		
		rowNum++;
		
		// borders
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+klasse.getSize()-1, nextColumn, nextColumn+1), sheet);
		
		for (int i = 0; i < klasse.getSize(); i++) {	
			String formula = columnNames[nextColumn] + (rowNum+1) + "*" + columnNames[nextColumn+1] + startRow;
			setCellTextAsFormula(sheet, rowNum, nextColumn+1, formula);			
			rowNum++;
		}
		
		resultColumns.add(columnNames[nextColumn+1]);
		
		totalPointsFormula = totalPointsFormula + columnNames[nextColumn] + (startRow) + "*" + columnNames[nextColumn+1] + (startRow) + "+";
		
		// increment column 2x
		nextColumn++;
		nextColumn++;		
	}
	
	private void TextproduktionToXlsx(Sheet sheet, Textproduktion task) {
		
		int rowNum = startRow-3;
		
		// header
		setCellText(sheet, rowNum, nextColumn, task.getBezeichnung());
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,nextColumn,nextColumn+2));
		
		rowNum++;
		
		// legend of all columns
		setCellText(sheet, rowNum, nextColumn, task.getNameInhalt());
		setCellText(sheet, rowNum, nextColumn+1, task.getNameSprache());
		setCellText(sheet, rowNum, nextColumn+2, task.getNameGewichtung());		
		
		rowNum++;
		
		// coefficients
		setCellText(sheet, rowNum, nextColumn, task.getPunkteInhalt());
		setCellText(sheet, rowNum, nextColumn+1, task.getPunkteSprache());
		setCellText(sheet, rowNum, nextColumn+2, task.getGewichtung());
		
		rowNum++;
		
		// borders
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+klasse.getSize()-1, nextColumn, nextColumn+2), sheet);
		
		for (int i = 0; i < klasse.getSize(); i++) {
			String formula = "(" + columnNames[nextColumn] + (rowNum+1) + "+" + columnNames[nextColumn+1] + (rowNum+1) + ")*" + columnNames[nextColumn+2] + startRow;
			setCellTextAsFormula(sheet, rowNum, nextColumn+2, formula);			
			rowNum++;
		}	
				
		resultColumns.add(columnNames[nextColumn+2]);
		totalPointsFormula = totalPointsFormula + "(" + columnNames[nextColumn] + (startRow) + "+" + columnNames[nextColumn+1] + (startRow) + ")*" + columnNames[nextColumn+2] + (startRow) + "+";
		
		// increment column index 3x
		nextColumn++;
		nextColumn++;
		nextColumn++;
	}

	private void addTotalPoints(Sheet sheet) {
		
		int rowNum = startRow-2;
		String string;
		
		setCellText(sheet, rowNum, nextColumn, "Gesamt");
		
		// increment row index
		rowNum++;
		
		String formula = totalPointsFormula.substring(0, totalPointsFormula.length()-1);
		System.out.println(formula);
		setCellTextAsFormula(sheet, rowNum, nextColumn, formula);
		
		rowNum++;
		
		// borders
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+klasse.getSize()-1, nextColumn, nextColumn), sheet);
		
		for(int i=0; i<klasse.getSize(); i++) {
			string = "";
			for (int j=0; j<resultColumns.size(); j++) {
				string = string + resultColumns.get(j) + (rowNum+1) + "+";
			}
			string = string.substring(0, string.length() - 1);
			setCellTextAsFormula(sheet, rowNum, nextColumn, string);
			rowNum++;
		}		
		
		totalPointsColumnIndex = nextColumn;
		
		// increment column index
		nextColumn++;		
	}

	private void setCellText(Sheet sheet, int row, int col, String text) {
		Row r = sheet.getRow(row);
		Cell cell = r.getCell(col);
		if (cell == null)
			cell = r.createCell(col);
		cell.setCellValue(text);
	}
	
	private void setCellText(Sheet sheet, int row, int col, double value) {
		Row r = sheet.getRow(row);
		Cell cell = r.getCell(col);
		if (cell == null)
			cell = r.createCell(col);
		cell.setCellValue(value);
	}
	
	private void setCellTextAsFormula(Sheet sheet, int row, int col, String formula) {
		Row r = sheet.getRow(row);
		Cell cell = r.getCell(col);
		if (cell == null)
			cell = r.createCell(col);
		cell.setCellFormula(formula);
	}
	
	private void addNotenuebersicht(Sheet sheet) {
		
		int rowNum = startRow-1;
		
		// headers
		setCellText(sheet, rowNum, nextColumn, "Note");
		setCellText(sheet, rowNum, nextColumn+1, "Prozent");
		setCellText(sheet, rowNum, nextColumn+2, "Punktebereich");
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,nextColumn+2,nextColumn+4));
		setCellText(sheet, rowNum, nextColumn+5, "Anzahl");
		
		CellRangeAddress cra = new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn+5);
		setRegionBorderWithThin(cra, sheet);
		
		// increment row index
		rowNum++;
		
		double[] prozentbereiche = {87.5,75,62.5,50,30,0};
		String form;
		String cellTotalPoints = columnNames[totalPointsColumnIndex] + startRow;
		
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+5, nextColumn, nextColumn+5), sheet);
		
		// noten
		for (int i=1; i<=6; i++) {
			setCellText(sheet, rowNum+i-1, nextColumn, i);
			setCellText(sheet, rowNum+i-1, nextColumn+1, prozentbereiche[i-1]);
			if (i==1) {
				form = cellTotalPoints;
			} else {
				form = "ROUNDDOWN(" + columnNames[nextColumn+1] + (rowNum+i-1) + "*" + cellTotalPoints + "/100*2,0)/2-0.5";
			}
			setCellTextAsFormula(sheet, rowNum+i-1, nextColumn+2, form);
			setCellText(sheet, rowNum+i-1, nextColumn+3,"-");
			form = "ROUNDDOWN(" + columnNames[nextColumn+1] + (rowNum+i) + "*" + cellTotalPoints + "/100*2,0)/2";
			setCellTextAsFormula(sheet, rowNum+i-1, nextColumn+4, form);
//			setCellTextAsFormula(sheet, rowNum+i-1, nextColumn+5, "COUNTIF(1,1)");
		}
		
	}
	
	private void setRegionBorderWithThin(CellRangeAddress region, Sheet sheet) {    
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
    }

}
