package log;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class SWTLog extends Group {
	
	private final int numColumns = 1;
	private LogTable logTable;

	public SWTLog(Composite parent) {
		super(parent, SWT.NONE);
		this.setText("Log");
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		this.setLayout(gridLayout);
		
		addLogTable();
	}
	
	private void addLogTable() {
		logTable = new LogTable(this);
		GridData logTableGridData = new GridData(SWT.FILL, SWT.FILL, true, false, numColumns, 1);
		logTable.setLayoutData(logTableGridData);
	}
	
	public void addLogMessage(String logMessage, Color color) {
		if (this.logTable != null && !this.logTable.isDisposed()) {
			logTable.addLogMessage(logMessage, color);
		}
	}
	
	@Override
	protected void checkSubclass() {
		// to make subclassing Table possible
	}
}
