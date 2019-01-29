package zbl.fly.base.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonUtils {
    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    public static Date localDateToDate(final LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    public static Date localDateTimeToDate(LocalDateTime start) {
        return Date.from(start.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    public static LocalDate dateToLocalDate(Date here) {
        return here.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    }

    public static ZonedDateTime dateToZonedDateTime(Date start) {
        return start.toInstant().atZone(DEFAULT_ZONE_ID);
    }

    public static List<Date> convertBetweenDateToListDate(Date startDate, Date endDate) {
        LocalDate startLocalDate = startDate.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate().plusDays(1);
        List<Date> dates = new ArrayList<>();
        buildDates(startLocalDate, endLocalDate, dates);
        return dates;
    }

    private static void buildDates(LocalDate startLocalDate, LocalDate endLocalDate, List<Date> dates) {
        if (startLocalDate.isBefore(endLocalDate) || startLocalDate.isEqual(endLocalDate)) {
            dates.add(localDateTimeToDate(startLocalDate.atStartOfDay()));
            buildDates(startLocalDate.plusDays(1), endLocalDate, dates);
        }
    }

}
