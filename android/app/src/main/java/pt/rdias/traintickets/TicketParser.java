package pt.rdias.traintickets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ricardo Dias on 19/09/15.
 */
public class TicketParser {

    private static Pattern ticketPattern = Pattern.compile("Bilhete \\d+-\\d+, [\\w\\s]+ \\d+, (\\d\\d\\d\\d-\\d\\d-\\d\\d), Partida (\\d\\d:\\d\\d), ([\\w\\s-]+), Chegada (\\d\\d:\\d\\d), ([\\w\\s-]+), Carruagem (\\d+), Lugar (\\d+), ([\\w\\s]+), Preco (\\d+\\.?\\d*)");

    public static List<Ticket> parse(String msgBody) throws ParseException {
        List<Ticket> tickets = new ArrayList<>();

        Matcher m = ticketPattern.matcher(msgBody);
        if (m.find()) {
            String dayDate = m.group(1);
            String departureTime = m.group(2);
            String from = m.group(3);
            String arrivalTime = m.group(4);
            String to = m.group(5);
            String car = m.group(6);
            String seat = m.group(7);
            String tClass = m.group(8);
            String price = m.group(9);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date departure = sdf.parse(dayDate+" "+departureTime);
            Date arrival = sdf.parse(dayDate+" "+arrivalTime);

            Ticket ticket = new Ticket(from, to, departure, arrival, Integer.parseInt(car),
                    Integer.parseInt(seat), TicketClass.parseTicketClass(tClass), Math.round(Float.parseFloat(price)*100));

            tickets.add(ticket);

            if (m.find()) {
                dayDate = m.group(1);
                departureTime = m.group(2);
                from = m.group(3);
                arrivalTime = m.group(4);
                to = m.group(5);
                car = m.group(6);
                seat = m.group(7);
                tClass = m.group(8);
                price = m.group(9);

                departure = sdf.parse(dayDate+" "+departureTime);
                arrival = sdf.parse(dayDate+" "+arrivalTime);

                ticket = new Ticket(from, to, departure, arrival, Integer.parseInt(car),
                        Integer.parseInt(seat), TicketClass.parseTicketClass(tClass), Math.round(Float.parseFloat(price)*100));

                tickets.add(ticket);
            }
        } else {
            System.out.println("NO MATCH");
        }

        return tickets;
    }
}
