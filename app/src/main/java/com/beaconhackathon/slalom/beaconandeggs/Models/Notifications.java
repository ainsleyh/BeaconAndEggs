package com.beaconhackathon.slalom.beaconandeggs.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ainsleyherndon on 10/18/15.
 */
public class Notifications implements Serializable{

    public Notifications() {
        notifications = new ArrayList<>();
    }

    public List<Notification> notifications;

    /**
     * Returns the number of unread notifications
     *
     * @return the number of unread notifications
     */
    public int UnreadNotifications() {
        int counter = 0;
        
        for (Notification n : notifications) {
            if (!n.hasBeenViewed) {
                counter++;
            }
        }

        return counter;
    }

}
