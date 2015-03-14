package de.ad.tools.redmine.cli;

import java.io.Serializable;

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
    if (this == o) return true;
    if (!(o instanceof Configuration)) return false;

    Configuration that = (Configuration) o;

    if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) {
      return false;
    }
    if (server != null ? !server.equals(that.server) : that.server != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = server != null ? server.hashCode() : 0;
    result = 31 * result + (apiKey != null ? apiKey.hashCode() : 0);
    return result;
  }
}
