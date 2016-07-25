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

    boolean result = configuration.isConfigured();

    assertThat(result).isTrue();
  }

  @Test
  public void testIsNotConnected() throws Exception {
    boolean result = configuration.isConfigured();

    assertThat(result).isFalse();
  }

  @Test
  public void testIsNotConnectedAlthoughServerIsSet() throws Exception {
    configuration.setServer("server");

    boolean result = configuration.isConfigured();

    assertThat(result).isFalse();
  }

  @Test
  public void testIsNotConnectedAlthoughApiKeyIsSet() throws Exception {
    configuration.setApiKey("apiKey");

    boolean result = configuration.isConfigured();

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

  @Test
  public void testEquals() throws Exception {
    Configuration configuration1 = new Configuration();
    Configuration configuration2 = new Configuration();

    assertThat(configuration1.equals(configuration2)).isTrue();
  }

  @Test
  public void testHashCode() throws Exception {
    Configuration configuration1 = new Configuration();
    Configuration configuration2 = new Configuration();

    assertThat(configuration1.hashCode()).isEqualTo(configuration2.hashCode());
  }
}
