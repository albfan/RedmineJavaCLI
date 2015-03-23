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

  public static String stripQuotes(String s) {
    if (s.startsWith("\"")) {
      s = s.substring(1);
    }

    if (s.endsWith("\"")) {
      s = s.substring(0, s.length() - 1);
    }

    return s;
  }
}
