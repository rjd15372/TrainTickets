package pt.rdias.traintickets;

import java.util.Calendar;

/**
 * Created by Ricardo Dias on 24/09/15.
 */
public class Util {
    public static final String[] WEEKDAYS = new String[] {
            "Domingo", "Segunda-Feira", "Terça-Feira", "Quarta-Feira", "Quinta-feira", "Sexta-feira"
            , "Sábado"
    };

    public static String getWeekdayString(Calendar cal) {
        return WEEKDAYS[cal.get(Calendar.DAY_OF_WEEK)-1];
    }
}
