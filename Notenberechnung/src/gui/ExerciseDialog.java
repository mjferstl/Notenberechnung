package gui;

import log.LogType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import school.exercise.*;

public class ExerciseDialog extends Dialog {

	// doubles
	private final double ERROR_ID = 999999;

	// labels
	private Label lblText1, lblText2, lblText3;

	// edit text fields
	private Text editText1, editText2, editText3;

	// buttons
	private Button btnRadioAufgabe, btnRadioTP;

	// strings
	private final String AUFGABE = "Aufgabe";
	private final String TEXTPRODUCTION = "Textproduktion";
	private final String CONTENT = TextproductionExercise.CONTENT;
	private final String LANGUAGE = TextproductionExercise.LANGUAGE;
	private final String WEIGHTING = NormalExercise.WEIGHTING;
	private final String BE = NormalExercise.BE;

	// boolean
	private boolean loadTask;

	// integers
	private int tableIndex;

	// objects
	private Exercise importedTask;
	private Text tbTaskName;

	private IF_GUI guiInterface;

	/**
	 * InputDialog constructor
	 * 
	 * @param parent: the parent
	 */
	public ExerciseDialog(IF_GUI parentGui, Shell parent) {
		// Pass the default styles here
		this(parentGui, parent, SWT.TITLE | SWT.APPLICATION_MODAL);
		this.guiInterface = parentGui;
	}

	/**
	 * InputDialog constructor
	 * 
	 * @param parent the parent
	 * @param style  the style
	 */
	public ExerciseDialog(IF_GUI par, Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Aufgabe hinzufügen");
		loadTask = false;
		this.guiInterface = par;
	}


	public ExerciseDialog(IF_GUI par, Shell parent, Exercise task, int index) {
		// Pass the default styles here
		this(par, parent, SWT.DIALOG_TRIM);
		setText("Aufgabe editieren");

		//
		importedTask = task;
		loadTask = true;
		tableIndex = index;
	}

	/**
	 * Opens the dialog
	 */
	public void open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setSize(350, 400);
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		// display
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		int BACKGROUND_COLOR_RED = MainGUI.BACKGROUND_COLOR_RGB_RED;
		int BACKGROUND_COLOR_GREEN = MainGUI.BACKGROUND_COLOR_RGB_GREEN;
		int BACKGROUND_COLOR_BLUE = MainGUI.BACKGROUND_COLOR_RGB_BLUE;
		shell.setBackground(
				new Color(shell.getDisplay(), BACKGROUND_COLOR_RED, BACKGROUND_COLOR_GREEN, BACKGROUND_COLOR_BLUE));

		Color transparentBackgroundColor = new Color(shell.getDisplay(), 255, 255, 255, 0);

		// group
		Group groupAufgabentyp = new Group(shell, SWT.NONE);
		GridData gridDataAufgabentyp = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gridDataAufgabentyp.heightHint = 73;
		groupAufgabentyp.setLayoutData(gridDataAufgabentyp);
		groupAufgabentyp.setText("Aufgabentyp");
		groupAufgabentyp.setBackground(transparentBackgroundColor);

