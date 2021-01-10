package gui;

import log.LogType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import utils.UpdatePublisher;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OpenFileButton extends Button {
	
	private UpdatePublisher updatePublisher;
	
	public OpenFileButton(UpdatePublisher parent, Composite composite, int style) {
		super(composite, style);
		this.setText("Datei öffnen");
		
		if (parent != null) {
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

		if (file == null) throw new IllegalArgumentException("The argument file is null");

		try {
			publishUpdate(String.format("Die Datei \"%s\" wird geöffnet...", file.getName()));
			Desktop.getDesktop().open(file);
			publishUpdate("Datei geöffnet");
		} catch (IOException e) {
			publishUpdate(String.format("Die Datei \"%s\" konnte nicht geöffnet werden", file.getName()), LogType.ERROR);
		}
	}
	
	private void publishUpdate(String message) {
		this.publishUpdate(message, LogType.INFO);
	}
	
	private void publishUpdate(String message, LogType logType) {
		if (this.updatePublisher != null) {
			this.updatePublisher.publishUpdate(message, logType);
		}
	}
	
	@Override
	protected void checkSubclass() {
		// to make subclassing Table possible
	}
}
