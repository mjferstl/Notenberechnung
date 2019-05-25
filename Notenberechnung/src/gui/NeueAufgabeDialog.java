package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import school.Aufgabe;
import school.Textproduktion;

public class NeueAufgabeDialog extends Dialog {

	// doubles
	private double errorId = 999999;
	
	// labels
	private Label lblText1, lblText2, lblText3;
	private Label logwindow;
	
	// edit text fields
	private Text editText1, editText2, editText3;
	
	// shell
	private Shell shell_1;
	
	// group
	private Group grpAufgabentyp;
	
	// buttons
	private Button btnRadioAufgabe, btnRadioTP;
	
	// display
	private Display display;

	// strings
	private final String AUFGABE = "Aufgabe";
	private final String TP = "Textproduktion";
	private final String INHALT = Textproduktion.INHALT;
	private final String SPRACHE = Textproduktion.SPRACHE;
	private final String GEWICHTUNG = Aufgabe.GEWICHTUNG;
	private final String BE = Aufgabe.BE;
	
	
	/**
	 * InputDialog constructor
	 * 
	 * @param parent the parent
	 */
	public NeueAufgabeDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * InputDialog constructor
	 * 
	 * @param parent the parent
	 * @param style  the style
	 */
	public NeueAufgabeDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Aufgabe hinzufügen");
	}


	/**
	 * InputDialog constructor for selection Textproduktion with values
	 * 
	 * @param parent    the parent
	 * @param style     the style
	 * @param num1      is the number in the first edit text field at start
	 * @param num2      is the number in the second edit text field at start
	 * @param num3      is the number in the third edit text field at start
	 */
	public NeueAufgabeDialog(Shell parent, double num1, double num2, double num3) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		setText("Aufgabe hinzufügen");

		// update radio buttons
		btnRadioTP.setEnabled(true);
		btnRadioAufgabe.setEnabled(false);

		// update labels
		lblText1.setText(INHALT);
		lblText1.requestLayout();
		lblText2.setText(SPRACHE);
		lblText2.requestLayout();
		lblText3.setText(GEWICHTUNG);
		lblText3.requestLayout();

		// update edit text fields
		editText1.setText(String.valueOf(num1));
		editText2.setText(String.valueOf(num2));
		editText3.setText(String.valueOf(num3));
	}
	

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public void open() {
		// Create the dialog window
		shell_1 = new Shell(getParent(), getStyle());
		shell_1.setSize(173, 359);
		shell_1.setText(getText());
		createContents(shell_1);
		shell_1.pack();
		shell_1.open();
		display = getParent().getDisplay();
		while (!shell_1.isDisposed()) {
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

		GridLayout gl_shell_1 = new GridLayout();
		gl_shell_1.numColumns = 2;
		gl_shell_1.makeColumnsEqualWidth = true;
		shell.setLayout(gl_shell_1);

		GridData data = new GridData();
		data = new GridData(GridData.BEGINNING);
		data = new GridData(GridData.END);

		grpAufgabentyp = new Group(shell_1, SWT.NONE);
		GridData gd_grpAufgabentyp = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_grpAufgabentyp.heightHint = 72;
		grpAufgabentyp.setLayoutData(gd_grpAufgabentyp);
		grpAufgabentyp.setText("Aufgabentyp");

		// Radio Button Aufgabe
		btnRadioAufgabe = new Button(grpAufgabentyp, SWT.RADIO);
		btnRadioAufgabe.setBounds(10, 30, 66, 16);
		btnRadioAufgabe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				// if selected
				if (source.getSelection()) {
					lblText1.setText(BE);
					lblText2.setText(GEWICHTUNG);
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
		btnRadioTP = new Button(grpAufgabentyp, SWT.RADIO);
		btnRadioTP.setBounds(10, 52, 101, 16);
		btnRadioTP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				// if selected
				if (source.getSelection()) {
					lblText1.setText(INHALT);
					lblText1.requestLayout();
					lblText2.setText(SPRACHE);
					lblText3.setText(GEWICHTUNG);
					lblText3.requestLayout();
					editText1.setEnabled(true);
					editText2.setEnabled(true);
					editText3.setEnabled(true);
				}
			}
		});
		btnRadioTP.setText(TP);

		// ---------------------------------------------------------------------------------------
		// Label Text1
		lblText1 = new Label(shell, SWT.NONE);
		lblText1.setText(BE);
		// Edit Text
		editText1 = new Text(shell, SWT.BORDER);

		// ---------------------------------------------------------------------------------------
		// Label Text2
		lblText2 = new Label(shell, SWT.NONE);
		lblText2.setText(GEWICHTUNG);
		// Edit Text
		editText2 = new Text(shell, SWT.BORDER);

		// ---------------------------------------------------------------------------------------
		// Label Text3
		lblText3 = new Label(shell, SWT.NONE);
		lblText3.setText("");
		// Edit Text
		editText3 = new Text(shell, SWT.BORDER);

		logwindow = new Label(shell_1, SWT.NONE);
		logwindow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		data = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (btnRadioAufgabe.getSelection()) {
					double be = getDoubleFromInput(editText1.getText());
					double gewichtung = getDoubleFromInput(editText2.getText());
					if (gewichtung == 0.0 ) {
						gewichtung = 1.0;
					}
					if (be != errorId && gewichtung != errorId) {
						Aufgabe a = new Aufgabe("Aufgabe", be, gewichtung);
						Notenberechnung_GUI.addAufgabentyp(a);
						shell.close();
					}
				} else if (btnRadioTP.getSelection()) {
					double inhalt = getDoubleFromInput(editText1.getText());
					double sprache = getDoubleFromInput(editText2.getText());
					double gewichtung = getDoubleFromInput(editText3.getText());
					if (gewichtung == 0.0 ) {
						gewichtung = 1.0;
					}
					if (inhalt != errorId && sprache != errorId && gewichtung != errorId) {
						Textproduktion tp = new Textproduktion("Textproduktion", inhalt, sprache, gewichtung);
						Notenberechnung_GUI.addAufgabentyp(tp);
						shell.close();
					}
				}
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}

	private double getDoubleFromInput(String string) {

		if (string == "") {
			return 0.0;
		} else {
			try {
				return Double.parseDouble(string);
			} catch (NumberFormatException e) {
				updateLogwindow("Input konnte nicht verarbeitet werden.", "red");
				return errorId;
			}
		}
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

//	public synchronized void updateContent(double be, double gewichtung) {
//		
//		if (display == null || display.isDisposed()) 
//            return;
//		display.asyncExec(new Runnable() {
//
//            public void run() {
//            	// update labels
//        		lblText1.setText(BE);
//        		lblText1.requestLayout();
//        		lblText2.setText(GEWICHTUNG);
//        		lblText2.requestLayout();
//        		lblText3.setText("");
//        		lblText3.requestLayout();
//        		
//        		// update edit text fields
//        		editText1.setText(String.valueOf(be));
//        		editText2.setText(String.valueOf(gewichtung));
//        		editText3.setText("");	
//            }
//        });
//		
//		// update radio buttons
////		btnRadioTP.setEnabled(false);
////		btnRadioAufgabe.setEnabled(true);
//
//	}
	
	public void updateContent(double be, double gewichtung) {
		
		Display.getDefault().asyncExec(() -> editText1.setText(String.valueOf(be)));
		Display.getDefault().asyncExec(() -> editText2.setText(String.valueOf(gewichtung)));
	
	// update radio buttons
//	btnRadioTP.setEnabled(false);
//	btnRadioAufgabe.setEnabled(true);

	}
	
	
	
	public void updateContent(double punkteInhalt, double punkteSprache, double gewichtung) {
		// update radio buttons
//		btnRadioTP.setEnabled(false);
//		btnRadioAufgabe.setEnabled(true);

		// update labels
		lblText1.setText(INHALT);
		lblText1.requestLayout();
		lblText2.setText(SPRACHE);
		lblText2.requestLayout();
		lblText3.setText(GEWICHTUNG);
		lblText3.requestLayout();

		// update edit text fields
		editText1.setText(String.valueOf(punkteInhalt));
		editText2.setText(String.valueOf(punkteSprache));
		editText3.setText(String.valueOf(gewichtung));				
	}
}
