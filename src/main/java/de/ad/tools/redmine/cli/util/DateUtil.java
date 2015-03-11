package de.ad.tools.redmine.cli.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class DateUtil {

  private DateUtil() {
  }

  public static String getTimeDifferenceAsText(Date date) {
    if (date == null) {
      return null;
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime then = LocalDateTime
        .ofInstant(date.toInstant(), ZoneId.systemDefault());

    long years = ChronoUnit.YEARS.between(then, now);
    if (years > 1) {
      return String.format("over %d years", years);
    } else if (years > 0) {
      return "a year";
    }

    long months = ChronoUnit.MONTHS.between(then, now);
    if (months > 1) {
      return String.format("%d months", months);
    } else if (months > 0) {
      return "a month";
    }

    long weeks = ChronoUnit.WEEKS.between(then, now);
    if (weeks > 1) {
      return String.format("%d weeks", weeks);
    } else if (weeks > 0) {
      return "a week";
    }

    long days = ChronoUnit.DAYS.between(then, now);
    if (days > 1) {
      return String.format("%d days", days);
    } else if (days > 0) {
      return "a day";
    }

    long hours = ChronoUnit.HOURS.between(then, now);
    if (hours > 1) {
      return String.format("%d hours", hours);
    } else if (hours > 0) {
      return "an hour";
    }

    long minutes = ChronoUnit.MINUTES.between(then, now);
    if (minutes > 1) {
      return String.format("%d minutes", minutes);
    } else if (minutes > 0) {
      return "a minute";
    }

    return "a few moments";
  }
}
