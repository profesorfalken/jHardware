/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jutils.jhardware.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts the CIM datetime format used by WMI
 * ({@code yyyyMMddHHmmss.ffffff+zzz}) to ISO-8601.
 */
public final class WmiDateUtils {

    private static final Pattern DATE_ONLY = Pattern.compile(
            "^(\\d{4})(\\d{2})(\\d{2})$");
    private static final Pattern DATE_TIME = Pattern.compile(
            "^(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})");
    private static final Pattern OFFSET = Pattern.compile("([+-])(\\d{3})\\s*$");

    private WmiDateUtils() {
    }

    /**
     * @param raw WMI date or datetime
     * @return {@code yyyy-MM-dd}, or the original value when it cannot be parsed
     */
    public static String toIsoDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        Matcher dateOnly = DATE_ONLY.matcher(raw.trim());
        if (dateOnly.matches()) {
            try {
                return LocalDate.of(
                        Integer.parseInt(dateOnly.group(1)),
                        Integer.parseInt(dateOnly.group(2)),
                        Integer.parseInt(dateOnly.group(3))).toString();
            } catch (DateTimeException | NumberFormatException ex) {
                return raw;
            }
        }
        Matcher matcher = DATE_TIME.matcher(raw.trim());
        if (!matcher.find()) {
            return raw;
        }
        try {
            LocalDate date = LocalDate.of(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)));
            return date.toString();
        } catch (DateTimeException | NumberFormatException ex) {
            return raw;
        }
    }

    /**
     * @param raw WMI datetime
     * @return an ISO-8601 datetime with offset when available, otherwise the
     *         original value
     */
    public static String toIsoDateTime(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        Matcher matcher = DATE_TIME.matcher(raw.trim());
        if (!matcher.find()) {
            return raw;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.of(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)),
                    Integer.parseInt(matcher.group(6)));
            Matcher offsetMatcher = OFFSET.matcher(raw.trim());
            if (offsetMatcher.find()) {
                int sign = "-".equals(offsetMatcher.group(1)) ? -1 : 1;
                // CIM stores the UTC offset as a signed 3-digit number of minutes.
                int totalMinutes = Integer.parseInt(offsetMatcher.group(2)) * sign;
                ZoneOffset offset = ZoneOffset.ofTotalSeconds(totalMinutes * 60);
                return OffsetDateTime.of(dateTime, offset)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
            return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeException | NumberFormatException ex) {
            return raw;
        }
    }
}
