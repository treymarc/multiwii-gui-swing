package eu.kprod;


import java.text.MessageFormat;

public class I18n {

  public static String format(String fmt, Object ... args) {
    return MessageFormat.format(fmt, args);
  }
}
