package de.ad.tools.redmine.cli.util;

import org.junit.Test;

public class AnsiUtilTest {
  @Test
  public void testName() throws Exception {
    System.out.println(AnsiColor.RED.format("test"));

  }
}
