package de.ad.tools.redmine.cli.util;

import java.io.*;

public final class FileUtil {

  static File baseDir = new File(".");
  
  private FileUtil() {
  }

  public static <T> T readObjectFromFile(String filename) throws IOException,
      ClassNotFoundException {
    File input = new File(baseDir, filename);
    FileInputStream fin = new FileInputStream(input);
    ObjectInputStream ois = new ObjectInputStream(fin);
    T object = (T) ois.readObject();
    ois.close();

    return object;
  }

  public static void writeObjectToFile(Object object, String filename)
      throws IOException {
    File output = new File(baseDir, filename);
    FileOutputStream fout = new FileOutputStream(output);
    ObjectOutputStream oos = new ObjectOutputStream(fout);
    oos.writeObject(object);
  }
}
