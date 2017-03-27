package pt.rdias.traintickets.pebble;

import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo Dias on 24/09/15.
 */
public class PebbleTicketSection implements PebbleMessage {

    protected String title;
    protected List<PebbleTicket> tickets;

    public PebbleTicketSection(String title) {
        this.title = title;
        this.tickets = new ArrayList<>();
    }

    public void addPebbleTicker(PebbleTicket ticket) {
        tickets.add(ticket);
    }

    public int getNumTickets() {
        return tickets.size();
    }

    public void fillMessage(PebbleDictionary dict, int baseKey) {
        dict.addInt8(baseKey, (byte) tickets.size());
        dict.addString(baseKey + 2, title);
    }

    public void fillMessage(PebbleDictionary dict, int baseKey, int ticketOffset, int numTickets) {
        int count = 0;
        int offset = 3;
        for (int i=ticketOffset; i < (ticketOffset+numTickets) && i < tickets.size(); i++) {
            PebbleTicket t = tickets.get(i);
            t.fillMessage(dict, baseKey + offset);
            offset += t.getNumKeys();
            count++;
        }

        dict.addInt8(baseKey + 1, (byte) count);
        fillMessage(dict, baseKey);
    }

    @Override
    public int getNumKeys() {
        int keys = 0;
        for (PebbleTicket t : tickets)
            keys += t.getNumKeys();
        return 3 + keys;
    }
}
