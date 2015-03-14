package de.ad.tools.redmine.cli;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Configuration implements Serializable {
  private String server;
  private String apiKey;

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String auth) {
    this.apiKey = auth;
  }

  public boolean isConnected() {
    return server != null && apiKey != null;
  }

  public void reset() {
    server = null;
    apiKey = null;
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o, true);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, true);
  }
}
