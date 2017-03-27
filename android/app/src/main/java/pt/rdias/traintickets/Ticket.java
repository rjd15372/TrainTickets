package pt.rdias.traintickets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ricardo Dias on 19/09/15.
 */
public class Ticket implements Comparable<Ticket> {

    protected long id;
    protected String from;
    protected String to;
    protected Date departure;
    protected Date arrival;
    protected int car;
    protected int seat;
    protected TicketClass tClass;
    protected int price;

    public Ticket(String from, String to, Date departure, Date arrival, int car, int seat, TicketClass tClass, int price) {
        this.from = from;
        this.to = to;
        this.departure = departure;
        this.arrival = arrival;
        this.car = car;
        this.seat = seat;
        this.tClass = tClass;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        return car == ticket.car && seat == ticket.seat && !(from != null ? !from.equals(ticket.from) : ticket.from != null) && !(to != null ? !to.equals(ticket.to) : ticket.to != null) && !(departure != null ? !departure.equals(ticket.departure) : ticket.departure != null) && !(arrival != null ? !arrival.equals(ticket.arrival) : ticket.arrival != null);

    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (departure != null ? departure.hashCode() : 0);
        result = 31 * result + (arrival != null ? arrival.hashCode() : 0);
        result = 31 * result + car;
        result = 31 * result + seat;
        return result;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Date getDeparture() {
        return departure;
    }

    public String getDepartureStr() {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
        return sfd.format(departure);
    }

    public Calendar getDepartureCal() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(departure);
        return cal;
    }

    public Date getArrival() {
        return arrival;
    }

    public String getArrivalStr() {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
        return sfd.format(arrival);
    }

    public int getCar() {
        return car;
    }

    public int getSeat() {
        return seat;
    }

    public TicketClass gettClass() {
        return tClass;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public int compareTo(Ticket another) {
        return departure.compareTo(another.departure);
    }

    @Override
    public String toString() {
        return from+" "+departure+" -> "+to+" "+arrival+" : "+car+":"+seat;
    }

    public long getId() {
        return id;
    }
}
