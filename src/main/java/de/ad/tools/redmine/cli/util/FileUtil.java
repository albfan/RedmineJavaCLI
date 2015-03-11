package de.ad.tools.redmine.cli.util;

import java.io.*;

public final class FileUtil {

  private FileUtil() {
  }

  public static <T> T readObjectFromFile(String filename) throws IOException,
      ClassNotFoundException {
    FileInputStream fin = new FileInputStream(filename);
    ObjectInputStream ois = new ObjectInputStream(fin);
    T object = (T) ois.readObject();
    ois.close();

    return object;
  }

  public static void writeObjectToFile(Object object, String filename)
      throws IOException {
    FileOutputStream fout = new FileOutputStream(filename);
    ObjectOutputStream oos = new ObjectOutputStream(fout);
    oos.writeObject(object);
  }
}
