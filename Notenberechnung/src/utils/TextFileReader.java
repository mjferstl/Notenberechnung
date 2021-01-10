package utils;

import org.eclipse.jdt.annotation.NonNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextFileReader implements FileReader{

	private File file;

	public TextFileReader(@NonNull File file) {
		setFile(file);
	}

	public File getFile() {
		return this.file;
	}

	public void setFile(@NonNull File file) {
		this.file = file;
	}

	public List<String> readFile() throws IOException {
		
		List<String> contentLineByLine = new ArrayList<>();
		
		try (FileInputStream fis = new FileInputStream(getFile());
			 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.ISO_8859_1);
			 BufferedReader reader = new BufferedReader(isr)) {

			String lineContent;
			while ((lineContent = reader.readLine()) != null) {
				contentLineByLine.add(lineContent);
			}

		} catch (IOException e) {
			throw new IOException("The file " + file.getAbsolutePath() + " cannot be read. " + e.getMessage());
		}
		
		return contentLineByLine;
	}
}
