package de.ad.tools.redmine.cli;

import de.ad.tools.redmine.cli.command.Command;

import java.io.PrintStream;

public class TimeEntriesCommand extends Command {
    private static final String NAME = "timeEntries";
    private static final String DESCRIPTION = "Show time entries";
    private static final Argument[] ARGUMENTS = new Argument[0];

    @Override
    public void process(String[] arguments) {
        configuration.reset();

        println("TODO: timeentries");
    }
    public TimeEntriesCommand(Configuration configuration, PrintStream out) {
        super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out);
    }
}
