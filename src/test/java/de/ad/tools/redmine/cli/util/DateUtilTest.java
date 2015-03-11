package de.ad.tools.redmine.cli.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilTest {

  @Test
  public void testGetSeveralYearsDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusYears(2);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("over 2 years");
  }

  @Test
  public void testGetOneYearDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusYears(1);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("a year");
  }

  @Test
  public void testGetSeveralMonthsDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusMonths(2);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("2 months");
  }

  @Test
  public void testGetOneMonthDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusMonths(1);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("a month");
  }

  @Test
  public void testGetSeveralWeeksDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusWeeks(2);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("2 weeks");
  }

  @Test
  public void testGetOneWeekDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusWeeks(1);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("a week");
  }

  @Test
  public void testGetSeveralDaysDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusDays(2);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("2 days");
  }

  @Test
  public void testGetOneDayDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusDays(1);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("a day");
  }

  @Test
  public void testGetSeveralHoursDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusHours(2);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("2 hours");
  }

  @Test
  public void testGetOneHourDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusHours(1);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("an hour");
  }

  @Test
  public void testGetSeveralMinutesDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusMinutes(2);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("2 minutes");
  }

  @Test
  public void testGetOneMinuteDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusMinutes(1);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("a minute");
  }

  @Test
  public void testGetMomentsDifference() throws Exception {
    LocalDateTime then = LocalDateTime.now().minusSeconds(10);
    Date date = Date.from(then.atZone(ZoneId.systemDefault()).toInstant());

    String result = DateUtil.getTimeDifferenceAsText(date);

    assertThat(result).matches("a few moments");
  }

  @Test
  public void testGetDifferenceWithNull() throws Exception {
    String result = DateUtil.getTimeDifferenceAsText(null);

    assertThat(result).isNull();
  }

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<?>[] constructors =
        DateUtil.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
  }
}
