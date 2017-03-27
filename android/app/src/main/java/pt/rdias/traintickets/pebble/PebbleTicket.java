package pt.rdias.traintickets.pebble;

import com.getpebble.android.kit.util.PebbleDictionary;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pt.rdias.traintickets.Ticket;

/**
 * Created by Ricardo Dias on 24/09/15.
 */
public class PebbleTicket implements PebbleMessage {

    private static final int MAX_CHARS = 8;
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    protected String to;
    protected Calendar departure;
    protected int car;
    protected int seat;

    public PebbleTicket(String to, Calendar departure, int car, int seat) {
        this.to = to;
        this.departure = departure;
        this.car = car;
        this.seat = seat;
    }

    public PebbleTicket(Ticket ticket) {
        this(ticket.getTo(), ticket.getDepartureCal(), ticket.getCar(), ticket.getSeat());
    }

    public void fillMessage(PebbleDictionary dict, int baseKey) {
        String time = timeFormat.format(departure.getTime());
        dict.addString(baseKey, to.substring(0, to.length() > MAX_CHARS ? MAX_CHARS : to.length())+" "+time);
        dict.addInt8(baseKey + 1, (byte) car);
        dict.addInt8(baseKey + 2, (byte) seat);
    }

    @Override
    public int getNumKeys() {
        return 3;
    }
}
