package gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import log.Log;

public class Main {

	public final static String VERSION = "0.3.2";
	
	private static Log log;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		log = new Log();
		log.addMessage("Starting program (v" + VERSION + ")");

		// Try to start the program
		try {
			log.addMessage("Starting GUI");
			MainGUI gui = new MainGUI(log);
			gui.open();
			log.addMessage("GUI closed");
		} catch (Exception e) {
			System.out.println(e.toString());
			log.addMessage("Error stopped program");
			
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			log.addMessage(error.toString());
		}

		//
		log.addMessage("Program stopped");
	}
}
