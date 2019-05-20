package gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFColorScaleFormatting;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

import extras.Error;
import school.Aufgabe;
import school.NeueAufgabeDialog;
import school.Schueler;
import school.Schulklasse;
import school.Textproduktion;

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
	
	private final String[] alphabet =   {"","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private final ArrayList<String> columnNames = new ArrayList<String>();
	private List<String> resultColumns = new ArrayList<String>();
	private String totalPointsFormula = "";
	private int totalPointsColumnIndex, totalNotenColumnindex, notenBereicheEndeColumnIndex;
	
	private Font headerFont;
	
	private CellStyle boldStyle, boldCenteredStyle, centeredStyle, leftStyle, rightStyle;
	

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
		shell.setImage(SWTResourceManager.getImage(Notenberechnung_GUI.class, "/gui/icon.png"));
		shell.setSize(512, 223);
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
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Klassenliste auswählen...");
				String[] filterExt = { "*.txt", "*.*" };
		        fd.setFilterExtensions(filterExt);
				String selected = fd.open();				
				
				if (selected == null) {
					updateLogwindow("Keine Datei ausgwählt","red");
					lblKlasseDatei.setText("Keine Datei ausgwählt");
					lblKlasseDatei.requestLayout();
				} else {
					System.out.println("Datei ausgwählt: " + fd.getFileName().toString());
					String file_name = fd.getFileName().toString();
					String file_path = fd.getFilterPath().toString();
					
					lblKlasseDatei.setText(file_name);
					lblKlasseDatei.requestLayout();
					
					updateLogwindow("Klassenliste ausgewählt","blue");
					
					klasse = new Schulklasse();
					String path_to_file = file_path + "\\" + file_name;
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
		
		Button addTask = new Button(shell, SWT.NONE);
		addTask.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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
			updateLogwindow("Keine Klassenliste ausgewählt...","red");
		} else if (table.getItemCount() == 0) {
			updateLogwindow("Keine Aufgaben angelegt...","red");			
		} else {		
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Klasse");
			
			initVariables(sheet);
		    
		    for (int i=0; i<startRow; i++) {
		    	sheet.createRow(i);
		    }

			int rowNum = startRow-1;			
			nextColumn = 1;
			
			for (int i = 0; i < schuelerHeader.length; i++) {
				  setCellText(sheet, rowNum, nextColumn+i, schuelerHeader[i]);
				  sheet.getRow(rowNum).getCell(nextColumn+i).setCellStyle(boldStyle);
			}
			setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn+schuelerHeader.length-1), sheet);
			
			rowNum++;
			
			// borders			
			setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+klasse.getSize()-1, nextColumn, nextColumn+schuelerHeader.length-1), sheet);
			
			int temp_rowNum = rowNum;
			for (Schueler schueler : klasse.getSchueler()) {
			      setCellText(sheet, temp_rowNum, nextColumn, schueler.getNachname());
			      setCellText(sheet, temp_rowNum, nextColumn+1, schueler.getVorname());
			      temp_rowNum++;
			}
			
			// increment column 2x
			nextColumn++;
			nextColumn++;
			
			rowNum--;
			
			// Noten
			setCellText(sheet, rowNum, nextColumn, "  Noten  ");
			sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,nextColumn,nextColumn+2));
			sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);
			setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn+2), sheet);
			rowNum++;
			setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+klasse.getSize()-1, nextColumn, nextColumn+2), sheet);
			
			totalNotenColumnindex = nextColumn+1;
			
			// increment column 3x
			nextColumn++;
			nextColumn++;
			nextColumn++;
			
			
			// alle Aufgaben hinzufügen
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
			
			// Berechnung der Gesamtpunktzahl
			addTotalPoints(sheet);
			nextColumn++;		
			
			// Uebersicht ueber alle Noten
			addNotenuebersicht(sheet);
			
			// Berechnung der GesamtNoten
			addNotenberechnung(sheet);
			
			// Resize all columns to fit the content size
		    for (int i = 0; i < 6; i++) {
		      sheet.autoSizeColumn(i);
		    }
			
			// Select a filename to save the file	    
		    FileDialog fsd = new FileDialog(shell, SWT.SAVE);
			fsd.setText("Speichern unter...");
			String[] filterExt = {"*.xlsx"};
	        fsd.setFilterExtensions(filterExt);
	        fsd.setOverwrite(true);
			String selected = fsd.open();	
			
			if (selected == null) {
				updateLogwindow("Keine Datei ausgewählt. Liste wurde nicht gespeichert.", "red");
			} else {
				// Write the output to a file
			    FileOutputStream fileOut;
				try {
					fileOut = new FileOutputStream(selected);
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
	    
	}
	
	public static void addAufgabe(Aufgabe task) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, task.getBezeichnung());
		item.setText(1, task.getConfig());
		fitTableColumnsWidth(table);		
	}
	
	public static void addTextproduktion(Textproduktion task) {
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
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn+1), sheet);
		
		rowNum++;
		
		// legend of all columns
		setCellText(sheet, rowNum, nextColumn, task.getNameBe());
		setCellText(sheet, rowNum, nextColumn+1, task.getNameGewichtung());		
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn+1).setCellStyle(centeredStyle);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn+1, nextColumn+1), sheet);
		
		sheet.setColumnWidth(nextColumn, 10*256);
		sheet.setColumnWidth(nextColumn+1, 12*256);

		rowNum++;
		
		setCellText(sheet, rowNum, nextColumn, task.getBe());
		setCellText(sheet, rowNum, nextColumn+1, task.getGewichtung());		
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn+1).setCellStyle(centeredStyle);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn+1, nextColumn+1), sheet);
		
		rowNum++;
		
		// borders
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+klasse.getSize()-1, nextColumn, nextColumn+1), sheet);
		
		for (int i = 0; i < klasse.getSize(); i++) {	
			String formula = columnNames.get(nextColumn) + (rowNum+1) + "*" + columnNames.get(nextColumn+1) + startRow;
			setCellTextAsFormula(sheet, rowNum, nextColumn+1, formula);			
			rowNum++;
		}
		
		resultColumns.add(columnNames.get(nextColumn+1));
		
		totalPointsFormula = totalPointsFormula + columnNames.get(nextColumn) + (startRow) + "*" + columnNames.get(nextColumn+1) + (startRow) + "+";
		
		// increment column 2x
		nextColumn++;
		nextColumn++;		
	}
	
	private void TextproduktionToXlsx(Sheet sheet, Textproduktion task) {
		
		int rowNum = startRow-3;
		
		// header
		setCellText(sheet, rowNum, nextColumn, task.getBezeichnung());
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,nextColumn,nextColumn+2));
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn+2), sheet);
		
		rowNum++;
		
		// legend of all columns
		setCellText(sheet, rowNum, nextColumn, task.getNameInhalt());
		setCellText(sheet, rowNum, nextColumn+1, task.getNameSprache());
		setCellText(sheet, rowNum, nextColumn+2, task.getNameGewichtung());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn+1).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn+2).setCellStyle(centeredStyle);		
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn+1, nextColumn+1), sheet);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn+2, nextColumn+2), sheet);
		
		sheet.setColumnWidth(nextColumn, 10*256);
		sheet.setColumnWidth(nextColumn+1, 10*256);
		sheet.setColumnWidth(nextColumn+2, 12*256);
		
		rowNum++;
		
		// coefficients
		setCellText(sheet, rowNum, nextColumn, task.getPunkteInhalt());
		setCellText(sheet, rowNum, nextColumn+1, task.getPunkteSprache());
		setCellText(sheet, rowNum, nextColumn+2, task.getGewichtung());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn+1).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn+2).setCellStyle(centeredStyle);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn+1, nextColumn+1), sheet);
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum, nextColumn+2, nextColumn+2), sheet);
		
		rowNum++;
		
		// borders
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+klasse.getSize()-1, nextColumn, nextColumn+2), sheet);
		
		for (int i = 0; i < klasse.getSize(); i++) {
			String formula = "(" + columnNames.get(nextColumn) + (rowNum+1) + "+" + columnNames.get(nextColumn+1) + (rowNum+1) + ")*" + columnNames.get(nextColumn+2) + startRow;
			setCellTextAsFormula(sheet, rowNum, nextColumn+2, formula);			
			rowNum++;
		}	
				
		resultColumns.add(columnNames.get(nextColumn+2));
		totalPointsFormula = totalPointsFormula + "(" + columnNames.get(nextColumn) + (startRow) + "+" + columnNames.get(nextColumn+1) + (startRow) + ")*" + columnNames.get(nextColumn+2) + (startRow) + "+";
		
		// increment column index 3x
		nextColumn++;
		nextColumn++;
		nextColumn++;
	}

	private void addTotalPoints(Sheet sheet) {
		
		int rowNum = startRow-3;
		String string;
		
		setCellText(sheet, rowNum, nextColumn, "Gesamt");
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);
		
		// increment row index
		rowNum++;
		rowNum++;
		
		String formula = totalPointsFormula.substring(0, totalPointsFormula.length()-1);
		setCellTextAsFormula(sheet, rowNum, nextColumn, formula);
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldStyle);
		setRegionBorderWithThin(new CellRangeAddress(rowNum-2, rowNum, nextColumn, nextColumn), sheet);
		
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
		sheet.setColumnWidth(nextColumn, 10*256);
		setCellText(sheet, rowNum, nextColumn+1, "Prozent");
		sheet.setColumnWidth(nextColumn+1, 10*256);
		setCellText(sheet, rowNum, nextColumn+2, "Punktebereich");
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,nextColumn+2,nextColumn+4));
		sheet.setColumnWidth(nextColumn+3, 3*256); // Minus-Zeichen
		setCellText(sheet, rowNum, nextColumn+5, "Anzahl");
		
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);
		sheet.getRow(rowNum).getCell(nextColumn+1).setCellStyle(boldCenteredStyle);
		sheet.getRow(rowNum).getCell(nextColumn+2).setCellStyle(boldCenteredStyle);
		sheet.getRow(rowNum).getCell(nextColumn+5).setCellStyle(boldCenteredStyle);
		
		notenBereicheEndeColumnIndex = nextColumn+4;
		
		CellRangeAddress cra = new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn+5);
		setRegionBorderWithThin(cra, sheet);
		
		// increment row index
		rowNum++;
		
		double[] prozentbereiche = {87.5,75,62.5,50,30,0};
		String form;
		String cellTotalPoints = columnNames.get(totalPointsColumnIndex) + startRow;		
		
		// Noten
		for (int i=1; i<=6; i++) {
			setCellText(sheet, rowNum+i-1, nextColumn, i);
			setCellText(sheet, rowNum+i-1, nextColumn+1, prozentbereiche[i-1]);
			if (i==1) {
				form = cellTotalPoints;
			} else {
				form = "ROUNDDOWN(" + columnNames.get(nextColumn+1) + (rowNum+i-1) + "*" + cellTotalPoints + "/100*2,0)/2-0.5";
			}
			setCellTextAsFormula(sheet, rowNum+i-1, nextColumn+2, form);
			sheet.getRow(rowNum+i-1).getCell(nextColumn+2).setCellStyle(rightStyle);
			
			setCellText(sheet, rowNum+i-1, nextColumn+3,"-");
			sheet.getRow(rowNum+i-1).getCell(nextColumn+3).setCellStyle(centeredStyle);
			
			form = "ROUNDDOWN(" + columnNames.get(nextColumn+1) + (rowNum+i) + "*" + cellTotalPoints + "/100*2,0)/2";
			setCellTextAsFormula(sheet, rowNum+i-1, nextColumn+4, form);
			sheet.getRow(rowNum+i-1).getCell(nextColumn+4).setCellStyle(leftStyle);
		}
		
		// borders
		setRegionBorderWithThin(new CellRangeAddress(rowNum, rowNum+5, nextColumn, nextColumn+5), sheet);
		
	}
	
	private void setRegionBorderWithThin(CellRangeAddress region, Sheet sheet) {    
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
    }
	
	private void addNotenberechnung(Sheet sheet) {
		
		String formulaNote, formulaPlus, formulaMinus, aktCell;
		int rowNum = startRow+1;
		
		for (int i=0; i<klasse.getSize(); i++) {
			
			aktCell = columnNames.get(totalPointsColumnIndex) + rowNum; 
			
			formulaNote = "IF(NOT(ISNUMBER(" + aktCell + ")),\"\",";
			// Ausgangsbasis fuer die + und - Berechnung
			formulaPlus = formulaNote + "IF(OR(";
			formulaMinus = formulaNote + "IF(OR(";
			
			for (int j=1; j<=6; j++) {
				formulaNote = formulaNote + "IF(" + aktCell + ">=$" + columnNames.get(notenBereicheEndeColumnIndex) + "$" + (startRow+j) + "," + j + ",";
				formulaPlus = formulaPlus + aktCell + "=$" + columnNames.get(notenBereicheEndeColumnIndex-2) + "$" + (startRow+j) + ",";
				formulaMinus = formulaMinus + aktCell + "=$" + columnNames.get(notenBereicheEndeColumnIndex) + "$" + (startRow+j) + ",";
			}
			formulaNote = formulaNote + "\"\")))))))";
			formulaPlus = formulaPlus.substring(0, formulaPlus.length()-1) + "),\"+\",\"\"))";
			formulaMinus = formulaMinus.substring(0, formulaMinus.length()-1) + "),\"-\",\"\"))";
			
			setCellTextAsFormula(sheet, startRow+i, totalNotenColumnindex, formulaNote);
			setCellTextAsFormula(sheet, startRow+i, totalNotenColumnindex-1, formulaPlus);
			setCellTextAsFormula(sheet, startRow+i, totalNotenColumnindex+1, formulaMinus);
			
			rowNum++;
		}
		
		// Bedingte Formattierung		
		SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();		
		ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingColorScaleRule();
	    ColorScaleFormatting clrFmt = rule1.getColorScaleFormatting();
	    assertEquals(3, clrFmt.getNumControlPoints());
	    String[] colors = {"00b034", "ffc000", "ff0000"};
	    assertEquals(3, clrFmt.getColors().length);
	    assertEquals(3, clrFmt.getThresholds().length);
	    
	    for (int c = 0; c < 3; c++) {
	    	((ExtendedColor)clrFmt.getColors()[c]).setARGBHex(colors[c]);
	    }

	    clrFmt.getThresholds()[0].setRangeType(RangeType.NUMBER);
	    clrFmt.getThresholds()[0].setValue(1d);
	    clrFmt.getThresholds()[1].setRangeType(RangeType.NUMBER);
	    clrFmt.getThresholds()[1].setValue(3d);
	    clrFmt.getThresholds()[2].setRangeType(RangeType.NUMBER);
	    clrFmt.getThresholds()[2].setValue(6d);

	    CellRangeAddress [] regions = { CellRangeAddress.valueOf(columnNames.get(totalNotenColumnindex) + (startRow+1) + ":" + columnNames.get(totalNotenColumnindex) + (startRow+klasse.getSize())) };
	    sheetCF.addConditionalFormatting(regions, rule1);
		
	    
		// zaehlen der jeweiligen Noten
		String formula;
		for (int i=1; i<=6; i++) {
			formula = "COUNTIF(" + columnNames.get(totalNotenColumnindex) + (startRow+1) + ":" + columnNames.get(totalNotenColumnindex) + (startRow+klasse.getSize()) + "," + columnNames.get(notenBereicheEndeColumnIndex-4) + (startRow+i) + ")";
			setCellTextAsFormula(sheet, startRow+i-1, notenBereicheEndeColumnIndex+1, formula);
		}		
	}
	
	private void initVariables(Sheet sheet) {
		headerFont = sheet.getWorkbook().createFont();
	    headerFont.setBold(true);
	    headerFont.setFontHeightInPoints((short) 12);
	    
	    boldStyle = sheet.getWorkbook().createCellStyle();
	    boldStyle.setFont(headerFont);

	    boldCenteredStyle = boldStyle;
	    boldCenteredStyle.setFont(headerFont);
	    boldCenteredStyle.setAlignment(HorizontalAlignment.CENTER);
	    
	    centeredStyle = sheet.getWorkbook().createCellStyle();
	    centeredStyle.setAlignment(HorizontalAlignment.CENTER);
	    
	    leftStyle = sheet.getWorkbook().createCellStyle();
	    leftStyle.setAlignment(HorizontalAlignment.LEFT);
	    
	    rightStyle = sheet.getWorkbook().createCellStyle();
	    rightStyle.setAlignment(HorizontalAlignment.RIGHT);
	    
	    // Bezeichnungen der Spalten in MS-Excel
	    if (columnNames.size() == 0) {
		    for (int i=0; i<alphabet.length; i++) {
		    	for (int j=1; j<alphabet.length; j++) {
		    		if (i==0) {
		    			columnNames.add(alphabet[j] + alphabet[i]);
		    		} else {
		    			columnNames.add(alphabet[i] + alphabet[j]);
		    		}
		    	}
		    }
		}
	}
}
