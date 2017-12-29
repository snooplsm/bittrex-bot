/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wmwm.bittrex.api;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public final class UtcDateTypeAdapter extends TypeAdapter<Date> {
  private final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

  @Override
  public void write(JsonWriter out, Date date) throws IOException {
    if (date == null) {
      out.nullValue();
    } else {
      String value = format(date, true, UTC_TIME_ZONE);
      out.value(value);
    }
  }

  @Override
  public Date read(JsonReader in) throws IOException {
    try {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            default:
                String date = in.nextString();
                return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(date);
        }
    } catch (ParseException e) {
        throw new IllegalArgumentException(e);
    }
  }

  // Date parsing code from Jackson databind ISO8601Utils.java
  // https://github.com/FasterXML/jackson-databind/blob/master/src/main/java/com/fasterxml/jackson/databind/util/ISO8601Utils.java
  private static final String GMT_ID = "GMT";

  /**
   * Format date into yyyy-MM-ddThh:mm:ss[.sss][Z|[+-]hh:mm]
   *
   * @param date the date to format
   * @param millis true to include millis precision otherwise false
   * @param tz timezone to use for the formatting (GMT will produce 'Z')
   * @return the date formatted as yyyy-MM-ddThh:mm:ss[.sss][Z|[+-]hh:mm]
   */
  private static String format(Date date, boolean millis, TimeZone tz) {
      return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(date);
  }
  /**
   * Zero pad a number to a specified length
   *
   * @param buffer buffer to use for padding
   * @param value the integer value to pad if necessary.
   * @param length the length of the string we should zero pad
   */
  private static void padInt(StringBuilder buffer, int value, int length) {
      String strValue = Integer.toString(value);
      for (int i = length - strValue.length(); i > 0; i--) {
          buffer.append('0');
      }
      buffer.append(strValue);
  }

  /**
   * Parse a date from ISO-8601 formatted string. It expects a format
   * [yyyy-MM-dd|yyyyMMdd][T(hh:mm[:ss[.sss]]|hhmm[ss[.sss]])]?[Z|[+-]hh:mm]]
   *
   * @param date ISO string to parse in the appropriate format.
   * @param pos The position to start parsing from, updated to where parsing stopped.
   * @return the parsed date
   * @throws ParseException if the date is not in the appropriate format
   */
  private static Date parse(String date, ParsePosition pos) throws ParseException {
    Exception fail = null;
    try {
      int offset = pos.getIndex();

      // extract year
      int year = parseInt(date, offset, offset += 4);
      if (checkOffset(date, offset, '-')) {
        offset += 1;
      }

      // extract month
      int month = parseInt(date, offset, offset += 2);
      if (checkOffset(date, offset, '-')) {
        offset += 1;
      }

      // extract day
      int day = parseInt(date, offset, offset += 2);
      // default time value
      int hour = 0;
      int minutes = 0;
      int seconds = 0;
      int milliseconds = 0; // always use 0 otherwise returned date will include millis of current time
      if (checkOffset(date, offset, 'T')) {

        // extract hours, minutes, seconds and milliseconds
        hour = parseInt(date, offset += 1, offset += 2);
        if (checkOffset(date, offset, ':')) {
          offset += 1;
        }

        minutes = parseInt(date, offset, offset += 2);
        if (checkOffset(date, offset, ':')) {
          offset += 1;
        }
        // second and milliseconds can be optional
        if (date.length() > offset) {
          char c = date.charAt(offset);
          if (c != 'Z' && c != '+' && c != '-') {
            seconds = parseInt(date, offset, offset += 2);
            // milliseconds can be optional in the format
            if (checkOffset(date, offset, '.')) {
              milliseconds = parseInt(date, offset += 1, offset += 3);
            }
          }
        }
      }

      // extract timezone
      String timezoneId;
      if (date.length() <= offset) {
        throw new IllegalArgumentException("No time zone indicator");
      }
      char timezoneIndicator = date.charAt(offset);
      if (timezoneIndicator == '+' || timezoneIndicator == '-') {
        String timezoneOffset = date.substring(offset);
        timezoneId = GMT_ID + timezoneOffset;
        offset += timezoneOffset.length();
      } else if (timezoneIndicator == 'Z') {
        timezoneId = GMT_ID;
        offset += 1;
      } else {
        throw new IndexOutOfBoundsException("Invalid time zone indicator " + timezoneIndicator);
      }

      TimeZone timezone = TimeZone.getTimeZone(timezoneId);
      if (!timezone.getID().equals(timezoneId)) {
        throw new IndexOutOfBoundsException();
      }

      Calendar calendar = new GregorianCalendar(timezone);
      calendar.setLenient(false);
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.MONTH, month - 1);
      calendar.set(Calendar.DAY_OF_MONTH, day);
      calendar.set(Calendar.HOUR_OF_DAY, hour);
      calendar.set(Calendar.MINUTE, minutes);
      calendar.set(Calendar.SECOND, seconds);
      calendar.set(Calendar.MILLISECOND, milliseconds);

      pos.setIndex(offset);
      return calendar.getTime();
      // If we get a ParseException it'll already have the right message/offset.
      // Other exception types can convert here.
    } catch (IndexOutOfBoundsException e) {
      fail = e;
    } catch (NumberFormatException e) {
      fail = e;
    } catch (IllegalArgumentException e) {
      fail = e;
    }
    String input = (date == null) ? null : ("'" + date + "'");
    throw new ParseException("Failed to parse date [" + input + "]: " + fail.getMessage(), pos.getIndex());
  }

  /**
   * Check if the expected character exist at the given offset in the value.
   *
   * @param value the string to check at the specified offset
   * @param offset the offset to look for the expected character
   * @param expected the expected character
   * @return true if the expected character exist at the given offset
   */
  private static boolean checkOffset(String value, int offset, char expected) {
    return (offset < value.length()) && (value.charAt(offset) == expected);
  }

  /**
   * Parse an integer located between 2 given offsets in a string
   *
   * @param value the string to parse
   * @param beginIndex the start index for the integer in the string
   * @param endIndex the end index for the integer in the string
   * @return the int
   * @throws NumberFormatException if the value is not a number
   */
  private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
    if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
      throw new NumberFormatException(value);
    }
    // use same logic as in Integer.parseInt() but less generic we're not supporting negative values
    int i = beginIndex;
    int result = 0;
    int digit;
    if (i < endIndex) {
      digit = Character.digit(value.charAt(i++), 10);
      if (digit < 0) {
        throw new NumberFormatException("Invalid number: " + value);
      }
      result = -digit;
    }
    while (i < endIndex) {
      digit = Character.digit(value.charAt(i++), 10);
      if (digit < 0) {
        throw new NumberFormatException("Invalid number: " + value);
      }
      result *= 10;
      result -= digit;
    }
    return -result;
  }
}