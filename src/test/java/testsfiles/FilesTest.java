package testsfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FilesTest {

  private ClassLoader cl = FilesTest.class.getClassLoader();

  private File FileResearchInZip(String filename, String pathZip, String pathFile)
      throws Exception {

    File path = new File(pathFile);
    if (!path.exists()) {
      path.mkdirs();
    }
    try (ZipInputStream zip = new ZipInputStream(cl.getResourceAsStream(pathZip))) {
      ZipEntry entry;
      while ((entry = zip.getNextEntry()) != null) {
        String nameFile = entry.getName();
        if (Objects.equals(nameFile, filename)) {
          File newFile = new File(path, nameFile);
          try (FileOutputStream fout = new FileOutputStream(newFile);) {
            byte[] savefile = new byte[2048];
            int length;
            while ((length = zip.read(savefile)) > 0) {
              fout.write(savefile, 0, length);
            }
          }
          return newFile;
        }

      }
    }
    return null;
  }


  @Test
  void pdfFilesTest() throws Exception {
    File file = FileResearchInZip("pdffile.pdf", "AllFile.zip", "src/test/resources/output");
    PDF pdf = new PDF(file);
    assertEquals(
        "Stefan Bechtold, Sam Brannen, Johannes Link, Matthias Merdes, Marc Philipp, Juliette de Rancourt, Christian Stein",
        pdf.author);
  }

  @Test
  void xlsFilesParsingTest() throws Exception {
    File file = FileResearchInZip("xlsfile.xlsx", "AllFile.zip", "src/test/resources/output");
    XLS xls = new XLS(file);
    String actualString = xls.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue();
    assertTrue(actualString.contains("Здравствуйте"));
  }

  @Test
  void csvFilesParsingTest() throws Exception {
    File file = FileResearchInZip("csvfile1.csv", "AllFile.zip", "src/test/resources/output");
    CSVReader csv = new CSVReader(new FileReader(file));
    List<String[]> data = csv.readAll();
    Assertions.assertEquals(4, data.size());
    Assertions.assertArrayEquals(new String[]{"eruid", "description"}, data.get(0));
    Assertions.assertArrayEquals(new String[]{"batman", "uses technology"}, data.get(1));
    Assertions.assertArrayEquals(new String[]{"superman", "flies through the air"}, data.get(2));
    Assertions.assertArrayEquals(new String[]{"spiderman", "uses a web"}, data.get(3));
  }
}
