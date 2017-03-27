package pt.rdias.traintickets;

import android.content.Context;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

/**
 * Created by Ricardo Dias on 19/09/15.
 */
public class TicketListAdapter extends BaseAdapter {

    private final Context context;
    protected List<Ticket> tickets;

    public TicketListAdapter(Context context, Set<Ticket> tickets) {
        this.context = context;
        this.tickets = new ArrayList<>(tickets);
    }

    @Override
    public int getCount() {
        return tickets.size();
    }

    @Override
    public Object getItem(int position) {
        return tickets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private static final String[] MONTHS = new String[] {
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro",
        "Outubro", "Novembro", "Dezembro"
    };

    private String formatDate(Calendar c) {
        String weekDay = Util.getWeekdayString(c);
        String month = MONTHS[c.get(Calendar.MONTH)];

        return String .format("%s, %02d de %s, %s", weekDay, c.get(Calendar.DAY_OF_MONTH), month, c.get(Calendar.YEAR));
    }

    private boolean isNextTicket(int position) {
        Calendar now = Calendar.getInstance();
        int next = -1;
        for (int i=0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);
            Calendar departureCal = Calendar.getInstance();
            departureCal.setTime(t.getArrival());

            if (now.compareTo(departureCal) < 0) {
                next = i;
                break;
            }
        }
        return next == position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ticket t = tickets.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.ticketlistitem, parent, false);

        Calendar departureCal = Calendar.getInstance();
        departureCal.setTime(t.getDeparture());

        if (isNextTicket(position)) {
            rowView.setBackgroundColor(context.getResources().getColor(R.color.itemBackground));
        }

        TextView textView = (TextView) rowView.findViewById(R.id.titleView);
        textView.setText(String.format("%s -> %s",t.getFrom(), t.getTo()));

        textView = (TextView) rowView.findViewById(R.id.dateView);
        textView.setText(formatDate(departureCal));

        textView = (TextView) rowView.findViewById(R.id.scheduleView);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        textView.setText(String.format("Partida: %s  Chegada: %s", sdf.format(t.getDeparture()),
                sdf.format(t.getArrival())));

        textView = (TextView) rowView.findViewById(R.id.carView);
        textView.setText(String.format("Car: %02d", t.getCar()));

        textView = (TextView) rowView.findViewById(R.id.seatView);
        textView.setText(String.format("Lug: %02d", t.getSeat()));

        return rowView;
    }
}
