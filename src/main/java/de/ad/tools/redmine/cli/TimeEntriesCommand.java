package de.ad.tools.redmine.cli;

import com.taskadapter.redmineapi.TransportConfiguration;
import com.taskadapter.redmineapi.bean.Issue;
import de.ad.tools.redmine.cli.command.Command;
import org.apache.http.message.BasicNameValuePair;

import java.io.PrintStream;

public class TimeEntriesCommand extends Command {
    static final String RESET_SUCCESS_MESSAGE =
            "Successfully reset configuration.";

    private static final String NAME = "timeEntries";
    private static final String DESCRIPTION = "Show time entries";
    private static final Argument[] ARGUMENTS = new Argument[0];
    private TransportConfiguration transport;

    @Override
    public void process(String[] arguments) {
        transport.getObject(Issue.class, id, new BasicNameValuePair("include", value));
        configuration.reset();

        println(RESET_SUCCESS_MESSAGE);
    }
    public TimeEntriesCommand(Configuration configuration, PrintStream out, TransportConfiguration transport) {
        super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out);
        this.transport = transport;
    }

}
