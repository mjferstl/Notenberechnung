package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import school.Schueler;
import school.Schulklasse;

import org.eclipse.swt.graphics.Point;


public class PupilsListDialog extends Dialog {

	// shell
	private Shell shell_2;
	private Table table;
	private Schulklasse klasse;

	/**
	 * InputDialog constructor
	 * 
	 * @param parent: the parent
	 */
	public PupilsListDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}


	public PupilsListDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Klassenliste");
	}
	
	public PupilsListDialog(Shell parent, Schulklasse schulklasse) {
		// Let users override the default styles
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("Klassenliste");
		this.klasse = schulklasse;
	}


	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public void open() {
		// Create the dialog window
		shell_2 = new Shell(getParent(), getStyle());
		shell_2.setMinimumSize(new Point(180, 300));
		shell_2.setSize(180, 403);
		shell_2.setText(getText());
		createContents(shell_2);
		shell_2.pack();
		shell_2.open();
		Display display = getParent().getDisplay();
		while (!shell_2.isDisposed()) {
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
		
		table = new Table(shell_2, SWT.BORDER | SWT.FULL_SELECTION);
		final TableEditor editor = new TableEditor(table);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {	
		        
			}
		});
		
		GridData gd_table = new GridData(SWT.FILL, SWT.TOP, true, true, 2, 1);
		gd_table.heightHint = 231;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		String[] coumnTitles = {Schueler.NACHNAME, Schueler.VORNAME};
		
		for (int i = 0; i < coumnTitles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(coumnTitles[i]);
		}
		
		if (klasse != null) {
			for (Schueler s : klasse.getSchueler()) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, s.getNachname());
				item.setText(1, s.getVorname());
			}
			fitTableColumnsWidth(table);
		}
		

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		data = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Abbrechen");
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
	
	/**
	 * set the width of all columns in the table to it's longest content
	 */
	private static void fitTableColumnsWidth(Table t) {
		for (int i = 0, n = t.getColumnCount(); i < n; i++) {
			t.getColumn(i).pack();
		}
	}

}