		// Radio Button Aufgabe
		btnRadioAufgabe = new Button(groupAufgabentyp, SWT.RADIO);
		btnRadioAufgabe.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 1));
		btnRadioAufgabe.setBounds(10, 30, 66, 16);
		btnRadioAufgabe.setBackground(transparentBackgroundColor);
		btnRadioAufgabe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				// if selected
				if (source.getSelection()) {
					lblText1.setText(BE);
					lblText2.setText(WEIGHTING);
					lblText3.setText(" ");
					lblText2.requestLayout();
					editText1.setEnabled(true);
					editText2.setEnabled(true);
					editText3.setEnabled(false);
				}
			}
		});
		btnRadioAufgabe.setText(AUFGABE);

		// Radio Button Textproduktion
		btnRadioTP = new Button(groupAufgabentyp, SWT.RADIO);
		btnRadioTP.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 1));
		btnRadioTP.setBounds(10, 52, 101, 16);
		btnRadioTP.setBackground(transparentBackgroundColor);
		btnRadioTP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				// if selected
				if (source.getSelection()) {
					lblText1.setText(CONTENT);
					lblText1.requestLayout();
					lblText2.setText(LANGUAGE);
					lblText3.setText(WEIGHTING);
					lblText3.requestLayout();
					editText1.setEnabled(true);
					editText2.setEnabled(true);
					editText3.setEnabled(true);
				}
			}
		});
		btnRadioTP.setText(TEXTPRODUCTION);

		Label lblTaskName = new Label(shell, SWT.NONE);
		lblTaskName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblTaskName.setText("Bezeichnung");
		lblTaskName.setBackground(transparentBackgroundColor);

		tbTaskName = new Text(shell, SWT.BORDER);
		tbTaskName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label sepHoriz = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_sepHoriz = new GridData(SWT.CENTER, SWT.FILL, true, false, 2, 1);
		gd_sepHoriz.widthHint = 144;
		sepHoriz.setLayoutData(gd_sepHoriz);

		// ---------------------------------------------------------------------------------------
		// Label Text1
		lblText1 = new Label(shell, SWT.NONE);
		lblText1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblText1.setText(BE);
		lblText1.setBackground(transparentBackgroundColor);
		// Edit Text
		editText1 = new Text(shell, SWT.BORDER);
		editText1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// ---------------------------------------------------------------------------------------
		// Label Text2
		lblText2 = new Label(shell, SWT.NONE);
		lblText2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblText2.setText(WEIGHTING);
		lblText2.setBackground(transparentBackgroundColor);
		// Edit Text
		editText2 = new Text(shell, SWT.BORDER);
		editText2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// ---------------------------------------------------------------------------------------
		// Label Text3
		lblText3 = new Label(shell, SWT.NONE);
		lblText3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblText3.setText("");
		lblText3.setBackground(transparentBackgroundColor);
		// Edit Text
		editText3 = new Text(shell, SWT.BORDER);
		editText3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button btnOk = new Button(shell, SWT.PUSH);
		btnOk.setText("OK");
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (btnRadioAufgabe.getSelection()) {
					double be = getDoubleFromInput(editText1.getText());
					double gewichtung = getDoubleFromInput(editText2.getText());
					// get the specified task name
					String bezeichnung = tbTaskName.getText();
					if (bezeichnung.trim().equals("")) {
						bezeichnung = AUFGABE;
					}

					if (gewichtung == 0.0) {
						gewichtung = 1.0;
					}
					if (be != ERROR_ID && gewichtung != ERROR_ID) {
						Exercise a = new NormalExercise(bezeichnung, new NormalExerciseEvaluation(gewichtung, be));
						if (loadTask) {
							guiInterface.updateTask(a, tableIndex);
						} else {
							guiInterface.addTask(a);
						}
						shell.close();
					}
				} else if (btnRadioTP.getSelection()) {
					double inhalt = getDoubleFromInput(editText1.getText());
					double sprache = getDoubleFromInput(editText2.getText());
					double gewichtung = getDoubleFromInput(editText3.getText());
					// get the specified task name
					String bezeichnung = tbTaskName.getText();
					if (bezeichnung.trim().equals("")) {
						bezeichnung = TEXTPRODUCTION;
					}

					if (gewichtung == 0.0) {
						gewichtung = 1.0;
					}
					if (inhalt != ERROR_ID && sprache != ERROR_ID && gewichtung != ERROR_ID) {
						Exercise tp = new TextproductionExercise(bezeichnung, new TextProductionEvaluation(gewichtung, inhalt, sprache));
						if (loadTask) {
							guiInterface.updateTask(tp, tableIndex);
						} else {
							guiInterface.addTask(tp);
						}
						shell.close();
					}
				}
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button btnCancel = new Button(shell, SWT.PUSH);
		btnCancel.setText("Abbrechen");
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(btnOk);

		// optional load a specific task
		if (loadTask) {
			loadImportedTask();
		}
	}

	/**
	 * Load the data of a task to the GUI
	 */
	private void loadImportedTask() {

		tbTaskName.setText(importedTask.getName());

		if (importedTask.getClass() == NormalExercise.class) {
			lblText1.setText(NormalExercise.BE);
			lblText2.setText(NormalExercise.WEIGHTING);
			lblText3.setText("");
			btnRadioAufgabe.setSelection(true);
			btnRadioTP.setSelection(false);
			editText3.setEnabled(false);
		} else if (importedTask.getClass() == TextproductionExercise.class) {
			lblText1.setText(TextproductionExercise.CONTENT);
			lblText2.setText(TextproductionExercise.LANGUAGE);
			lblText3.setText(TextproductionExercise.WEIGHTING);
			btnRadioAufgabe.setSelection(false);
			btnRadioTP.setSelection(true);
		}

		// set entries in the text fields
		String first = "", second = "", third = "";
		switch (importedTask.getExerciseType()) {
			case NORMAL_TASK: {
				NormalExercise exercise = (NormalExercise) importedTask;
				NormalExerciseEvaluation evaluation = exercise.getEvaluation();
				first = String.valueOf(evaluation.getBE());
				second = String.valueOf(evaluation.getWeighting());
				break;
			}
			case TEXT_PRODUCTION: {
				TextproductionExercise exercise = (TextproductionExercise) importedTask;
				TextProductionEvaluation evaluation = exercise.getEvaluation();
				first = String.valueOf(evaluation.getPointsContent());
				second = String.valueOf(evaluation.getPointsLanguage());
				third = String.valueOf(evaluation.getWeighting());
				break;
			}
			case UNDEFINED: {
				throw new IllegalStateException("The imported task " + importedTask.getName() + " has an invald exercise type: " + importedTask.getExerciseType());
			}
		}
		editText1.setText(first);
		editText2.setText(second);
		editText3.setText(third);
	}

	/**
	 * Converts a String to a double value
	 * An empty String will be converted to 0.0
	 * 
	 * @param string string to convert
	 * @return converted double value
	 */
	private double getDoubleFromInput(String string) {

		if (string.trim().equals("")) {
			return 0.0;
		} else {
			try {
				return Double.parseDouble(string);
			} catch (NumberFormatException e) {
				updateLogwindow("Input konnte nicht verarbeitet werden.", LogType.ERROR);
				return ERROR_ID;
			}
		}
	}

	/**
	 * update the label at the bottom of the gui
	 *
	 * @param text text to be shown in the label of the "logwindow"
	 */
	private void updateLogwindow(String text, LogType logType) {
		this.guiInterface.addLogMessage(text, logType);
	}
}
