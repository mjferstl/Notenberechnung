package gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import log.IF_Log;
import utils.UpdatePublisher;

public class OpenFileButton extends Button {
	
	private UpdatePublisher updatePublisher;
	
	public OpenFileButton(UpdatePublisher parent, Composite composite, int style) {
		super(composite, style);
		this.setText("Datei öffnen");
		
		if (parent instanceof UpdatePublisher) {
			this.updatePublisher = parent;
		}
	}
	
	public void activate(File file) {
		this.activate(createSelectionAdapterOpenExcelFile(file));
	}
	
	private void activate(SelectionAdapter selectionAdapter) {
		this.setVisible(true);
		setSelectionListener(this, selectionAdapter);
	}
	
	public void deactivate() {
		this.setVisible(false);
		
		// remove all current selection listeners
		// a new selection listener needs to be set to activate the button
		removeSelectionListeners(this);
	}
	
	private void setSelectionListener(Button button, SelectionAdapter selectionAdapter) {
		removeSelectionListeners(button);
		button.addSelectionListener(selectionAdapter);
	}

	private void removeSelectionListeners(Button button) {
		Listener[] selectionListeners = button.getListeners(SWT.Selection);
		for (Listener listener : selectionListeners) {
			button.removeListener(SWT.Selection, listener);
		}
	}
	
	private SelectionAdapter createSelectionAdapterOpenExcelFile(File file) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openFile(file);
			}
		};
	}
	
	private void openFile(File file) {
		try {
			publishUpdate(String.format("Die Datei \"%s\" wird geöffnet...", file.getName()));
			Desktop.getDesktop().open(file);
			publishUpdate("Datei geöffnet");
		} catch (IOException e) {
			publishUpdate(String.format("Die Datei konnte nicht geöffnet werden", file.getName()), IF_Log.LOG_ERROR);
		}
	}
	
	private void publishUpdate(String message) {
		this.publishUpdate(message, IF_Log.LOG_INFO);
	}
	
	private void publishUpdate(String message, int logLevel) {
		if (this.updatePublisher != null) {
			this.updatePublisher.publishUpdate(message, logLevel);
		}
	}
	
	@Override
	protected void checkSubclass() {
		// to make subclassing Table possible
	}
}
