package de.ad.tools.redmine.cli.util;

import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PrintUtilTest {

  private PrintStream out;

  @Before
  public void setUp() throws Exception {
    out = mock(PrintStream.class);
  }

  @Test
  public void testPrintTable() throws Exception {
    String[][] table = new String[2][2];
    table[0] = new String[] { "row1,col1", "row1,col2" };
    table[1] = new String[] { "row2,col1", "row2,col2" };

    PrintUtil.printTable(out, table);

    verify(out).println("row1,col1  row1,col2  ");
    verify(out).println("row2,col1  row2,col2  ");
  }

  @Test
  public void testPrintTableWithDifferentColumnSizes() throws Exception {
    String[][] table = new String[3][2];
    table[0] = new String[] { "row1,col1", "row1,col2" };
    table[1] = new String[] { "row2,col1", "" };
    table[2] = new String[] { "", "row3,col2" };

    PrintUtil.printTable(out, table);

    verify(out).println("row1,col1  row1,col2  ");
    verify(out).println("row2,col1             ");
    verify(out).println("           row3,col2  ");
  }

  @Test
  public void testPrintTableWithHeader() throws Exception {
    String[] header = new String[] { "header1", "header2" };

    String[][] table = new String[2][2];
    table[0] = new String[] { "row1,col1", "row1,col2" };
    table[1] = new String[] { "row2,col1", "row2,col2" };

    PrintUtil.printTable(out, header, table);

    verify(out).println("header1    header2    ");
    verify(out).println("¯¯¯¯¯¯¯    ¯¯¯¯¯¯¯    ");
    verify(out).println("row1,col1  row1,col2  ");
    verify(out).println("row2,col1  row2,col2  ");
  }

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<?>[] constructors =
        PrintUtil.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
  }
}
