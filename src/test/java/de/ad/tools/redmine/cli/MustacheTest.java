package de.ad.tools.redmine.cli;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.Test;

public class MustacheTest {
  @Test
  public void testName() throws Exception {
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache m = mf.compile("template");

    List<Issue> issues = Arrays.asList(IssueFactory.create(0, "Issue 1"),
        IssueFactory.create(1, "Issue 2"));

    Function function = new Function() {
      @Override public Object apply(Object o) {
        return ((String)o).toUpperCase();
      }
    };

    Map<String, Object> scope = new HashMap<>();
    
    scope.put("issues", issues);
    scope.put("func", function);
    
    m.execute(new PrintWriter(System.out), scope)
        .flush();
  }
}
