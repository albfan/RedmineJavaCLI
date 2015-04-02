package de.ad.tools.redmine.cli.util;

import java.io.*;

public final class FileUtil {

  private static File baseDir = new File(".");

  private FileUtil() {
  }

  public static boolean exists(String filename) {
    return new File(baseDir, filename).exists();
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

  public static void setBaseDir(File baseDir) {
    FileUtil.baseDir = baseDir;
  }
}
