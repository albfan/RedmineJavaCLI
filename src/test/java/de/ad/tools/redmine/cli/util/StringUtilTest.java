package de.ad.tools.redmine.cli.util;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilTest {

  @Test
  public void testEllipsizeWithNoEffect() throws Exception {
    String string = "1234567890";
    int maxLength = 10;

    String result = StringUtil.ellipsize(string, maxLength);

    assertThat(result).matches(string);
  }

  @Test
  public void testEllipsize() throws Exception {
    String string = "12345678901";
    int maxLength = 10;

    String result = StringUtil.ellipsize(string, maxLength);

    assertThat(result).hasSize(10).endsWith(StringUtil.ELLIPSIS);
  }

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<?>[] constructors =
        StringUtil.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
  }
}
