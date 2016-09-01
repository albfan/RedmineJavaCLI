package de.ad.tools.redmine.cli;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.internal.Transport;
import com.taskadapter.redmineapi.internal.URIConfigurator;
import java.lang.reflect.Field;
import java.net.URL;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RedmineCliTest {
  private Configuration configuration;
  private PrintStream out;
  private RedmineCli.RedmineManagerFactory redmineManagerFactory;

  private RedmineCli redmineCli;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConfigured()).thenReturn(true);
    when(configuration.getServer()).thenReturn("http://test.redmine.com");
    when(configuration.getApiKey()).thenReturn("1234567890");

    out = mock(PrintStream.class);

    redmineManagerFactory = mock(RedmineCli.RedmineManagerFactory.class);

    redmineCli = new RedmineCli(configuration, out, redmineManagerFactory);
  }

  @Test
  public void testInitWhenNotConnected() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConfigured()).thenReturn(false);

    redmineCli = new RedmineCli(configuration, out, redmineManagerFactory);
  }

  @Test
  public void testHandleCommandWithNoArguments() throws Exception {
    String[] arguments = null;

    exception.expect(Exception.class);
    exception.expectMessage(RedmineCli.INVALID_ARGUMENT_MESSAGE);
    redmineCli.handleCommand(arguments);
  }

  @Test
  public void testHandleCommandWithEmptyArguments() throws Exception {
    String[] arguments = new String[0];

    exception.expect(Exception.class);
    exception.expectMessage(RedmineCli.INVALID_ARGUMENT_MESSAGE);
    redmineCli.handleCommand(arguments);
  }

  @Test
  public void testEquals() throws Exception {
    RedmineCli redmineCli1 = new RedmineCli(configuration, out,
        redmineManagerFactory);
    RedmineCli redmineCli2 = new RedmineCli(configuration, out,
        redmineManagerFactory);

    assertThat(redmineCli1.equals(redmineCli2)).isTrue();
  }

  @Test
  public void testHashCode() throws Exception {
    RedmineCli redmineCli1 = new RedmineCli(configuration, out,
        redmineManagerFactory);
    RedmineCli redmineCli2 = new RedmineCli(configuration, out,
        redmineManagerFactory);

    assertThat(redmineCli1.hashCode()).isEqualTo(redmineCli2.hashCode());
  }

  public static class RedmineManagerFactoryTest {
    @Test
    public void testCreateWithApiKey() throws Exception {
      RedmineCli.RedmineManagerFactory redmineManagerFactory =
          new RedmineCli.RedmineManagerFactory();

      String url = "http://test.redmine.com";
      String apiKey = "apiKey";

      RedmineManager result =
          redmineManagerFactory.createWithApiKey(url, apiKey);

      Transport transport = reflectField(result, "transport");
      URIConfigurator configurator = reflectField(transport, "configurator");
      URL baseUrl = reflectField(configurator, "baseURL");
      String apiAccessKey = reflectField(configurator, "apiAccessKey");

      assertThat(baseUrl).isEqualTo(new URL(url));
      assertThat(apiAccessKey).isEqualTo(apiKey);
    }

    private <T> T reflectField(Object subject, String fieldName)
        throws NoSuchFieldException, IllegalAccessException {
      Field field = subject.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);

      return (T) field.get(subject);
    }
  }
}
