/* Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */
 
 
package org.kobjects.isodate;

import java.util.*;

public class IsoDate {

    public static final int DATE = 1;
    public static final int TIME = 2;
    public static final int DATE_TIME = 3;

    static void dd(StringBuffer buf, int i) {
        buf.append((char) (((int) '0') + i / 10));
        buf.append((char) (((int) '0') + i % 10));
    }

    public static String dateToString(Date date, int type) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        c.setTime(date);

        StringBuffer buf = new StringBuffer();

        if ((type & DATE) != 0) {
            int year = c.get(Calendar.YEAR);
            dd(buf, year / 100);
            dd(buf, year % 100);
            buf.append('-');
            dd(
                buf,
                c.get(Calendar.MONTH) - Calendar.JANUARY + 1);
            buf.append('-');
            dd(buf, c.get(Calendar.DAY_OF_MONTH));

            if (type == DATE_TIME)
                buf.append("T");
        }

        if ((type & TIME) != 0) {
            dd(buf, c.get(Calendar.HOUR_OF_DAY));
            buf.append(':');
            dd(buf, c.get(Calendar.MINUTE));
            buf.append(':');
            dd(buf, c.get(Calendar.SECOND));
            buf.append('.');
            int ms = c.get(Calendar.MILLISECOND);
            buf.append((char) (((int) '0') + (ms / 100)));
            dd(buf, ms % 100);
            buf.append('Z');
        }

        return buf.toString();
    }

    public static Date stringToDate(String text, int type) {

        Calendar c = Calendar.getInstance();

        if ((type & DATE) != 0) {
            c.set(
                Calendar.YEAR,
                Integer.parseInt(text.substring(0, 4)));
            c.set(
                Calendar.MONTH,
                Integer.parseInt(text.substring(5, 7))
                    - 1
                    + Calendar.JANUARY);
            c.set(
                Calendar.DAY_OF_MONTH,
                Integer.parseInt(text.substring(8, 10)));

            if (type != DATE_TIME || text.length () < 11) {
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);            	
	            c.set(Calendar.MILLISECOND, 0);
	            return c.getTime();
            }
            text = text.substring(11);
        }
        else 
        	c.setTime(new Date(0));


        c.set(
            Calendar.HOUR_OF_DAY,
            Integer.parseInt(text.substring(0, 2)));
        // -11
        c.set(
            Calendar.MINUTE,
            Integer.parseInt(text.substring(3, 5)));
        c.set(
            Calendar.SECOND,
            Integer.parseInt(text.substring(6, 8)));

        int pos = 8;
        if (pos < text.length() && text.charAt(pos) == '.') {
            int ms = 0;
            int f = 100;
            while (true) {
                char d = text.charAt(++pos);
                if (d < '0' || d > '9')
                    break;
                ms += (d - '0') * f;
                f /= 10;
            }
            c.set(Calendar.MILLISECOND, ms);
        }
        else
            c.set(Calendar.MILLISECOND, 0);

        if (pos < text.length()) {

            if (text.charAt(pos) == '+'
                || text.charAt(pos) == '-')
                c.setTimeZone(
                    TimeZone.getTimeZone(
                        "GMT" + text.substring(pos)));

            else if (text.charAt(pos) == 'Z')
                c.setTimeZone(TimeZone.getTimeZone("GMT"));
            else
                throw new RuntimeException("illegal time format!");
        }

        return c.getTime();
    }
}
