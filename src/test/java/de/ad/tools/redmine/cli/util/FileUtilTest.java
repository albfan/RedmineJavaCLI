package de.ad.tools.redmine.cli.util;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilTest {
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    FileUtil.setBaseDir(tmpFolder.getRoot());
  }

  @Test
  public void testExists() throws Exception {
    TestObject object = new TestObject(1, true, "test");
    FileUtil.writeObjectToFile(object, "test");
    
    boolean actual = FileUtil.exists("test");
    
    assertThat(actual).isTrue();
  }

  @Test
  public void testExistsWithNonExistentFile() throws Exception {
    boolean actual = FileUtil.exists("DoesNotExist");

    assertThat(actual).isFalse();
  }

  @Test
  public void testReadObjectFromFile() throws Exception {
    TestObject object = new TestObject(1, true, "test");
    FileUtil.writeObjectToFile(object, "test");

    TestObject result = FileUtil.readObjectFromFile("test");

    assertThat(result).isEqualTo(object);
  }

  @Test
  public void testWriteObjectToFile() throws Exception {
    TestObject object = new TestObject(1, true, "test");
    FileUtil.writeObjectToFile(object, "test");

    TestObject result = FileUtil.readObjectFromFile("test");

    assertThat(result).isEqualTo(object);
  }

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<?>[] constructors =
        FileUtil.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
  }

  private static class TestObject implements Serializable {
    private int i;
    private boolean b;
    private String s;

    public TestObject(int i, boolean b, String s) {
      this.i = i;
      this.b = b;
      this.s = s;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      TestObject that = (TestObject) o;

      if (b != that.b) {
        return false;
      }
      if (i != that.i) {
        return false;
      }
      if (s != null ? !s.equals(that.s) : that.s != null) {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      int result = i;
      result = 31 * result + (b ? 1 : 0);
      result = 31 * result + (s != null ? s.hashCode() : 0);
      return result;
    }
  }
}
