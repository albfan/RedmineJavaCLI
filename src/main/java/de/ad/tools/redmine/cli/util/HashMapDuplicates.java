package de.ad.tools.redmine.cli.util;

import java.util.*;

public class HashMapDuplicates extends HashMap<String, String> {

  Set<Entry<String, String>> entries;

  @Override
  public Set<Entry<String, String>> entrySet() {
    if (entries == null) {
      entries = new AbstractSet<Entry<String, String>>() {

        ArrayList<Entry<String, String>> list = new ArrayList<>();

        @Override
        public Iterator<Entry<String, String>> iterator() {
          return list.iterator();
        }

        @Override
        public int size() {
          return list.size();
        }

        @Override
        public boolean add(Entry<String, String> stringStringEntry) {
          return list.add(stringStringEntry);
        }
      };
    }
    return entries;
  }

  @Override
  public int size() {
    return entries.size();
  }

  public String put(String key, String value) {
    Set<Entry<String, String>> entries = entrySet();
    EntryDuplicates entry = new EntryDuplicates();
    entry.setKey(key);
    entry.setValue(value);
    entries.add(entry);
    return value;
  }

  public static void addFormParameterEqual(Map<String, String> parameters, String key, String value) {
    addFormParameter(parameters, key, value, "=");
  }

  public static void addFormParameter(Map<String, String> parameters, String key, String value, String op) {
    parameters.put("f[]", key);
    parameters.put("op["+key+"]", op);
    parameters.put("v["+key+"][]",value);
  }
}
