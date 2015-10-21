package com.beaconhackathon.slalom.beaconandeggs.Models;

import java.io.Serializable;

/**
 * A Notification message
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public class Notification implements Serializable {

    public Notification() {
        hasBeenViewed = true;
    }

    /**
     * The title
     */
    public String title;

    /**
     * The enticing message
     */
    public String message;

    /**
     * Whether the notification has been viewed by the user
     */
    public Boolean hasBeenViewed;

    /**
     * The NotificationType
     */
    public NotificationType type;

}