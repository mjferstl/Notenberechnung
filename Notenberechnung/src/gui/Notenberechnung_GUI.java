package gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;

import excel.ExcelWorkbookCreator;
import extras.Error;
import school.NormalExercise;
import school.ExerciseInterface;
import school.SchoolClass;
import school.TextproductionExercise;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class Notenberechnung_GUI implements ExcelWorkbookCreator.UpdatePublisher {

	// shell
	protected Shell shell;
	
	public final static int BACKGROUND_COLOR_RED = 245;
	public final static int BACKGROUND_COLOR_GREEN = 245;
	public final static int BACKGROUND_COLOR_BLUE = 245;

	// strings
	private final String ARROW_UPWARDS = "\u2191";
	private final String ARROW_DOWNWARDS = "\u2193";
	
	private final String[] titles = {"", "Bezeichnung", "Bewertung" };
	
	// labels
	private static Label logwindow;
	private Label lblSchoolClassFile;
	private Label lblSchoolClassList;
	private Label lblExercises;
	
	// buttons
	private Button btnAddExercise;
	private Button btnRemoveTask;
	private Button btnMoveUp;
	private Button btnMoveDown;
	private Button btnBrowse;
	private Button btnCreateExcel;
	
	// custom objects
	private SchoolClass schoolClass;
	
	// tables
	private static Table tabExercises;
	private Group group;
	

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
		shell.setSize(600, 250);
		shell.setText("Erstellen einer Excel-Datei zur Notenauswertung");
		shell.setLayout(new GridLayout(3, false));
		shell.setBackground(new Color(shell.getDisplay(), BACKGROUND_COLOR_RED, BACKGROUND_COLOR_GREEN, BACKGROUND_COLOR_BLUE, 0));
		
		Color transparentBackgroundColor = new Color(shell.getDisplay(), 255, 255, 255, 0);

		lblSchoolClassList = new Label(shell, SWT.NONE);
		lblSchoolClassList.setText("Klassenliste");
		lblSchoolClassList.setBackground(transparentBackgroundColor);

		lblSchoolClassFile = new Label(shell, SWT.NONE);
		lblSchoolClassFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblSchoolClassFile.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
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
							updateLogMessage("Keine Datei ausgwählt", IF_Log.LOG_ERROR);
							lblSchoolClassFile.setText("Keine Datei ausgwählt");
							lblSchoolClassFile.requestLayout();
						} else {

							String fileName = fd.getFileName().toString();
							String fileDirectory = fd.getFilterPath().toString();

							lblSchoolClassFile.setText(fileName);
							lblSchoolClassFile.requestLayout();

							updateLogMessage("Klassenliste ausgewählt", IF_Log.LOG_INFO);

							schoolClass = new SchoolClass();
							String filePath = fileDirectory + "\\" + fileName;
							File selectedFile = new File(filePath);
							Error error = new Error();
							error = schoolClass.loadStudentsFromFile(selectedFile);

							// update logwindow
							if (error.getErrorId() == 0) {
								updateLogMessage(error.getErrorMsg(), IF_Log.LOG_SUCCESS);
							} else {
								updateLogMessage(error.getErrorMsg(), IF_Log.LOG_ERROR);
							}
						}
					}
				});
				btnBrowse.setText("Datei einlesen");

		lblExercises = new Label(shell, SWT.NONE);
		lblExercises.setText("Aufgaben");
		lblExercises.setBackground(transparentBackgroundColor);

		tabExercises = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		tabExercises.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if ((tabExercises.getItemCount() > 0)) {
					int selectedIndex = tabExercises.getSelectionIndex();
					TableItem ti = tabExercises.getItem(selectedIndex);
					if (ti.getText(0) == NormalExercise.SHORT_KEY) {
						NormalExercise a = NormalExercise.parseTextToAufgabe(ti.getText(1),ti.getText(2));
						ExerciseDialog nad = new ExerciseDialog(shell, a, selectedIndex);
						nad.open();
					} else if (ti.getText(0) == TextproductionExercise.SHORT_KEY) {
						TextproductionExercise t = TextproductionExercise.parseTextToTextproduktion(ti.getText(1),ti.getText(2));
						ExerciseDialog nad = new ExerciseDialog(shell, t, selectedIndex);
						nad.open();
					}
				}		
			}
		});
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		gd_table.heightHint = 25;
		tabExercises.setLayoutData(gd_table);
		tabExercises.setHeaderVisible(true);
		tabExercises.setLinesVisible(true);

		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(tabExercises, SWT.NONE);
			column.setText(titles[i]);
		}

		for (int i = 0; i < titles.length; i++) {
			tabExercises.getColumn(i).pack();
		}
		
		group = new Group(shell, SWT.NONE);
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		btnMoveUp = new Button(group, SWT.NONE);
		btnMoveUp.setBounds(3, 47, 50, 25);
		btnMoveUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((tabExercises.getItemCount() != 0) && (tabExercises.getSelection() != null) && (tabExercises.getSelectionIndex() != 0)) {
					int selectedItem = tabExercises.getSelectionIndex();
					moveTableItem(selectedItem, "upwards");
				}
			}
		});
		btnMoveUp.setText(ARROW_UPWARDS);		
		btnAddExercise = new Button(group, SWT.NONE);
		btnAddExercise.setBounds(3, 10, 50, 25);
		btnAddExercise.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				// open a new dialog for creating a task
				ExerciseDialog na = new ExerciseDialog(shell);
				na.open();
				
				// update the buttons
				setButtonsEnables();
			}
		});
		btnAddExercise.setText("+");
		btnRemoveTask = new Button(group, SWT.NONE);
		btnRemoveTask.setBounds(56, 10, 50, 25);
		btnRemoveTask.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((tabExercises.getItemCount() != 0) && (tabExercises.getSelectionIndex() >= 0)) {
					tabExercises.remove(tabExercises.getSelectionIndex());
					
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
				if ((tabExercises.getItemCount() != 0) && (tabExercises.getSelection() != null) && (tabExercises.getSelectionIndex() != tabExercises.getItemCount())) {
					int selectedItem = tabExercises.getSelectionIndex();
					moveTableItem(selectedItem, "downwards");
				}
				
			}
		});
		btnMoveDown.setText(ARROW_DOWNWARDS);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		btnCreateExcel = new Button(shell, SWT.NONE);
		btnCreateExcel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		btnCreateExcel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createExcelFile();				
			}
		});
		btnCreateExcel.setText("Excel erstellen");

		logwindow = new Label(shell, SWT.NONE);
		logwindow.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		logwindow.setText(" ");
		logwindow.setBackground(transparentBackgroundColor);
		
		// set all buttons enabled/disabled depending on the current contents
		setButtonsEnables();
	}
	
	private void createExcelFile() {
		updateLogMessage("Excel-Datei wird erstellt...", IF_Log.LOG_INFO);
		List<ExerciseInterface> exercises = parseExercisesFromGUI();
		ExcelWorkbookCreator creator = new ExcelWorkbookCreator(this, schoolClass, exercises);
		creator.createXlsxFile();
	}

	
	/**
	 * Set the buttons of the GUI enabled/disabled
	 */
	private void setButtonsEnables() {
		
		// buttons for moving items up and down
		if (tabExercises.getItemCount() >= 2) {
			btnMoveUp.setEnabled(true);
			btnMoveDown.setEnabled(true);
		} else {
			btnMoveUp.setEnabled(false);
			btnMoveDown.setEnabled(false);
		}
		
		if (tabExercises.getItemCount() == 0) {
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
	public void updateLogMessage(String text) {
		updateLogMessage(text,IF_Log.LOG_INFO);
	}

	/**
	 * Update the label called "logwindow" and set a specific text color
	 * 
	 * @param text: text to be set in the logwindow label
	 * @param color: string containing the text color. possible colors: blue, red, green, black
	 */
	@Deprecated
	public void updateLogMessage(String text, String color) {
		switch (color) {
		case "blue":
			updateLogMessage(text, SWTResourceManager.getColor(SWT.COLOR_BLUE));
			break;
		case "red":
			updateLogMessage(text, SWTResourceManager.getColor(SWT.COLOR_RED));
			break;
		case "green":
			updateLogMessage(text, SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
			break;
		default:
			updateLogMessage(text, SWTResourceManager.getColor(SWT.COLOR_BLACK));
		}
	}
	
	private void updateLogMessage(String message, int logLevel) {
		Color logColor;
		
		switch (logLevel) {
		case IF_Log.LOG_ERROR:
			logColor = IF_Log.RED;
			break;
		case IF_Log.LOG_SUCCESS:
			logColor = IF_Log.GREEN;
			break;
		case IF_Log.LOG_INFO:
			logColor = IF_Log.BLACK;
			break;
		default:
			logColor = IF_Log.BLACK;
		}
		updateLogMessage(message, logColor);
	}
	
	private void updateLogMessage(String message, Color color) {
		logwindow.setText(message);
		logwindow.setForeground(color);
		logwindow.requestLayout();
	}


	private List<ExerciseInterface> parseExercisesFromGUI() {
		
		List<ExerciseInterface> exerciseList = new ArrayList<>();
		
		String bezeichnung, text;
		for (int i = 0; i < tabExercises.getItemCount(); i++) {
			TableItem ti = tabExercises.getItem(i);
			bezeichnung = ti.getText(1);
			text = ti.getText(2);
			switch (ti.getText(0)) {
			case NormalExercise.SHORT_KEY:
				NormalExercise exercise = NormalExercise.parseTextToAufgabe(bezeichnung, text);
				if (exercise != null) {
					exerciseList.add(exercise);
				}
				break;
			case TextproductionExercise.SHORT_KEY:
				TextproductionExercise textproduction = TextproductionExercise.parseTextToTextproduktion(bezeichnung, text);
				if (textproduction != null) {
					exerciseList.add(textproduction);
				}
				break;
			}
		}
		
		return exerciseList;
	}

	/**
	 * add a task to the table
	 * 
	 * @param task: object of interface Aufgabentyp
	 */
	public static void addTask(ExerciseInterface task) {
		TableItem item = new TableItem(tabExercises, SWT.NONE);
		item.setText(0, task.getTypeShortName());
		item.setText(1, task.getName());
		item.setText(2, task.getConfig());
		fitTableColumnsWidth(tabExercises);
	}
	
	/**
	 * add a task to the table
	 * 
	 * @param task: object of interface Aufgabentyp
	 * @param tableIndex: index of the table item to be updated
	 */
	public static void updateTask(ExerciseInterface task, int tableIndex) {		
		TableItem item = tabExercises.getItem(tableIndex);
		item.setText(0, task.getTypeShortName());
		item.setText(1, task.getName());
		item.setText(2, task.getConfig());
		fitTableColumnsWidth(tabExercises);
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
	 * moving table items up- or downwards in the table
	 * 
	 * @param index: number representing the index of the table item in the table
	 * @param direction: String containing "upwards" or "downwards"
	 */
	private void moveTableItem(int index, String direction) {
		if (index >= 0 && (direction == "upwards" || direction == "downwards")) {
			 TableItem ti = tabExercises.getItem(index);
			 String[] tiContent = {ti.getText(0), ti.getText(1), ti.getText(2)};
			 ti.dispose();
			 TableItem nti = null;
			 
			 
			 if (direction == "upwards" && index > 0) {
				 nti = new TableItem(tabExercises, SWT.NONE, index-1);	
			 } else if (direction == "downwards") {
				 nti = new TableItem(tabExercises, SWT.NONE, index+1);
			 }			 			 
			 nti.setText(tiContent);		
		}
	}

	@Override
	public void printUpdate(String message, int logLevel) {
		updateLogMessage(message, logLevel);
	}

	@Override
	public Shell getShell() {
		if (this.shell != null && !this.shell.isDisposed()) {
			return this.shell;
		} else {
			return null;
		}
			
	}
}
