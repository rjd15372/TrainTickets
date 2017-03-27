package pt.rdias.traintickets;

import android.content.Context;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import pt.rdias.traintickets.pebble.PebbleTicketSection;

/**
 * Created by Ricardo Dias on 17/09/15.
 */
public class TrainTicketReceiver extends PebbleKit.PebbleDataReceiver {

    private static final UUID APP_UUID = UUID.fromString("2ad16ed6-9ae2-4f05-a458-f8d35e403b2f");
    private static final int HANDSHAKE_KEY = 0;

    private static final int TOTAL_SECTIONS_KEY = 1;
    private static final int SECTION_OFFSET_KEY = 96;
    private static final int TICKET_OFFSET_KEY = 97;

    private static final int PROTOCOL_STATE_INIT = 100;
    private static final int PROTOCOL_STATE_RUNNING = 101;

    private static final int NUM_TICKETS_PER_MESSAGE = 2;

    public TrainTicketReceiver() {
        super(APP_UUID);
    }

    private static List<PebbleTicketSection> sections = null;

    @Override
    public void receiveData(Context context, int transactionId, PebbleDictionary data) {
        PebbleKit.sendAckToPebble(context, transactionId);

        Long value = data.getInteger(HANDSHAKE_KEY);

        if (value != null && value == PROTOCOL_STATE_INIT) {
            sections = TicketManager.getPebbleTickets(context);

            PebbleDictionary result = new PebbleDictionary();

            result.addInt8(HANDSHAKE_KEY, (byte) PROTOCOL_STATE_INIT);
            result.addInt8(TOTAL_SECTIONS_KEY, (byte) sections.size());

            PebbleKit.sendDataToPebble(context, APP_UUID, result);
        }
        else if (value != null && value == PROTOCOL_STATE_RUNNING) {

            PebbleDictionary result = new PebbleDictionary();

            result.addInt8(HANDSHAKE_KEY, (byte) PROTOCOL_STATE_RUNNING);

            int section_offset = (int)data.getInteger(SECTION_OFFSET_KEY).longValue();
            int ticket_offset = (int)data.getInteger(TICKET_OFFSET_KEY).longValue();


            PebbleTicketSection section = sections.get(section_offset);

            section.fillMessage(result, 1, ticket_offset, NUM_TICKETS_PER_MESSAGE);


            PebbleKit.sendDataToPebble(context, APP_UUID, result);

        }

    }

}
