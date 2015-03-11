package de.ad.tools.redmine.cli.util;

public final class StringUtil {
  public static final String ELLIPSIS = "…";

  private StringUtil() {
  }

  public static String ellipsize(String string, int maxLength) {
    if (string.length() <= maxLength) {
      return string;
    } else {
      return string.substring(0, maxLength - 1) + ELLIPSIS;
    }
  }
}
