package de.ad.tools.redmine.cli.util;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

public class TemplateUtil {
  public static void printTemplate(OutputStream out, String templateFile,
      Object scope) {
    MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    Mustache mustache = mustacheFactory.compile(templateFile);

    Writer writer = new PrintWriter(out);
    try {
      mustache.execute(writer, scope).flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String convertToTable(String s) {
    String[] rows = s.split(System.getProperty("line.separator"));

    String[][] table = new String[rows.length][];
    for (int i = 0; i < table.length; i++) {
      table[i] = rows[i].split("\\|");
    }

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(stream);

    PrintUtil.printTable(out, table);

    return new String(stream.toByteArray());
  }
}
