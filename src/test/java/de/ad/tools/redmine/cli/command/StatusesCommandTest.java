package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.TrackerFactory;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.UserFactory;
import de.ad.tools.redmine.cli.Configuration;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static de.ad.tools.redmine.cli.test.TestHelper.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatusesCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private ByteArrayOutputStream stream;
  private RedmineManager redmineManager;
  private IssueManager issueManager;

  private StatusesCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(true);

    stream = new ByteArrayOutputStream();
    out = new PrintStream(stream);

    redmineManager = mock(RedmineManager.class);
    issueManager = mock(IssueManager.class);

    command = new StatusesCommand(configuration, out, redmineManager);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[0];

    List<IssueStatus> statuses = createDummyStatuses();

    when(redmineManager.getIssueManager()).thenReturn(issueManager);
    when(issueManager.getStatuses()).thenReturn(statuses);

    command.process(arguments);

    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/StatusesCommandOutput.txt"));

    assertThat(actual).isEqualTo(expected);
  }

  private List<IssueStatus> createDummyStatuses() {
    IssueStatus newStatus = mock(IssueStatus.class);
    when(newStatus.getName()).thenReturn("New");
    when(newStatus.getId()).thenReturn(1);
    when(newStatus.isDefaultStatus()).thenReturn(true);
    when(newStatus.isClosed()).thenReturn(false);

    IssueStatus closedStatus = mock(IssueStatus.class);
    when(closedStatus.getName()).thenReturn("Closed");
    when(closedStatus.getId()).thenReturn(2);
    when(closedStatus.isDefaultStatus()).thenReturn(false);
    when(closedStatus.isClosed()).thenReturn(true);
    
    return Arrays.asList(newStatus, closedStatus);
  }
}
