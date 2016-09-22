package de.ad.tools.redmine.cli;

import de.ad.tools.redmine.cli.command.Command;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class AliasCommand extends Command {
    private static final String NAME = "alias";
    private static final String DESCRIPTION = "Create and configure shortcuts";
    private static final Argument[] ARGUMENTS = new Argument[] {
            new TextArgument("key", "The key for the alias.", true),
            new TextArgument("value", "The value for the alias.", true)
    };
    static final String ALIAS_SUCCESS_MESSAGE = "Successfully created alias.";

    @Override
    public void process(String[] arguments) {
        try {
            Ini ini = new Ini(new File(Application.CONFIGURATION_FILE_NAME));
            if (arguments.length >= 1) {
                String key = arguments[0];
                if (arguments.length == 2) {
                    String value = arguments[1];
                    ini.put("alias", key, value);
                    ini.store();
                    println(ALIAS_SUCCESS_MESSAGE);
                } else {
                    if (arguments.length > 2) {
                        println("Are you trying to store an alias with spaces?");
                        println("Use quotes for value");
                        String elipsis = arguments.length > 3 ? "..." : "";
                        println("Ignoring: "+arguments[2]+ elipsis);
                    }
                    println(ini.get("alias", key));
                }
            } else {
                Profile.Section alias = ini.get("alias");
                for (String key : alias.keySet()) {
                    if (!key.endsWith(".description")) {
                        String definition = ini.get("alias", key);
                        String description = ini.get("alias", key+".description");
                        if (description != null) {
                            println(key+ " : " + description);
                            println("   "+definition);
                        } else {
                            println(key + " = " + definition);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public AliasCommand(Configuration configuration, PrintStream out) {
        super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out);
    }
}

