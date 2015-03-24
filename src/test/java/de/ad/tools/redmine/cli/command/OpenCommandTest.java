package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import de.ad.tools.redmine.cli.Configuration;
import java.awt.Desktop;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import sun.security.krb5.internal.crypto.Des;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OpenCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private RedmineManager redmineManager;
  private OpenCommand.Browser browser;

  private OpenCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(true);

    out = mock(PrintStream.class);

    redmineManager = mock(RedmineManager.class);

    browser = mock(OpenCommand.Browser.class);

    command = new OpenCommand(configuration, out, redmineManager, browser);
  }

  @Test
  public void testCommandWithNoBrowserSupport() throws Exception {
    String[] arguments = new String[] { "1" };

    when(browser.isSupported()).thenReturn(false);
    String message =
        String.format(OpenCommand.NO_DESKTOP_SUPPORT_MESSAGE, 1);

    exception.expect(Exception.class);
    exception.expectMessage(message);

    command.process(arguments);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[] { "1" };

    String server = "http://test.redmine.com";
    when(configuration.getServer()).thenReturn(server);
    when(browser.isSupported()).thenReturn(true);

    command.process(arguments);

    String url = String.format("%s/issues/%s", server, arguments[0]);
    verify(browser).browse(new URI(url));
    verify(out).println(String.format(OpenCommand.SUCCESS_MESSAGE, 1));
  }

  @Test
  public void testBrowserIsSupported() throws Exception {
    boolean expected = true;
    OpenCommand.Browser browser = new OpenCommand.Browser(expected, null);

    boolean actual = browser.isSupported();

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testBrowserIsNotSupported() throws Exception {
    boolean expected = false;
    OpenCommand.Browser browser = new OpenCommand.Browser(expected, null);

    boolean actual = browser.isSupported();

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testBrowserBrowse() throws Exception {
    Desktop desktop = mock(Desktop.class);
    OpenCommand.Browser browser = new OpenCommand.Browser(false, desktop);

    URI uri = URI.create("http://test.redmine.com");
    browser.browse(uri);

    verify(desktop).browse(uri);
  }

  @Test
  public void testBrowserBrowseWhenNotSupported() throws Exception {
    OpenCommand.Browser browser = new OpenCommand.Browser(false, null);

    URI uri = URI.create("http://test.redmine.com");
    browser.browse(uri);
  }
}
