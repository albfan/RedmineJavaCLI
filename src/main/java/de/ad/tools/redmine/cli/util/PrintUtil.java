package de.ad.tools.redmine.cli.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;

public final class PrintUtil {

  private PrintUtil() {
  }

  public static void printTable(PrintStream out, String[][] table) {
    int[] columnSizes = computeColumnSizes(table);

    String rowFormat = generateRowFormat(columnSizes);

    printRows(out, table, rowFormat);
  }

  public static void printTable(PrintStream out, String[] header,
      String[][] table) {
    String[][] tableWithHeader = addHeader(header, table);

    printTable(out, tableWithHeader);
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

  private static void printRows(PrintStream out, String[][] table,
      String rowFormat) {
    for (String[] row : table) {
      println(out, rowFormat, row);
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
      } else {
        size = columnSize + 2;
      }
      total += size;
      String format = String.format("%%-%ds", size);
      formatBuilder.append(format);
    }

    return formatBuilder.toString();
  }

  private static int getTerminalWidth() {
    int terminalWidth;
    try {
      Process p = Runtime.getRuntime().exec("tput cols");
      BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
      terminalWidth = Integer.parseInt(bri.readLine());
    } catch (IOException e) {
      terminalWidth = -1;
    }
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
    out.println(String.format(s, args));
  }
}
