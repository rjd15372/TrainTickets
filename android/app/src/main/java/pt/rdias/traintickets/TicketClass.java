package pt.rdias.traintickets;

/**
 * Created by Ricardo Dias on 19/09/15.
 */
public class TicketClass {

    public static final TicketClass FIRST_CLASS = new TicketClass();
    public static final TicketClass SECOND_CLASS = new TicketClass();

    private TicketClass() {}

    public static TicketClass parseTicketClass(String tClass) {
        if (tClass.equals("Turistica")) {
            return SECOND_CLASS;
        }
        else if (tClass.equals("2a Classe")) {
            return SECOND_CLASS;
        }
        else if (tClass.equals("Conforto")) {
            return FIRST_CLASS;
        }
        else if (tClass.equals("1a Classe")) {
            return FIRST_CLASS;
        }
        else {
            return null;
        }
    }
}
