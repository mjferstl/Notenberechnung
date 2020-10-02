package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextFileReader {

	private File file;

	public TextFileReader() {

	}

	public TextFileReader(String pathToFile) {
		this(new File(pathToFile));
	}

	public TextFileReader(File file) {
		setFile(file);
	}

	public File getFile() {
		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFile(String filePath) {
		this.setFile(new File(filePath));
	}

	public List<String> readFile() throws IOException {
		
		List<String> contentLineByLine = new ArrayList<>();
		
		try (FileInputStream fis = new FileInputStream(getFile());
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader reader = new BufferedReader(isr)) {

			String lineContent;
			while ((lineContent = reader.readLine()) != null) {
				contentLineByLine.add(lineContent);
			}

		} catch (IOException e) {
			throw e;
		}
		
		return contentLineByLine;
	}

}
