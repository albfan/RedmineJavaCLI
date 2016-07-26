package de.ad.tools.redmine.cli;

import com.taskadapter.redmineapi.Include;
import de.ad.tools.redmine.cli.command.Command;
import de.ad.tools.redmine.cli.command.IssueCommand;
import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigCommand extends Command {
    private static final String NAME = "config";
    private static final String DESCRIPTION = "Create and configure cli";
    private static final Argument[] ARGUMENTS = new Argument[] {
            new TextArgument("key", "The key for the config. Add dot to separate section", true),
            new TextArgument("value", "The value for the alias.", true)
    };
    private static final Option[] OPTIONS = new Option[]{
            Option.buildOptionWithoutValue("pretty", "Show pretty output")
    };

    static final String CONFIG_SUCCESS_MESSAGE = "Successfully created or update config.";

    private static final Map<String, IHandler> handlers = new HashMap<>();

    private boolean pretty;

    public ConfigCommand(Configuration configuration, PrintStream out) {
        super(NAME, DESCRIPTION, "", ARGUMENTS, OPTIONS, configuration, out);
        ArrayList<IHandler> handlerList = new ArrayList<>();
        handlerList.add(new PrettyHandler("pretty"));

        for (IHandler handler : handlerList) {
            handlers.put(handler.getName(), handler);
        }
    }

    interface IHandler {
        String getName();
        void handle();
    }
    class Handler implements IHandler {

        private String name;

        public Handler(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void handle() {
            pretty = true;
        }
    }

    @Override
    public void process(String[] arguments) throws Exception {
        super.process(arguments);

        for (Option option : getOptions()) {
            if (option.buildValue() == null) {
                continue;
            }

            handlers.get(option.getName()).handle();
        }
        try {
            Ini ini = new Ini(new File(Application.CONFIGURATION_FILE_NAME));
            if (arguments.length >= 1) {
                String section = arguments[0];
                String key = null;
                if (section.contains(".")) {
                    String[] split = section.split("\\.");
                    section = split[0];
                    key = split[1];
                }
                if (arguments.length == 2 && !StringUtils.isBlank(key)) {
                    key = arguments[1];
                }
                if (arguments.length == 2 && !StringUtils.isBlank(key)) {
                    String value = arguments[1];
                    ini.put(section, section, value);
                    ini.store();
                    println(CONFIG_SUCCESS_MESSAGE);
                } else {
                    if (!StringUtils.isBlank(key)) {
                        println(key + " = " + ini.get(section, key));
                    } else {
                        for (String sectionKey : ini.keySet()) {
                            printSection(ini, sectionKey, false);
                        }
                    }
                }
            } else {
                for (String sectionKey : ini.keySet()) {
                    printSection(ini, sectionKey, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printSection(Ini ini, String sectionKey, boolean showSection) {
        Profile.Section section = ini.get(sectionKey);
        if (pretty) {
            println("["+sectionKey+"]");
        }
        for (String sectionKeyKey : section.keySet()) {
            String key;
            if (pretty) {
                key = "\t"+sectionKeyKey;
            } else {
                key = (showSection ? sectionKey + "." : "") + sectionKeyKey;
            }
            println(key + " = " + ini.get(sectionKey, sectionKeyKey));
        }
    }

    private class PrettyHandler extends Handler {
        public PrettyHandler(String name) {
            super(name);
        }
    }
}

