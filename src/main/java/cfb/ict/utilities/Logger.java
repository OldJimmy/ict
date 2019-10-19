package cfb.ict.utilities;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static final Format FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void log(final String message) {

        System.out.println(FORMAT.format(new Date(System.currentTimeMillis())) + ": " + message);
    }
}
