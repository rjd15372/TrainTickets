package pt.rdias.traintickets;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pt.rdias.traintickets.pebble.PebbleTicket;
import pt.rdias.traintickets.pebble.PebbleTicketSection;

/**
 * Created by Ricardo Dias on 19/09/15.
 */
public class TicketManager {

    public static final Set<String> ADDRESSES = new HashSet<>(Arrays.asList(new String[]{"12605", "30131"}));

    public static Set<Ticket> retriveTickets(Context context) {
        Set<Ticket> tickets = new TreeSet<>();

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"),
                new String[] { "_id", "address", "body", "read" } , null,
                null, null);

        Calendar now = Calendar.getInstance();

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                if (ADDRESSES.contains(cursor.getString(1))) {
                    try {
                        List<Ticket> ticks = TicketParser.parse(cursor.getString(2));
                        for (Ticket t : ticks) {
                            t.setId(cursor.getLong(0));
                        }
                        for (Ticket t : ticks) {
                            Calendar arrCal = Calendar.getInstance();
                            arrCal.setTime(t.getArrival());

                            if (now.compareTo(arrCal) <= 0)
                                tickets.add(t);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            } while (cursor.moveToNext());

        }

        cursor.close();

        return tickets;
    }

    public static void deleteTicket(Context context, Ticket t) {
        context.getContentResolver().delete(Uri.parse("content://sms/" + t.getId()), null, null);
    }

    public static void setAsRead(Context context, Ticket t) {
        ContentValues values = new ContentValues();
        values.put("read",0);
        values.put("seen",0);

        context.getContentResolver().update(Uri.parse("content://sms/"), values, "_id=" + t.getId(), null);
    }

    private static final String NEXT_WEEK_SECTION_PREFIX = "Semana ";

    private static void addToSection(Map<String,PebbleTicketSection> sections, String sectionKey, Ticket t) {
        PebbleTicketSection section = sections.get(sectionKey);
        if (section == null) {
            section = new PebbleTicketSection(sectionKey);
            sections.put(sectionKey, section);
        }
        section.addPebbleTicker(new PebbleTicket(t));
    }

    public static List<PebbleTicketSection> getPebbleTickets(Context context) {
        Set<Ticket> tickets = retriveTickets(context);

        if (tickets.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String,PebbleTicketSection> sections = new LinkedHashMap<>();

        List<PebbleTicketSection> pebbleTickets = new ArrayList<>();

        //Calendar now = Calendar.getInstance();
        //int current_week = now.get(Calendar.WEEK_OF_YEAR);

        Calendar first = tickets.iterator().next().getDepartureCal();
        int current_week = first.get(Calendar.WEEK_OF_YEAR);

        for (Ticket ticket : tickets) {
            Calendar tCal = ticket.getDepartureCal();
            int ticket_week = tCal.get(Calendar.WEEK_OF_YEAR);
            if (ticket_week == current_week) {
                String weekDay = Util.getWeekdayString(tCal)+"  "+
                        String.format("%02d/%02d", tCal.get(Calendar.DAY_OF_MONTH), tCal.get(Calendar.MONTH)+1);
                addToSection(sections, weekDay, ticket);
            }
            else if (ticket_week > current_week) {
                int weekDay = tCal.get(Calendar.DAY_OF_WEEK);
                Calendar weekBegin = (Calendar) tCal.clone();
                weekBegin.add(Calendar.DAY_OF_YEAR, -(weekDay-2));

                Calendar weekEnd = (Calendar) tCal.clone();
                weekEnd.add(Calendar.DAY_OF_YEAR, 8-weekDay);

                String dayBegin = String.format("%02d", weekBegin.get(Calendar.DAY_OF_MONTH));
                String monthBegin = String.format("%02d", weekBegin.get(Calendar.MONTH)+1);
                String dayEnd = String.format("%02d", weekEnd.get(Calendar.DAY_OF_MONTH));
                String monthEnd = String.format("%02d", weekEnd.get(Calendar.MONTH)+1);

                String title = NEXT_WEEK_SECTION_PREFIX + dayBegin+"/"+monthBegin+" a "+dayEnd+"/"+monthEnd;

                addToSection(sections, title, ticket);
            }

        }

        for (PebbleTicketSection section : sections.values()) {
            pebbleTickets.add(section);
        }


        return pebbleTickets;
    }
}
