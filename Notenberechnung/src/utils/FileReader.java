package utils;

import java.io.IOException;
import java.util.List;

public interface FileReader {

    List<String> readFile() throws IOException;
}
