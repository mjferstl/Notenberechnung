package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import excel.ExcelWorkbookCreator;
import extras.Error;
import log.IF_Log;
import log.Log;
import school.ExerciseInterface;
import school.NormalExercise;
import school.SchoolClass;
import school.TextproductionExercise;
import utils.UpdatePublisher;

public class MainGUI implements UpdatePublisher, IF_GUI {
	
	// shell
	protected Shell shell;
	
	public final static int BACKGROUND_COLOR_RGB_RED = 245;
	public final static int BACKGROUND_COLOR_RGB_GREEN = 245;
	public final static int BACKGROUND_COLOR_RGB_BLUE = 245;
	
	// strings
	private final String ARROW_UPWARDS = "\u2191";
	private final String ARROW_DOWNWARDS = "\u2193";

	private final String[] titles = { "", "Bezeichnung", "Bewertung" };

	// buttons
	private Button btnRemoveTask;
	private Button btnMoveUp;
	private Button btnMoveDown;
	private OpenFileButton btnOpenExcel;

	// custom objects
	private SchoolClass schoolClass;

	// tables
	private static Table tabExercises;
	
	private Log log;
	
	
	public MainGUI(Log log) {
		this.log = log;
	}
	
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

		final MainGUI gui = this;

		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(MainGUI.class, "/gui/icon.png"));
		shell.setSize(600, 400);
		shell.setText("Erstellen einer Excel-Datei zur Notenauswertung");
		shell.setLayout(new GridLayout(3, false));
		shell.setBackground(
				new Color(shell.getDisplay(), BACKGROUND_COLOR_RGB_RED, BACKGROUND_COLOR_RGB_GREEN, BACKGROUND_COLOR_RGB_BLUE, 0));

		Color transparentBackgroundColor = new Color(shell.getDisplay(), 255, 255, 255, 0);

		Label lblSchoolClassList = new Label(shell, SWT.NONE);
		lblSchoolClassList.setText("Klassenliste");
		lblSchoolClassList.setBackground(transparentBackgroundColor);

		Label lblSchoolClassFile = new Label(shell, SWT.NONE);
		lblSchoolClassFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblSchoolClassFile.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

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
					addLogMessage("Keine Datei ausgwählt", IF_Log.LOG_ERROR);
					lblSchoolClassFile.setText("Keine Datei ausgwählt");
					lblSchoolClassFile.requestLayout();
				} else {

					String fileName = fd.getFileName().toString();
					String fileDirectory = fd.getFilterPath().toString();

					lblSchoolClassFile.setText(fileName);
					lblSchoolClassFile.requestLayout();

					addLogMessage("Klassenliste ausgewählt", IF_Log.LOG_INFO);

					schoolClass = new SchoolClass();
					String filePath = fileDirectory + "\\" + fileName;
					File selectedFile = new File(filePath);
					Error error = schoolClass.loadStudentsFromFile(selectedFile);

					// update logwindow
					if (error.getErrorLevel() == 0) {
						addLogMessage(error.getMessage(), IF_Log.LOG_SUCCESS);
					} else {
						addLogMessage(error.getMessage(), IF_Log.LOG_ERROR);
					}
				}
			}
		});
		btnBrowse.setText("Datei einlesen");

		Label lblExercises = new Label(shell, SWT.NONE);
		lblExercises.setText("Aufgaben");
		lblExercises.setBackground(transparentBackgroundColor);

		tabExercises = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		tabExercises.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if ((tabExercises.getItemCount() > 0)) {
					int selectedIndex = tabExercises.getSelectionIndex();
					TableItem ti = tabExercises.getItem(selectedIndex);
					ExerciseInterface exercise;
					if (ti.getText(0) == NormalExercise.SHORT_KEY) {
						exercise = NormalExercise.parseTextToAufgabe(ti.getText(1), ti.getText(2));
					} else if (ti.getText(0) == TextproductionExercise.SHORT_KEY) {
						exercise = TextproductionExercise.parseTextToTextproduktion(ti.getText(1), ti.getText(2));
					} else {
						return;
					}

					ExerciseDialog exerciseDialog = new ExerciseDialog(gui, shell, exercise, selectedIndex);
					exerciseDialog.open();
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

		Group group = new Group(shell, SWT.NONE);
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		btnMoveUp = new Button(group, SWT.NONE);
		btnMoveUp.setBounds(3, 47, 50, 25);
		btnMoveUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((tabExercises.getItemCount() != 0) && (tabExercises.getSelection() != null)
						&& (tabExercises.getSelectionIndex() != 0)) {
					int selectedItem = tabExercises.getSelectionIndex();
					moveExercise(selectedItem, "upwards");
				}
			}
		});
		btnMoveUp.setText(ARROW_UPWARDS);

		Button btnAddExercise = new Button(group, SWT.NONE);
		btnAddExercise.setBounds(3, 10, 50, 25);
		btnAddExercise.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openNewExerciseDialog(gui);
			}
		});
		btnAddExercise.setText("+");

		btnRemoveTask = new Button(group, SWT.NONE);
		btnRemoveTask.setBounds(56, 10, 50, 25);
		btnRemoveTask.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeTaskFromTable(tabExercises);
			}
		});
		btnRemoveTask.setText("-");

		btnMoveDown = new Button(group, SWT.NONE);
		btnMoveDown.setBounds(3, 78, 50, 25);
		btnMoveDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((tabExercises.getItemCount() != 0) && (tabExercises.getSelection() != null)
						&& (tabExercises.getSelectionIndex() != tabExercises.getItemCount())) {
					int selectedItem = tabExercises.getSelectionIndex();
					moveExercise(selectedItem, "downwards");
				}
			}
		});
		btnMoveDown.setText(ARROW_DOWNWARDS);

		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		Button btnCreateExcel = new Button(shell, SWT.NONE);
		btnCreateExcel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		btnCreateExcel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createExcelFile();
			}
		});
		btnCreateExcel.setText("Excel erstellen");
		
		Label space = new Label(shell, SWT.FILL);
		space.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		space.setBackground(transparentBackgroundColor);
		
		btnOpenExcel = new OpenFileButton(this, shell, SWT.NONE);
		btnOpenExcel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		btnOpenExcel.setText("Excel öffnen");
		btnOpenExcel.deactivate();

		log.createSwtLog(shell);
		log.getSwtLog().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		log.getSwtLog().setBackground(transparentBackgroundColor);

		Label versionInfoLabel = new Label(shell, SWT.NONE);
		versionInfoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1));
		versionInfoLabel.setText("v" + Main.VERSION);
		versionInfoLabel.setBackground(transparentBackgroundColor);

		// set all buttons enabled/disabled depending on the current contents
		setButtonsEnables();
	}
	
	/**
	 * Method to start creating the excel worksheet based on the school class file,
	 * which the user selected, and the tasks, which have been created
	 * 
	 * @author Mathias Ferstl
	 * @date 13.10.2020
	 * @version 1.0
	 */
	private void createExcelFile() {
		
		btnOpenExcel.deactivate();
		
		addLogMessage("Excel-Datei wird erstellt...", IF_Log.LOG_INFO);
		List<ExerciseInterface> exercises = parseExercisesFromGUI();
		ExcelWorkbookCreator creator = new ExcelWorkbookCreator(this, schoolClass, exercises);
		File excelFile = creator.createXlsxFile();
		
		if (excelFile != null && excelFile.exists()) {
			btnOpenExcel.activate(excelFile);
		} else {
			btnOpenExcel.deactivate();
		}
	}
	
	private void openNewExerciseDialog(IF_GUI par) {
		// open a new dialog for creating a task
		ExerciseDialog na = new ExerciseDialog(par, shell);
		na.open();

		// update the buttons
		setButtonsEnables();
	}

	/**
	 * Set the buttons of the GUI enabled/disabled, depending on the created tasks
	 * etc.
	 * 
	 * @author Mathias Ferstl
	 * @date 13.10.2020
	 * @version 1.0
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
	
	public void addLogMessage(String message) {
		log.addMessage(message);
	}

	public void addLogMessage(String message, int logLevel) {		
		log.addMessage(message, logLevel);
	}

	/**
	 * Method to parse the created exercises from the GUI table
	 * 
	 * @return list of objects implementing the ExerciseInterface
	 * 
	 * @author Mathias Ferstl
	 * @date 22.10.2020
	 * @version 1.1
	 */
	private List<ExerciseInterface> parseExercisesFromGUI() {

		List<ExerciseInterface> exerciseList = new ArrayList<>();

		for (int i = 0; i < tabExercises.getItemCount(); i++) {
			TableItem ti = tabExercises.getItem(i);
			ExerciseInterface exercise = parseTableItemToExercise(ti);
			if (exercise != null) {
				exerciseList.add(exercise);
			}
		}

		return exerciseList;
	}

	private ExerciseInterface parseTableItemToExercise(TableItem tableItem) {

		String bezeichnung = tableItem.getText(1);
		String text = tableItem.getText(2);
		
		switch (tableItem.getText(0)) {
		case NormalExercise.SHORT_KEY:
			NormalExercise exercise = NormalExercise.parseTextToAufgabe(bezeichnung, text);
			if (exercise != null) {
				return exercise;
			}
			break;
		case TextproductionExercise.SHORT_KEY:
			TextproductionExercise textproduction = TextproductionExercise.parseTextToTextproduktion(bezeichnung, text);
			if (textproduction != null) {
				return textproduction;
			}
			break;
		default:
			return null;
		}
		return null;
	}

	/**
	 * Method to add a task to the table
	 * 
	 * @param task: object of interface ExerciseInterface
	 * 
	 * @author Mathias Ferstl
	 * @date 25.10.2020
	 * @version 1.2
	 */
	public void addTask(ExerciseInterface task) {
		TableItem item = new TableItem(tabExercises, SWT.NONE);
		item.setText(0, task.getTypeShortName());
		item.setText(1, task.getName());
		item.setText(2, task.getConfig());
		fitTableColumnsWidth(tabExercises);
		
		btnOpenExcel.deactivate();

		addLogMessage(String.format("Aufgabe \"%s\" erstellt.", task.getName()));
	}

	/**
	 * Method to update a task, which is already in the GUI's table
	 * 
	 * @param task:       object of interface ExerciseInterface
	 * @param tableIndex: index of the table item, which should be updated
	 * 
	 * @author Mathias Ferstl
	 * @date 25.10.2020
	 * @version 1.2
	 */
	public void updateTask(ExerciseInterface task, int tableIndex) {
		TableItem item = tabExercises.getItem(tableIndex);
		item.setText(0, task.getTypeShortName());
		item.setText(1, task.getName());
		item.setText(2, task.getConfig());
		fitTableColumnsWidth(tabExercises);
		
		btnOpenExcel.deactivate();

		addLogMessage(String.format("Aufgabe \"%s\" aktualisiert", task.getName()), IF_Log.LOG_INFO);
	}

	/**
	 * Remove a task from the table. The item to remove is the currently selected item
	 * 
	 * @param table Table, which contains the item, that should be removed
	 * 
	 * @author Mathias Ferstl
	 * @date 22.10.2020
	 * @version 1.0
	 */
	private void removeTaskFromTable(Table table) {
		if ((table.getItemCount() != 0) && (table.getSelectionIndex() >= 0)) {
			int index = table.getSelectionIndex();
			TableItem item = table.getItem(index);
			ExerciseInterface exercise = parseTableItemToExercise(item);
			
			table.remove(index);
			
			addLogMessage(String.format("Aufgabe \"%s\" gelöscht", exercise.getName()), IF_Log.LOG_INFO);

			// update the buttons
			setButtonsEnables();
		}
		
		btnOpenExcel.deactivate();
	}

	/**
	 * Method to set the width of all columns in the table to it's longest content
	 * 
	 * @author Mathias Ferstl
	 * @date 13.10.2020
	 * @version 1.0
	 */
	private void fitTableColumnsWidth(Table t) {
		for (int i = 0, n = t.getColumnCount(); i < n; i++) {
			t.getColumn(i).pack();
		}
	}

	/**
	 * Method to move table items up- or downwards in the GUI's table
	 * 
	 * @param index:     number representing the index of the table item in the
	 *                   table
	 * @param direction: String containing "upwards" or "downwards"
	 * 
	 * @author Mathias Ferstl
	 * @date 25.10.2020
	 * @version 1.1
	 */
	private void moveExercise(int index, String direction) {
		if (index >= 0 && (direction == "upwards" || direction == "downwards")) {
			TableItem tableItem = tabExercises.getItem(index);
			String[] tableItemContent = { tableItem.getText(0), tableItem.getText(1), tableItem.getText(2) };
			tableItem.dispose();
			
			TableItem newTableItem;
			if (direction == "upwards" && index > 0) {
				newTableItem = new TableItem(tabExercises, SWT.NONE, index - 1);
			} else if (direction == "downwards") {
				newTableItem = new TableItem(tabExercises, SWT.NONE, index + 1);
			} else {
				newTableItem = null;
				return;
			}
			newTableItem.setText(tableItemContent);
			
			ExerciseInterface exercise = parseTableItemToExercise(newTableItem);
			String logMessage = String.format("Aufgabe \"%s\" verschoben", exercise.getName());
			log.addMessage(logMessage);
			
			btnOpenExcel.deactivate();
		}
	}

	@Override
	public void publishUpdate(String message, int logLevel) {
		addLogMessage(message, logLevel);
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
