package de.ad.tools.redmine.cli.util;

import jline.Terminal;
import jline.TerminalFactory;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

public final class PrintUtil {

  public static final String ANSI_CLS        = "\u001b[2J";
  public static final String ANSI_HOME       = "\u001b[H";
  public static final String ANSI_BOLD       = "\u001b[1m";
  public static final String ANSI_REVERSEON  = "\u001b[7m";
  public static final String ANSI_NORMAL     = "\u001b[0m";

  public static final String ANSI_BLACK      = "\u001b[30m";
  public static final String ANSI_RED        = "\u001b[31m";
  public static final String ANSI_GREEN      = "\u001b[32m";
  public static final String ANSI_YELLOW     = "\u001b[33m";
  public static final String ANSI_BLUE       = "\u001b[34m";
  public static final String ANSI_MAGENTA    = "\u001b[35m";
  public static final String ANSI_CYAN       = "\u001b[36m";
  public static final String ANSI_LIGHT_GRAY = "\u001b[37m";

  private PrintUtil() {
  }

  public static void printTable(PrintStream out, String[][] table) {
    printTable(out, table, null);
  }

  public static void printTable(PrintStream out, String[][] table, HashMap<Integer, String> subtotalMap) {
    int[] columnSizes = computeColumnSizes(table);

    String rowFormat = generateRowFormat(columnSizes);

    printRows(out, table, rowFormat, subtotalMap);
  }

  public static void printTable(PrintStream out, String[] header,
      String[][] table) {
      printTable(out, header, table, null);
  }

  public static void printTable(PrintStream out, String[] header,
      String[][] table, HashMap<Integer, String> subtotalMap) {
    String[][] tableWithHeader = addHeader(header, table);

    printTable(out, tableWithHeader, subtotalMap);
  }

  public static void printHeading(PrintStream out, String heading) {
    println(out, heading.toUpperCase());

    char[] divider = new char[heading.length()];
    Arrays.fill(divider, '¯');

    println(out, new String(divider));
  }

  private static String[][] addHeader(String[] header, String[][] table) {
    String[][] tableWithHeader = new String[table.length + 2][];

    tableWithHeader[0] = toUpperCase(header);
    tableWithHeader[1] = createDividers(header);

    for (int i = 0; i < table.length; i++) {
      tableWithHeader[i + 2] = table[i];
    }

    return tableWithHeader;
  }

  private static String[] toUpperCase(String[] header) {
    for (int i = 0; i < header.length; i++) {
      header[i] = header[i].toUpperCase();
    }

    return header;
  }

  private static String[] createDividers(String[] header) {
    String[] dividers = new String[header.length];

    char[] divider;
    for (int i = 0; i < dividers.length; i++) {
      divider = new char[header[i].length()];
      Arrays.fill(divider, '¯');
      dividers[i] = new String(divider);
    }

    return dividers;
  }


  private static void printRows(PrintStream out, String[][] table, String rowFormat) {
    printRows(out, table, rowFormat, null);
  }

  private static void printRows(PrintStream out, String[][] table,
                                String rowFormat, HashMap<Integer, String> subtotalMap) {
    for (int i = 0; i < table.length; i++) {
      String[] row = table[i];
      println(out, rowFormat, (Object[]) row);
      if (subtotalMap != null) {
        String subtotal = subtotalMap.get(i);
        if (subtotal != null) {
          println(out, subtotal);
        }
      }
    }
  }

  private static String generateRowFormat(int[] columnSizes) {

    int terminalWidth = getTerminalWidth();

    StringBuilder formatBuilder = new StringBuilder();

    int total = 0;
    for (int i = 0; i < columnSizes.length; i++) {
      int columnSize = columnSizes[i];
      int size;
      if (i == columnSizes.length -1 && terminalWidth != -1) {
        size = terminalWidth - total;
        if (size <= 0) {
          size = columnSize;
        }
      } else {
        size = columnSize + 2;
      }
      total += size;
      String format = String.format("%%-%ds", size);
      formatBuilder.append(format);
    }

    return formatBuilder.toString();
  }

  public static int getTerminalWidth() {
    int terminalWidth;
    String redminecliTerminalWidth = System.getProperty("redminejavacli.terminalWidth");
    if (redminecliTerminalWidth != null) {
      try {
        terminalWidth = Integer.parseInt(redminecliTerminalWidth);
        return terminalWidth;
      } catch (Exception e) {
      }
    }

    Terminal terminal = TerminalFactory.get();
    //terminal.hasWidth() or acces to getSettings()
    terminalWidth = terminal.getWidth();
    return terminalWidth;
  }

  private static int[] computeColumnSizes(String[][] table) {
    if (table.length == 0) {
      return new int[0];
    }

    int[] columnSizes = new int[table[0].length];
    for (String[] row : table) {
      for (int column = 0; column < row.length; column++) {
        int columnSize = row[column] == null ? 0 : row[column].length();
        if (columnSizes[column] < columnSize) {
          columnSizes[column] = columnSize;
        }
      }
    }
    return columnSizes;
  }

  private static void println(PrintStream out, String s, Object... args) {
    out.println(String.format(s, args).replaceAll("\\s+$",""));
  }
}
