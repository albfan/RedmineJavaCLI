package de.ad.tools.redmine.cli.util;

import java.util.Map;

public class EntryDuplicates implements Map.Entry<String, String> {
  String key;
  String value;

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public String setValue(String value) {
    this.value = value;
    return value;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
