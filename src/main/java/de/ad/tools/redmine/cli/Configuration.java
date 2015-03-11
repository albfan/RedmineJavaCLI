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
}
