package com.beaconhackathon.slalom.beaconandeggs.Models;

/**
 * The State of the Item
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public enum State {

    /**
     * In cart, and has been picked up by user
     */
    Checked,

    /**
     * Available for cart pick up, and not yet pick up by user
     */
    Available,

    /**
     * Removed from the list, and not to be displayed to the user
     * (Note, we will keep track of removed items from list, but not display)
     */
    Removed
}
