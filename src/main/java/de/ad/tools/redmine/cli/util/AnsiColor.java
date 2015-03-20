package de.ad.tools.redmine.cli.util;

/**
 * http://en.wikipedia.org/wiki/ANSI_escape_code
 * http://stackoverflow.com/a/5762502/2538428
 */
public enum AnsiColor {
  BLACK("30"),
  RED("31"),
  GREEN("32"),
  YELLOW("33"),
  BLUE("34"),
  MAGENTA("35"),
  CYAN("36"),
  WHITE("37");
  private static final String ANSI_COLOR_PATTERN = "\u001B[%sm";
  private static final String ANSI_RESET = "\u001B[0m";

  private final String code;

  AnsiColor(String code) {
    this.code = code;
  }

  public String format(String s) {
    String color = String.format(ANSI_COLOR_PATTERN, code);
    return String.format("%s%s%s", color, s, ANSI_RESET);
  }
}

