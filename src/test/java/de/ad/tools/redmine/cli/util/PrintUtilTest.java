package de.ad.tools.redmine.cli.util;

import org.junit.Test;

import java.lang.reflect.Constructor;

public class PrintUtilTest {

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<?>[] constructors =
        PrintUtil.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
  }
}
