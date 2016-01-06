package Updater.Git;

/**
 * Created by Unknown on 6/01/2016.
 */
@FunctionalInterface
public interface ParseListener {
    public abstract void parseResponse(String returnedString);
}
