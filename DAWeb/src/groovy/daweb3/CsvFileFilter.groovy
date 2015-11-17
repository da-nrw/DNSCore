package daweb3;

import java.io.File;
import java.io.FilenameFilter;

public class CsvFileFilter implements FilenameFilter {
	   public boolean accept(File f, String filename) {
	       return filename.toLowerCase().endsWith("csv")
	   }
}
