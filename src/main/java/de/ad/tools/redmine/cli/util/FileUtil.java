package de.ad.tools.redmine.cli.util;

import java.io.*;

public final class FileUtil {

  public static FileUtilImpl impl = new FileUtilImpl(new File("."));

  private FileUtil() { }

  public static boolean exists(String filename) {
    return impl.exists(filename);
  }

  public static class FileUtilImpl {
    private File baseDir;

    public FileUtilImpl(File baseDir) {
      this.baseDir = baseDir;
    }

    public boolean exists(String filename) {
      return getFile(filename).exists();
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
