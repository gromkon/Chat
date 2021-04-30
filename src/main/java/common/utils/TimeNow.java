package common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Генерирует текущую дату в формате "dd-MM-yy hh:mm:ss aa"
 */
public class TimeNow {

    public static String getDate() {
        DateFormat dfInput = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dateNow = new Date();
        String input = dfInput.format(dateNow);
        DateFormat dfOutput = new SimpleDateFormat("dd-MM-yy hh:mm:ss aa");
        Date date;
        String output = null;
        try {
            date = dfInput.parse(input);
            output = dfOutput.format(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }
}
