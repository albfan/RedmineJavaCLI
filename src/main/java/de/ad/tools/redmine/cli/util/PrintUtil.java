package de.ad.tools.redmine.cli.util;

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
    StringBuilder formatBuilder = new StringBuilder();

    for (int columnSize : columnSizes) {
      String format = String.format("%%-%ds", columnSize + 2);
      formatBuilder.append(format);
    }

    return formatBuilder.toString();
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
