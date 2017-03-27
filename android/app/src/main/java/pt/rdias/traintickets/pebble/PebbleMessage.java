package pt.rdias.traintickets.pebble;

import com.getpebble.android.kit.util.PebbleDictionary;

/**
 * Created by Ricardo Dias on 24/09/15.
 */
public interface PebbleMessage {
    void fillMessage(PebbleDictionary dict, int baseKey);
    int getNumKeys();
}
