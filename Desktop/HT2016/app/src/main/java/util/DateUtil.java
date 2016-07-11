package util;

import java.util.Hashtable;

/**
 * Created by hongzhang on 8/24/15.
 */
public class DateUtil {
    public static Hashtable<String, String> Datetrans;
    public static Hashtable<String, String> Dtrans;

    static {
        Datetrans = new Hashtable<String, String>();
        Datetrans.put("2016-07-11", "Monday, July.11");
        Datetrans.put("2016-07-12", "Tuesday, July.12");
        Datetrans.put("2016-07-13", "Wednesday, July.13");
        Datetrans.put("2016-07-14", "Thursday, July.14");
        Datetrans.put("2016-07-15", "Friday, July.15");
        Datetrans.put("2016-07-16", "Saturday, July.16");

        Dtrans = new Hashtable<String, String>();
        Dtrans.put("2016-07-11", "1");
        Dtrans.put("2016-07-12", "2");
        Dtrans.put("2016-07-13", "3");
        Dtrans.put("2016-07-14", "4");
        Dtrans.put("2016-07-15", "5");
        Dtrans.put("2016-07-16", "6");
    }
}
