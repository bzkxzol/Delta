import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateHelpers {

    public static final String DEFAULT_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss.SSS";

    public SimpleDateFormat getDateFormat(String dateFormat, TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        if (timeZone != null) {
            sdf.setTimeZone(timeZone);
        }

        return sdf;
    }

    public String formatStringDate(String date, String currentFormat, String newFormat) throws ParseException {
        SimpleDateFormat newDateFormat = getDateFormat(currentFormat, (TimeZone)null);
        Date myDate = newDateFormat.parse(date);
        newDateFormat.applyPattern(newFormat);
        return newDateFormat.format(myDate);
    }

    public String dateDifferenceInMs(String stringDate1, String stringDate2) throws ParseException {
        String difference = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date date1 = dateFormat.parse(stringDate1);
        Date date2 = dateFormat.parse(stringDate2);
        long milliseconds = date2.getTime() - date1.getTime();
        // 1000 миллисекунд = 1 секунда
        int seconds = (int) (milliseconds / (1000));
        return difference = "Delta: " + milliseconds + "ms";

    }
}

