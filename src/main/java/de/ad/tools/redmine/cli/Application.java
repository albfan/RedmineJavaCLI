package de.ad.tools.redmine.cli;

import de.ad.tools.redmine.cli.util.FileUtil;

import java.io.IOException;

public final class Application {

  public static final String LOCAL_CONFIGURATION_FILE_NAME = ".redmine";

  public static void main(String... args) {
    Configuration configuration = loadConfiguration();

    RedmineCli redmineCli = new RedmineCli(configuration, System.out);

    try {
      redmineCli.handleCommand(args);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    persistConfiguration(configuration);
  }

  private static Configuration loadConfiguration() {
    Configuration configuration = new Configuration();

    try {
      configuration =
          FileUtil.readObjectFromFile(LOCAL_CONFIGURATION_FILE_NAME);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    return configuration;
  }

  private static void persistConfiguration(Configuration configuration) {
    try {
      FileUtil.writeObjectToFile(configuration,
          LOCAL_CONFIGURATION_FILE_NAME);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
