package log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class LogTable extends Table {
	
	private final static int style = SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL;
	private final int minHeightHint = 70;
	private final int numColumns = 2;
	
	private final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");

	public LogTable(Composite parent) {
		super(parent, style);
		this.setHeaderVisible(false);
		this.setLinesVisible(true);
		
		for (int i=0; i<numColumns; i++) {
			new TableColumn(this, SWT.NONE);
		}
	}
	
	public void addLogMessage(String logMessage, Color color) {
		String[] logMessageLines = logMessage.split("\n");

		for (String logMessageLine : logMessageLines) {
			TableItem item = new TableItem(this, SWT.NONE);
			item.setText(0, getLogTime());
			item.setText(1, logMessageLine);
			item.setForeground(color);
		}
		
		fitTableColumnsWidth();
		this.setTopIndex(this.getItemCount()-2);
	}
	
	private String getLogTime() {
		return logDateFormat.format(new Date());
	}
	
	private void fitTableColumnsWidth() {
		for (int i = 0, n = this.getColumnCount(); i < n; i++) {
			this.getColumn(i).pack();
		}
	}

	@Override
	public void setLayoutData(Object layoutData) {
		if (layoutData instanceof GridData) {
			((GridData) layoutData).heightHint = minHeightHint;
		}
		super.setLayoutData(layoutData);
	}

	@Override
	protected void checkSubclass() {
		// to make subclassing Table possible
	}
}
