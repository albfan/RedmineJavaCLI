package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineAuthenticationException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.UserManager;
import com.taskadapter.redmineapi.bean.User;
import de.ad.tools.redmine.cli.Configuration;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConnectCommandTest {
  private Configuration configuration;
  private PrintStream out;

  private ConnectCommand command;

  private ConnectCommand.RedmineManagerFactory redmineManagerFactory;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);

    out = mock(PrintStream.class);

    redmineManagerFactory = mock(ConnectCommand.RedmineManagerFactory.class);

    command = new ConnectCommand(configuration, out, redmineManagerFactory);
  }

  @Test
  public void testCommand() throws Exception {
    String server = "http://test.redmine.com";
    String apiKey = "apiKey";

    RedmineManager redmineManager = mock(RedmineManager.class);
    UserManager userManager = mock(UserManager.class);
    User currentUser = mock(User.class);

    when(redmineManagerFactory.createWithApiKey(server, apiKey)).
        thenReturn(redmineManager);
    when(redmineManager.getUserManager()).thenReturn(userManager);
    when(userManager.getCurrentUser()).thenReturn(currentUser);
    when(currentUser.getLogin()).thenReturn("foo.bar");

    String[] arguments = new String[] { server, apiKey };

    command.process(arguments);

    verify(configuration).setServer(server);
    verify(configuration).setApiKey(apiKey);

    String message =
        String.format(ConnectCommand.SUCCESS_MESSAGE, "foo.bar", server);
    verify(out).println(message);
  }

  @Test
  public void testCommandWhenAlreadyConnected() throws Exception {
    String server = "http://test.redmine.com";
    String apiKey = "apiKey";

    when(configuration.isConnected()).thenReturn(true);

    String[] arguments = new String[] { server, apiKey };

    command.process(arguments);

    verify(out).println(ConnectCommand.ALREADY_CONNECTED_MESSAGE);
  }

  @Test
  public void testCommandWithFailingAuthorization() throws Exception {
    String server = "http://test.redmine.com";
    String apiKey = "apiKey";

    RedmineManager redmineManager = mock(RedmineManager.class);
    UserManager userManager = mock(UserManager.class);

    when(redmineManagerFactory.createWithApiKey(any(String.class),
        any(String.class))).thenReturn(redmineManager);
    when(redmineManager.getUserManager()).thenReturn(userManager);

    doThrow(RedmineAuthenticationException.class).when(
        userManager).getCurrentUser();

    String[] arguments = new String[] { server, apiKey };

    command.process(arguments);

    String message =
        String.format(ConnectCommand.FAILURE_MESSAGE, server);
    verify(out).println(message);
  }

  @Test
  public void testCommandWithFailingConnection() throws Exception {
    String server = "http://test.redmine.com";
    String apiKey = "apiKey";

    RedmineManager redmineManager = mock(RedmineManager.class);
    UserManager userManager = mock(UserManager.class);

    when(redmineManagerFactory.createWithApiKey(any(String.class),
        any(String.class))).thenReturn(redmineManager);
    when(redmineManager.getUserManager()).thenReturn(userManager);

    doThrow(Exception.class).when(
        userManager).getCurrentUser();

    String[] arguments = new String[] { server, apiKey };

    exception.expect(Exception.class);
    command.process(arguments);
  }
}
