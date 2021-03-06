package de.ad.tools.redmine.cli;

import de.ad.tools.redmine.cli.util.FileUtil;
import org.fusesource.jansi.AnsiConsole;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Application {

  public static final String CONFIGURATION_FILE_NAME = System.getProperty("user.home")+"/.redmine";

  static Application instance =
      new Application(new ConfigurationManager(CONFIGURATION_FILE_NAME),
          new RedmineCliFactory(), AnsiConsole.out,
          new RedmineCli.RedmineManagerFactory());

  private ConfigurationManager configurationManager;
  private RedmineCliFactory redmineCliFactory;
  private PrintStream out;
  private RedmineCli.RedmineManagerFactory redmineManagerFactory;

  Application(ConfigurationManager configurationManager,
      RedmineCliFactory redmineCliFactory, PrintStream out,
      RedmineCli.RedmineManagerFactory redmineManagerFactory) {
    this.configurationManager = configurationManager;
    this.redmineCliFactory = redmineCliFactory;
    this.out = out;
    this.redmineManagerFactory = redmineManagerFactory;
  }

  public static void main(String... args) {
    instance.run(args);
  }

  void run(String... args) {
    AnsiConsole.systemInstall();
    Configuration configuration = configurationManager.loadConfiguration();

    RedmineCli redmineCli = redmineCliFactory.produce(configuration, out,
        redmineManagerFactory);

    try {
      redmineCli.handleCommand(args);
    } catch (Exception e) {
      out.println(e.getMessage());
    }

    configurationManager.persistConfiguration(configuration);
  }

  static class ConfigurationManager {
    private String configurationFileName;

    public ConfigurationManager(String configurationFileName) {
      this.configurationFileName = configurationFileName;
    }

    public Configuration loadConfiguration() {
      Configuration configuration = new Configuration();
      if (!FileUtil.exists(configurationFileName)) {
        return configuration;
      }

      try {
        Ini ini = new Ini(new File(configurationFileName));
        Profile.Section config = ini.get("config");
        if (config != null) {
          configuration.setApiKey(config.get("apikey"));
          configuration.setServer(config.get("server"));
        }
        return configuration;
      } catch (IOException e) {
        throw new IllegalStateException();
      }
    }

    public void persistConfiguration(Configuration configuration) {
      try {
        File file = new File(configurationFileName);
        if (!file.exists()) {
          file.createNewFile();
        }
        Ini ini = new Ini(file);
        ini.put("config", "apikey", configuration.getApiKey());
        ini.put("config", "server", configuration.getServer());
        ini.store();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  static class RedmineCliFactory {
    public RedmineCli produce(Configuration configuration, PrintStream out,
        RedmineCli.RedmineManagerFactory redmineManagerFactory) {
      return new RedmineCli(configuration, out, redmineManagerFactory);
    }
  }
}
