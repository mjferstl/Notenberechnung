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

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

import extras.Chart;
import extras.Error;
import extras.ExcelSheetFunctions;
import school.Aufgabe;
import school.Aufgabentyp;
import school.Schueler;
import school.Schulklasse;
import school.Textproduktion;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class Notenberechnung_GUI {

	// shell
	protected Shell shell;
	
	// integers
	private int nextColumn;
	private int totalPointsColumnIndex, totalNotenColumnindex, notenBereicheEndeColumnIndex;
	private int pointsMaxColumn, pointsMinColumn, percentColumn, gradesColumn, countGradesColumn;
	private final int startRow = 4; // funefte Zeile

	// strings
	private final String ARROW_UPWARDS = "\u2191";
	private final String ARROW_DOWNWARDS = "\u2193";
	private String fileNameKlasse = "";
	private String totalPointsFormula = "";
	private final String[] alphabet = { "", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
			"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private final String[] titles = {"", "Bezeichnung", "Bewertung" };
	private final static String[] schuelerHeader = { "Nachname", "Vorname" };
	private List<String> columnNames = new ArrayList<String>();
	private List<String> resultColumns = new ArrayList<String>();
	
	// labels
	private static Label logwindow;
	private Label lblKlasseDatei;
	private Label lblKlassenliste;
	private Label lblAufgaben;
	
	// buttons
	private Button btnAddTask;
	private Button btnRemoveTask;
	private Button btnMoveUp;
	private Button btnMoveDown;
	private Button btnBrowse;
	private Button btnExcelErstellen;
	
	// custom objects
	private Schulklasse klasse;
	
	// fonts
	private Font headerFont;

	// cell styles
	private CellStyle boldStyle, boldCenteredStyle, centeredStyle, leftStyle, rightStyle, noColor;
	
	// tables
	private static Table table;
	private Group group;
	
	private final String DIAMETER = "\u2300";
	

	/**
	 * Launch the application.
	 * 
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
		shell.setSize(536, 239);
		shell.setText("Erstellen einer Excel-Datei zur Notenauswertung");
		shell.setLayout(new GridLayout(3, false));

		lblKlassenliste = new Label(shell, SWT.NONE);
		lblKlassenliste.setText("Klassenliste");

		lblKlasseDatei = new Label(shell, SWT.NONE);
		lblKlasseDatei.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblKlasseDatei.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
				btnBrowse = new Button(shell, SWT.NONE);
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
							updateLogwindow("Keine Datei ausgwählt", "red");
							lblKlasseDatei.setText("Keine Datei ausgwählt");
							lblKlasseDatei.requestLayout();
						} else {
//					System.out.println("Datei ausgwählt: " + fd.getFileName().toString());
							fileNameKlasse = fd.getFileName().toString();
							String file_path = fd.getFilterPath().toString();

							lblKlasseDatei.setText(fileNameKlasse);
							lblKlasseDatei.requestLayout();

							updateLogwindow("Klassenliste ausgewählt", "blue");

							klasse = new Schulklasse();
							String path_to_file = file_path + "\\" + fileNameKlasse;
							Error error = new Error();
							error = klasse.readKlassenliste(path_to_file);

							// update logwindow
							if (error.getErrorId() == 0) {
								updateLogwindow(error.getErrorMsg(), "green");
							} else {
								updateLogwindow(error.getErrorMsg(), "red");
							}
						}
					}
				});
				btnBrowse.setText("Datei einlesen");

		lblAufgaben = new Label(shell, SWT.NONE);
		lblAufgaben.setText("Aufgaben");

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if ((table.getItemCount() > 0)) {
					int selectedIndex = table.getSelectionIndex();
					TableItem ti = table.getItem(selectedIndex);
					if (ti.getText(0) == Aufgabe.TYPE) {
						Aufgabe a = Aufgabe.parseTextToAufgabe(ti.getText(1),ti.getText(2));
						NeueAufgabeDialog nad = new NeueAufgabeDialog(shell, a, selectedIndex);
						nad.open();
					} else if (ti.getText(0) == Textproduktion.TYPE) {
						Textproduktion t = Textproduktion.parseTextToTextproduktion(ti.getText(1),ti.getText(2));
						NeueAufgabeDialog nad = new NeueAufgabeDialog(shell, t, selectedIndex);
						nad.open();
					}
				}		
			}
		});
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		gd_table.heightHint = 25;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}

		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
		
		group = new Group(shell, SWT.NONE);
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		btnMoveUp = new Button(group, SWT.NONE);
		btnMoveUp.setBounds(3, 47, 50, 25);
		btnMoveUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((table.getItemCount() != 0) && (table.getSelection() != null) && (table.getSelectionIndex() != 0)) {
					int selectedItem = table.getSelectionIndex();
					moveTableItem(selectedItem, "upwards");
				}
			}
		});
		btnMoveUp.setText(ARROW_UPWARDS);		
		btnAddTask = new Button(group, SWT.NONE);
		btnAddTask.setBounds(3, 10, 50, 25);
		btnAddTask.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				// open a new dialog for creating a task
				NeueAufgabeDialog na = new NeueAufgabeDialog(shell);
				na.open();
				
				// update the buttons
				setButtonsEnables();
			}
		});
		btnAddTask.setText("+");
		btnRemoveTask = new Button(group, SWT.NONE);
		btnRemoveTask.setBounds(56, 10, 50, 25);
		btnRemoveTask.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((table.getItemCount() != 0) && (table.getSelectionIndex() >= 0)) {
					table.remove(table.getSelectionIndex());
					
					// update the buttons
					setButtonsEnables();
				}
			}
		});
		btnRemoveTask.setText("-");
		btnMoveDown = new Button(group, SWT.NONE);
		btnMoveDown.setBounds(3, 78, 50, 25);
		btnMoveDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((table.getItemCount() != 0) && (table.getSelection() != null) && (table.getSelectionIndex() != table.getItemCount())) {
					int selectedItem = table.getSelectionIndex();
					moveTableItem(selectedItem, "downwards");
				}
				
			}
		});
		btnMoveDown.setText(ARROW_DOWNWARDS);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		btnExcelErstellen = new Button(shell, SWT.NONE);
		btnExcelErstellen.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		btnExcelErstellen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLogwindow("Excel-Datei wird erstellt...", "blue");
				createXlsxFile();
			}
		});
		btnExcelErstellen.setText("Excel erstellen");

		logwindow = new Label(shell, SWT.NONE);
		logwindow.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		logwindow.setText(" ");
		
		// set all buttons enabled/disabled depending on the current contents
		setButtonsEnables();
	}

	
	/**
	 * Set the buttons of the GUI enabled/disabled
	 */
	private void setButtonsEnables() {
		
		// buttons for moving items up and down
		if (table.getItemCount() >= 2) {
			btnMoveUp.setEnabled(true);
			btnMoveDown.setEnabled(true);
		} else {
			btnMoveUp.setEnabled(false);
			btnMoveDown.setEnabled(false);
		}
		
		if (table.getItemCount() == 0) {
			btnRemoveTask.setEnabled(false);
		} else {
			btnRemoveTask.setEnabled(true);
		}		
	}
	
	/**
	 * Update the label called "logwindow". text color is set to "black"
	 * 
	 * @param text: text to be set in the logwindow label
	 */
	public static void updateLogwindow(String text) {
		updateLogwindow(text,"black");
	}

	/**
	 * Update the label called "logwindow" and set a specific text color
	 * 
	 * @param text: text to be set in the logwindow label
	 * @param color: string containing the text color. possible colors: blue, red, green, black
	 */
	public static void updateLogwindow(String text, String color) {
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
	
	
	/**
	 * Create the Excel-file
	 */
	private void createXlsxFile() {

		if (klasse == null || klasse.isEmpty()) {
			updateLogwindow("Keine Klassenliste ausgewählt...", "red");
		} else if (table.getItemCount() == 0) {
			updateLogwindow("Keine Aufgaben angelegt...", "red");
		} else {			
			// remove all current item from the lists and formulas
			resultColumns.clear();
			columnNames.clear();	
			totalPointsFormula = "";			
			
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Klasse");

			initVariables(sheet);

			for (int i = 0; i < startRow; i++)
				sheet.createRow(i);

			int rowNum = startRow - 1;
			nextColumn = 1;

			addPupilsNames(sheet, rowNum);

			// increment column 2x
			nextColumn++;
			nextColumn++;

			addGradesColumn(sheet, rowNum);

			// increment column 3x
			nextColumn++;
			nextColumn++;
			nextColumn++;

			// Alle angelegten Aufgaben einfuegen
			addAufgaben(sheet);

			// Berechnung der Gesamtpunktzahl
			addTotalScore(sheet);
			nextColumn++;

			// Uebersicht ueber alle Noten
			addGrading(sheet);

			// Berechnung der GesamtNoten
			addCalculationGrades(sheet);

			// Diagramm hizufuegen
			addGradingChart(sheet);

			// Resize all columns to fit the content size
			for (int i = 0; i < 6; i++)
				sheet.autoSizeColumn(i);

			// Select a filename to save the file
			saveWorkbook(workbook);			
		}
	}

	/**
	 * save the current workbook to an *.xlsx file
	 * 
	 * @param workbook: object of type Workbook
	 */
	private void saveWorkbook(Workbook workbook) {
		FileDialog fsd = new FileDialog(shell, SWT.SAVE);
		fsd.setText("Speichern unter...");
		String[] filterExt = { "*.xlsx" };
		fsd.setFilterExtensions(filterExt);
		fsd.setOverwrite(true);
		fsd.setFileName(fileNameKlasse);
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
				updateLogwindow("Excel-Datei erfolgreich erstellt", "green");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				updateLogwindow("Die Datei konnte nicht erstellt werden", "red");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}

	/**
	 * get all table items and print them to the excel sheet by calling the correct methods
	 * 
	 * @param sheet: excel sheet
	 */
	private void addAufgaben(Sheet sheet) {
		
		String bezeichnung, text;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem ti = table.getItem(i);
			bezeichnung = ti.getText(1);
			text = ti.getText(2);
			switch (ti.getText(0)) {
			case Aufgabe.TYPE:
				Aufgabe a = Aufgabe.parseTextToAufgabe(bezeichnung, text);
				if (a != null) {
					aufgabeToXlsx(sheet, a);
				}
				break;
			case Textproduktion.TYPE:
				Textproduktion tp = Textproduktion.parseTextToTextproduktion(bezeichnung, text);
				if (tp != null) {
					TextproduktionToXlsx(sheet, tp);
				}
				break;
			}
		}
	}

	/**
	 * add chart to the excel file showing the grading of all pupils
	 * 
	 * @param sheet: excel sheet
	 */
	private void addGradingChart(Sheet sheet) {
		Chart notenChart = new Chart(sheet);
		notenChart.setChartTitle("Notenverteilung");
		notenChart.setxAxisLabel("Noten");
		notenChart.setyAxisLabel("Anzahl");
		notenChart.setXData(new CellRangeAddress(startRow, startRow + 5, nextColumn + 4, nextColumn + 4));
		notenChart.setYData(new CellRangeAddress(startRow, startRow + 5, nextColumn + 5, nextColumn + 5));
		notenChart.setPositionInSheet(startRow + 9, startRow + 19, nextColumn, nextColumn + 6);
		notenChart.createChart();
	}

	/**
	 * add a task to the table
	 * 
	 * @param task: object of interface Aufgabentyp
	 */
	public static void addTask(Aufgabentyp task) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, task.getType());
		item.setText(1, task.getBezeichnung());
		item.setText(2, task.getConfig());
		fitTableColumnsWidth(table);
	}
	
	/**
	 * add a task to the table
	 * 
	 * @param task: object of interface Aufgabentyp
	 * @param tableIndex: index of the table item to be updated
	 */
	public static void updateTask(Aufgabentyp task, int tableIndex) {		
		TableItem item = table.getItem(tableIndex);
		item.setText(0, task.getType());
		item.setText(1, task.getBezeichnung());
		item.setText(2, task.getConfig());
		fitTableColumnsWidth(table);
	}

	/**
	 * set the width of all columns in the table to it's longest content
	 */
	private static void fitTableColumnsWidth(Table t) {
		for (int i = 0, n = t.getColumnCount(); i < n; i++) {
			t.getColumn(i).pack();
		}
	}

	/**
	 * print a task of type "Aufgabe" to the Excel file
	 * 
	 * @param sheet: excel sheet
	 * @param task: object of class Aufagbe
	 */
	private void aufgabeToXlsx(Sheet sheet, Aufgabe task) {

		int rowNum = startRow - 3;

		// header
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, task.getBezeichnung());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn + 1));
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn + 1), sheet);

		rowNum++;

		// legend of all columns
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, task.getNameBe());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 1, task.getNameGewichtung());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 1).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 1, nextColumn + 1), sheet);

		sheet.setColumnWidth(nextColumn, 10 * 256);
		sheet.setColumnWidth(nextColumn + 1, 12 * 256);

		rowNum++;

		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, task.getBe());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 1, task.getGewichtung());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 1).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 1, nextColumn + 1), sheet);

		rowNum++;

		// borders
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum + klasse.getSize() - 1, nextColumn, nextColumn + 1),
				sheet);

		for (int i = 0; i < klasse.getSize(); i++) {
			String formula = columnNames.get(nextColumn) + (rowNum + 1) + "*" + columnNames.get(nextColumn + 1)
					+ startRow;
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum, nextColumn + 1, formula);
			rowNum++;
		}

		resultColumns.add(columnNames.get(nextColumn + 1));

		totalPointsFormula = totalPointsFormula + columnNames.get(nextColumn) + (startRow) + "*"
				+ columnNames.get(nextColumn + 1) + (startRow) + "+";

		// increment column 2x
		nextColumn++;
		nextColumn++;
	}

	/**
	 * print a task of type "Textproduktion" to the Excel file
	 * 
	 * @param: sheet: excel sheet
	 * @param: task: object of class Textproduktion
	 */
	private void TextproduktionToXlsx(Sheet sheet, Textproduktion task) {

		int rowNum = startRow - 3;

		// header
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, task.getBezeichnung());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn + 2));
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn + 2), sheet);

		rowNum++;

		// legend of all columns
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, task.getNameInhalt());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 1, task.getNameSprache());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 2, task.getNameGewichtung());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 1).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 2).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 1, nextColumn + 1), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 2, nextColumn + 2), sheet);

		sheet.setColumnWidth(nextColumn, 10 * 256);
		sheet.setColumnWidth(nextColumn + 1, 10 * 256);
		sheet.setColumnWidth(nextColumn + 2, 12 * 256);

		rowNum++;

		// coefficients
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, task.getPunkteInhalt());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 1, task.getPunkteSprache());
		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn + 2, task.getGewichtung());
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 1).setCellStyle(centeredStyle);
		sheet.getRow(rowNum).getCell(nextColumn + 2).setCellStyle(centeredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 1, nextColumn + 1), sheet);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum, nextColumn + 2, nextColumn + 2), sheet);

		rowNum++;

		// borders
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum + klasse.getSize() - 1, nextColumn, nextColumn + 2),
				sheet);

		for (int i = 0; i < klasse.getSize(); i++) {
			String formula = "(" + columnNames.get(nextColumn) + (rowNum + 1) + "+" + columnNames.get(nextColumn + 1)
					+ (rowNum + 1) + ")*" + columnNames.get(nextColumn + 2) + startRow;
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum, nextColumn + 2, formula);
			rowNum++;
		}

		resultColumns.add(columnNames.get(nextColumn + 2));
		
		totalPointsFormula = totalPointsFormula + "(" + columnNames.get(nextColumn) + (startRow) + "+"
				+ columnNames.get(nextColumn + 1) + (startRow) + ")*" + columnNames.get(nextColumn + 2) + (startRow)
				+ "+";

		// increment column index 3x
		nextColumn++;
		nextColumn++;
		nextColumn++;
	}

	/**
	 * insert formulas for calculating the total scores in the excel file
	 * 
	 * @param sheet: excel sheet
	 */	
	private void addTotalScore(Sheet sheet) {

		int rowNum = startRow - 3;
		String string;

		ExcelSheetFunctions.setCellText(sheet, rowNum, nextColumn, "Gesamt");
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldCenteredStyle);

		// increment row index
		rowNum++;
		rowNum++;

		String formula = totalPointsFormula.substring(0, totalPointsFormula.length() - 1);
		ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum, nextColumn, formula);
		sheet.getRow(rowNum).getCell(nextColumn).setCellStyle(boldStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum - 2, rowNum, nextColumn, nextColumn), sheet);

		rowNum++;

		// borders
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum + klasse.getSize() - 1, nextColumn, nextColumn),
				sheet);

		for (int i = 0; i < klasse.getSize(); i++) {
			string = "";
			for (int j = 0; j < resultColumns.size(); j++) {
				string = string + resultColumns.get(j) + (rowNum + 1) + "+";
			}
			string = string.substring(0, string.length() - 1);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum, nextColumn, string);
			rowNum++;
		}

		totalPointsColumnIndex = nextColumn;

		// increment column index
		nextColumn++;
	}

	/**
	 * add a grading overview of all pupils
	 * 
	 * @param sheet: excel sheet
	 */
	private void addGrading(Sheet sheet) {

		int rowNum = startRow - 1;
		
		pointsMaxColumn = nextColumn;
		pointsMinColumn = nextColumn + 2;
		percentColumn = nextColumn + 3;
		gradesColumn = nextColumn + 4;
		countGradesColumn = nextColumn + 5;

		// headers
		ExcelSheetFunctions.setCellText(sheet, rowNum, pointsMaxColumn, "Punktebereich");
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, pointsMaxColumn, pointsMinColumn));
		sheet.setColumnWidth(pointsMaxColumn + 1, 3 * 256); // Minus-Zeichen
		ExcelSheetFunctions.setCellText(sheet, rowNum, percentColumn, "Prozent");
		sheet.setColumnWidth(percentColumn, 10 * 256);
		ExcelSheetFunctions.setCellText(sheet, rowNum, gradesColumn, "Note");
		sheet.setColumnWidth(gradesColumn, 10 * 256);
		ExcelSheetFunctions.setCellText(sheet, rowNum, countGradesColumn, "Anzahl");
		
		sheet.getRow(rowNum).getCell(pointsMaxColumn).setCellStyle(boldCenteredStyle);
		sheet.getRow(rowNum).getCell(percentColumn).setCellStyle(boldCenteredStyle);
		sheet.getRow(rowNum).getCell(gradesColumn).setCellStyle(boldCenteredStyle);
		sheet.getRow(rowNum).getCell(countGradesColumn).setCellStyle(boldCenteredStyle);

		notenBereicheEndeColumnIndex = pointsMinColumn;

		CellRangeAddress cra = new CellRangeAddress(rowNum, rowNum, nextColumn, nextColumn + 5);
		ExcelSheetFunctions.setRegionBorderThin(cra, sheet);

		// increment row index
		rowNum++;

		double[] prozentbereiche = { 87.5, 75, 62.5, 50, 30, 0 };
		String form;
		String cellTotalPoints = columnNames.get(totalPointsColumnIndex) + startRow;

		// Noten
		for (int i = 1; i <= 6; i++) {
			ExcelSheetFunctions.setCellText(sheet, rowNum + i - 1, gradesColumn, i);
			ExcelSheetFunctions.setCellText(sheet, rowNum + i - 1, percentColumn, prozentbereiche[i - 1]);
			if (i == 1) {
				form = cellTotalPoints;
			} else {
				form = "ROUNDDOWN(" + columnNames.get(percentColumn) + (rowNum + i - 1) + "*" + cellTotalPoints
						+ "/100*2,0)/2-0.5";
			}
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum + i - 1, pointsMaxColumn, form);
			sheet.getRow(rowNum + i - 1).getCell(pointsMaxColumn).setCellStyle(rightStyle);

			ExcelSheetFunctions.setCellText(sheet, rowNum + i - 1, pointsMaxColumn + 1, "-");
			sheet.getRow(rowNum + i - 1).getCell(pointsMaxColumn + 1).setCellStyle(centeredStyle);

			form = "ROUNDDOWN(" + columnNames.get(percentColumn) + (rowNum + i) + "*" + cellTotalPoints
					+ "/100*2,0)/2";
			ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum + i - 1, pointsMinColumn, form);
			sheet.getRow(rowNum + i - 1).getCell(pointsMinColumn).setCellStyle(leftStyle);
		}

		// borders
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(rowNum, rowNum + 5, nextColumn, nextColumn + 5), sheet);
		
		// average grade
		form = "(";
		for (int i=1; i<7; i++) {
			form = form + columnNames.get(countGradesColumn) + (rowNum + i) + "*" + columnNames.get(gradesColumn) + (rowNum + i) + "+";
		}
		form = form.substring(0, form.length()-1);
		form = form + ")/SUM(" + columnNames.get(countGradesColumn) + (rowNum + 1) + ":" + columnNames.get(countGradesColumn) + (rowNum + 6) + ")";
		ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum + 6, countGradesColumn, form);
		ExcelSheetFunctions.setCellText(sheet, rowNum + 6, countGradesColumn-1, DIAMETER);
		sheet.getRow(rowNum + 6).getCell(countGradesColumn - 1).setCellStyle(rightStyle);
		
		// percentage of grades of 5 and 6
		form = "SUM(" + columnNames.get(countGradesColumn) + (rowNum + 5) + ":" + columnNames.get(countGradesColumn) + (rowNum + 6) + ")";
		form = form + "/SUM(" + columnNames.get(countGradesColumn) + (rowNum+1) + ":" + columnNames.get(countGradesColumn) + (rowNum+6) + ")*100";
		ExcelSheetFunctions.setCellTextAsFormula(sheet, rowNum + 7, countGradesColumn, form);
		ExcelSheetFunctions.setCellText(sheet, rowNum + 7, countGradesColumn - 1, "% 5 u. 6");
		sheet.getRow(rowNum + 7).getCell(countGradesColumn - 1).setCellStyle(rightStyle);
	}

	

	/**
	 * add formulas for calculation the grades of all pupils
	 * 
	 *  @param sheet: excel sheet
	 */
	private void addCalculationGrades(Sheet sheet) {

		String formulaNote, formulaPlus, formulaMinus, aktCell;
		int rowNum = startRow + 1;

		for (int i = 0; i < klasse.getSize(); i++) {

			aktCell = columnNames.get(totalPointsColumnIndex) + rowNum;

			formulaNote = "IF(NOT(ISNUMBER(" + aktCell + ")),\"\",";
			// Ausgangsbasis fuer die + und - Berechnung
			formulaPlus = formulaNote + "IF(OR(";
			formulaMinus = formulaNote + "IF(OR(";

			for (int j = 1; j <= 6; j++) {
				formulaNote = formulaNote + "IF(" + aktCell + ">=$" + columnNames.get(notenBereicheEndeColumnIndex)
						+ "$" + (startRow + j) + "," + j + ",";
				formulaPlus = formulaPlus + aktCell + "=$" + columnNames.get(notenBereicheEndeColumnIndex - 2) + "$"
						+ (startRow + j) + ",";
				formulaMinus = formulaMinus + aktCell + "=$" + columnNames.get(notenBereicheEndeColumnIndex) + "$"
						+ (startRow + j) + ",";
			}
			formulaNote = formulaNote + "\"\")))))))";
			formulaPlus = formulaPlus.substring(0, formulaPlus.length() - 1) + "),\"+\",\"\"))";
			formulaMinus = formulaMinus.substring(0, formulaMinus.length() - 1) + "),\"-\",\"\"))";

			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i, totalNotenColumnindex, formulaNote);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i, totalNotenColumnindex - 1, formulaPlus);
			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i, totalNotenColumnindex + 1, formulaMinus);

			rowNum++;
		}

		/*
		 * conditional formatting for the pupils grades
		 * 1 = green, 3 = orange, 6 = red
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
		
//		for (int i=0; i<klasse.getSize(); i++) {
//			Cell cell = sheet.getRow(startRow+i).getCell(totalNotenColumnindex);
//			cell.setCellStyle(noColor);
//		}

		CellRangeAddress[] regions = { CellRangeAddress.valueOf(columnNames.get(totalNotenColumnindex) + (startRow + 1)
				+ ":" + columnNames.get(totalNotenColumnindex) + (startRow + klasse.getSize())) };
		sheetCF.addConditionalFormatting(regions, rule1);

		// count the amount of all grades
		String formula;
		for (int i = 1; i <= 6; i++) {
			formula = "COUNTIF(" + columnNames.get(totalNotenColumnindex) + (startRow + 1) + ":"
					+ columnNames.get(totalNotenColumnindex) + (startRow + klasse.getSize()) + ","
					+ columnNames.get(notenBereicheEndeColumnIndex + 2) + (startRow + i) + ")";
			ExcelSheetFunctions.setCellTextAsFormula(sheet, startRow + i - 1, notenBereicheEndeColumnIndex + 3, formula);
		}		
	}

	/**
	 * method for initialising some used variables
	 * 
	 * @param sheet: excel sheet
	 */
	private void initVariables(Sheet sheet) {
		headerFont = sheet.getWorkbook().createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);

		boldStyle = sheet.getWorkbook().createCellStyle();
		boldStyle.setFont(headerFont);		
		
		noColor = sheet.getWorkbook().createCellStyle();
		noColor.setFillPattern(FillPatternType.NO_FILL);

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
	}
	
	/**
	 * moving table items up- or downwards in the table
	 * 
	 * @param index: number representing the index of the table item in the table
	 * @param direction: String containing "upwards" or "downwards"
	 */
	private void moveTableItem(int index, String direction) {
		if (index >= 0 && (direction == "upwards" || direction == "downwards")) {
			 TableItem ti = table.getItem(index);
			 String[] tiContent = {ti.getText(0), ti.getText(1), ti.getText(2)};
			 ti.dispose();
			 TableItem nti = null;
			 
			 
			 if (direction == "upwards" && index > 0) {
				 nti = new TableItem(table, SWT.NONE, index-1);	
			 } else if (direction == "downwards") {
				 nti = new TableItem(table, SWT.NONE, index+1);
			 }			 			 
			 nti.setText(tiContent);		
		}
	}
	
	
	private void addPupilsNames(Sheet sheet, int row) {
		for (int i = 0; i < schuelerHeader.length; i++) {
			ExcelSheetFunctions.setCellText(sheet, row, nextColumn + i, schuelerHeader[i]);
			sheet.getRow(row).getCell(nextColumn + i).setCellStyle(boldStyle);
		}
		ExcelSheetFunctions.setRegionBorderThin(
				new CellRangeAddress(row-2, row, nextColumn, nextColumn + schuelerHeader.length - 1), sheet);

		row++;

		// borders
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(row, row + klasse.getSize() - 1, nextColumn,
				nextColumn + schuelerHeader.length - 1), sheet);

		int temp_rowNum = row;
		for (Schueler schueler : klasse.getSchueler()) {
			ExcelSheetFunctions.setCellText(sheet, temp_rowNum, nextColumn, schueler.getNachname());
			ExcelSheetFunctions.setCellText(sheet, temp_rowNum, nextColumn + 1, schueler.getVorname());
			temp_rowNum++;
		}
	}
	
	private void addGradesColumn(Sheet sheet, int row) {
		// grades
		ExcelSheetFunctions.setCellText(sheet, row, nextColumn, "  Noten  ");
		sheet.addMergedRegion(new CellRangeAddress(row, row, nextColumn, nextColumn + 2));
		sheet.getRow(row).getCell(nextColumn).setCellStyle(boldCenteredStyle);
		ExcelSheetFunctions.setRegionBorderThin(new CellRangeAddress(row-2, row, nextColumn, nextColumn + 2), sheet);
		row++;

		// set top border for cell below class list
		// when using the last cell of the class, the conditional formatting gets lost
		RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(row+klasse.getSize(), row+klasse.getSize(), nextColumn, nextColumn+2), sheet);

		totalNotenColumnindex = nextColumn + 1;
	}
}
