package de.ad.tools.redmine.cli;

import de.ad.tools.redmine.cli.command.Command;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class AliasCommand extends Command {
    private static final String NAME = "alias";
    private static final String DESCRIPTION = "Create and configure shortcuts";
    private static final Argument[] ARGUMENTS = new Argument[] {
            new TextArgument("key", "The key for the alias.", false),
            new TextArgument("value", "The value for the alias.", true)
    };
    static final String ALIAS_SUCCESS_MESSAGE = "Successfully created alias.";

    @Override
    public void process(String[] arguments) {
        try {
            Ini ini = new Ini(new File(Application.CONFIGURATION_FILE_NAME));
            String key = arguments[0];
            if (arguments.length == 2) {
                String value = arguments[1];
                ini.put("alias", key, value);
                ini.store();
                println(ALIAS_SUCCESS_MESSAGE);
            } else {
                println(ini.get("alias", key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public AliasCommand(Configuration configuration, PrintStream out) {
        super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out);
    }
}

