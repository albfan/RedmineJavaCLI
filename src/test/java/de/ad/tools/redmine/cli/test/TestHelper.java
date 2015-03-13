package de.ad.tools.redmine.cli.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestHelper {
  public static byte[] resourceToByteArray(String resource) throws IOException {
    InputStream in = TestHelper.class.getResourceAsStream(resource);

    return toByteArray(in);
  }

  private static byte[] toByteArray(InputStream in) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    int data;
    while ((data = in.read()) != -1) {
      bytes.write(data);
    }

    return bytes.toByteArray();
  }
}
