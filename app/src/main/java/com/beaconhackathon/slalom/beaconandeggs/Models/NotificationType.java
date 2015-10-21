package com.beaconhackathon.slalom.beaconandeggs.Models;

import java.io.Serializable;

/**
 * The Types of Notification messages
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public enum NotificationType implements Serializable {

    /**
     * A yummy food sample
     */
    Sample,

    /**
     * A nearby deal
     */
    Coupon

}
