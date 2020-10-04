package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;

public interface IF_Log {
	
	Color BLUE = SWTResourceManager.getColor(SWT.COLOR_BLUE);
	Color RED = SWTResourceManager.getColor(SWT.COLOR_RED);
	Color GREEN = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
	Color BLACK = SWTResourceManager.getColor(SWT.COLOR_BLACK);
	
	int LOG_INFO = 0;
	int LOG_ERROR = 1;
	int LOG_SUCCESS = 2;
}
