package de.ad.tools.redmine.cli.util;

import java.io.*;

public final class FileUtil {

  public static FileUtilImpl impl = new FileUtilImpl(new File("."));

  private FileUtil() {
  }

  public static boolean exists(String filename) {
    return impl.exists(filename);
  }

  public static <T> T readObjectFromFile(String filename) throws IOException,
      ClassNotFoundException {
    return impl.readObjectFromFile(filename);
  }

  public static void writeObjectToFile(Object object, String filename)
      throws IOException {
    impl.writeObjectToFile(object, filename);
  }

  public static class FileUtilImpl {
    private File baseDir;

    public FileUtilImpl(File baseDir) {
      this.baseDir = baseDir;
    }

    public boolean exists(String filename) {
      return getFile(filename).exists();
    }

    public <T> T readObjectFromFile(String filename) throws IOException,
        ClassNotFoundException {
      File input = getFile(filename);
      FileInputStream fin = new FileInputStream(input);
      ObjectInputStream ois = new ObjectInputStream(fin);
      T object = (T) ois.readObject();
      ois.close();

      return object;
    }

    public void writeObjectToFile(Object object, String filename)
        throws IOException {
      File output = getFile(filename);
      FileOutputStream fout = new FileOutputStream(output);
      ObjectOutputStream oos = new ObjectOutputStream(fout);
      oos.writeObject(object);
    }

    private File getFile(String filename) {
      File output;
      if (!filename.startsWith(File.separator)) {
        output = new File(baseDir, filename);
      } else {
        output = new File(filename);
      }
      return output;
    }
  }
}
