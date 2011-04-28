/*
 * Copyright (c) 2011, Michael Hohl
 *
 * All rights reserved.
 */

package at.co.hohl.Announcer;

import java.util.Random;

/**
 * Thread which handles the announcing.
 *
 * @author Michael Hohl
 */
class AnnouncerThread extends Thread {
    /** Tool used for generating random numbers. */
    private final Random randomGenerator;

    /** The plugin which holds this thread. */
    private final AnnouncerPlugin plugin;

    /** The last announcement index. (Only for sequential announcing.) */
    private int lastAnnouncement = 0;

    /**
     * Allocates a new scheduled announcer thread.
     *
     * @param plugin the plugin which holds the thread.
     */
    public AnnouncerThread(AnnouncerPlugin plugin) {
        randomGenerator = new Random();
        this.plugin = plugin;
    }

    /** The main method of the thread. */
    @Override
    public void run() {
        if (plugin.enabled) {
            if (plugin.random) {
                lastAnnouncement = Math.abs(randomGenerator.nextInt()) % plugin.announcementMessages.size();
            } else {
                if ((++lastAnnouncement) >= plugin.announcementMessages.size()) {
                    lastAnnouncement = 0;
                }
            }

            if (lastAnnouncement < plugin.announcementMessages.size()) {
                plugin.announce(lastAnnouncement + 1);
            }
        }
    }
}
