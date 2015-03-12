package de.ad.tools.redmine.cli;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationTest {
  private Configuration configuration;

  @Before
  public void setUp() throws Exception {
    configuration = new Configuration();
  }

  @Test
  public void testIsConnected() throws Exception {
    configuration.setServer("server");
    configuration.setApiKey("apiKey");

    boolean result = configuration.isConnected();

    assertThat(result).isTrue();
  }

  @Test
  public void testIsNotConnected() throws Exception {
    boolean result = configuration.isConnected();

    assertThat(result).isFalse();
  }

  @Test
  public void testIsNotConnectedAlthoughServerIsSet() throws Exception {
    configuration.setServer("server");

    boolean result = configuration.isConnected();

    assertThat(result).isFalse();
  }

  @Test
  public void testIsNotConnectedAlthoughApiKeyIsSet() throws Exception {
    configuration.setApiKey("apiKey");

    boolean result = configuration.isConnected();

    assertThat(result).isFalse();
  }

  @Test
  public void testReset() throws Exception {
    configuration.setServer("server");
    configuration.setApiKey("apiKey");

    configuration.reset();

    assertThat(configuration.getServer()).isNull();
    assertThat(configuration.getApiKey()).isNull();
  }
}
