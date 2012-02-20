/*
 * Copyright (c) 2011, Michael Hohl
 *
 * All rights reserved.
 */

package at.co.hohl.Announcer;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Scheduled AnnouncerPlugin for Bukkit.
 *
 * @author hohl
 */
public class AnnouncerPlugin extends JavaPlugin {
  /**
   * Messages to be announced.
   */
  protected List<String> announcementMessages;

  /**
   * The tag used for the broadcast.
   */
  protected String announcementTag;

  /**
   * The color used for the announcement.
   */
  protected ChatColor announcementMessageColor;

  /**
   * The color used for the announcement.
   */
  protected ChatColor announcementTagColor;

  /**
   * Period used for announcing.
   */
  protected long announcementInterval;

  /**
   * Flag if the plugin is enabled.
   */
  protected boolean enabled;

  /**
   * Flag if the plugin should output the announcements randomly.
   */
  protected boolean random;

  /**
   * Sends to all users.
   */
  protected boolean sendToAll;

  /**
   * Thread used to announcing.
   */
  private AnnouncerThread announcerThread;

  /**
   * The executor for the /announce command.
   */
  private AnnouncerCommandExecutor announcerCommandExecutor;

  /**
   * The logger used to output logging information.
   */
  private Logger logger;

  /**
   * Allocates a new AnnouncerPlugin plugin. Any initialisation code is here. NOTE: Event registration should be done
   * in onEnable not here as all events are unregistered when a plugin is disabled
   */
  public AnnouncerPlugin() {
    super();

    announcerThread = new AnnouncerThread(this);
    announcerCommandExecutor = new AnnouncerCommandExecutor(this);
  }

  /**
   * Called when enabling the plugin.
   */
  public void onEnable() {
    logger = getServer().getLogger();

    // Load configuration.
    reloadConfiguration();

    // Register the schedule.
    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler
            .scheduleSyncRepeatingTask(this, announcerThread, announcementInterval * 10, announcementInterval * 10);

    // Register command executor.
    getCommand("announce").setExecutor(announcerCommandExecutor);
    getCommand("announcer").setExecutor(announcerCommandExecutor);

    // Logging.
    logger.info(String.format("%s is enabled!\n", getDescription().getFullName()));
  }

  /**
   * Called when disabling the plugin.
   */
  public void onDisable() {
    // Logging.
    logger.info(String.format("%s is disabled!\n", getDescription().getFullName()));
  }

  /**
   * Broadcasts an announcement.
   */
  public void announce() {
    announcerThread.run();
  }

  /**
   * Broadcasts an announcement.
   *
   * @param index 1 based index. (Like in the list output.)
   */
  public void announce(int index) {
    announce(announcementMessages.get(index - 1));
  }

  /**
   * Broadcasts an announcement.
   *
   * @param line the messages to promote.
   */
  public void announce(String line) {
    String[] messages = line.split("&n");
    for (String message : messages) {
      if (message.startsWith("/")) {
        getServer().dispatchCommand(getServer().getConsoleSender(), message.substring(1));
      } else {
        String announcement;
        if (announcementTag != null && announcementTag.length() > 0) {
          announcement = String.format("%s[%s] %s%s", announcementTagColor, announcementTag,
                  announcementMessageColor, message);
        } else {
          announcement = String.format("%s %s%s", announcementTagColor, announcementMessageColor, message);
        }

        String messageToSend = ChatColorHelper.replaceColorCodes(announcement);
        if (sendToAll) {
          getServer().broadcastMessage(messageToSend);
        } else {
          for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission(AnnouncerPermissions.RECEIVER)) {
              player.sendMessage(messageToSend);
            }
          }
        }
      }
    }
  }

  /**
   * Saves the announcements.
   */
  public void saveConfiguration() {
    getConfig().set("announcement.messages", announcementMessages);
    getConfig().set("announcement.interval", announcementInterval);
    getConfig().set("announcement.broadcast-color", announcementMessageColor.name());
    getConfig().set("announcement.broadcast-tag", announcementTag);
    getConfig().set("announcement.broadcast-tag-color", announcementTagColor.name());
    getConfig().set("announcement.enabled", enabled);
    getConfig().set("announcement.random", random);
    getConfig().set("announcement.sendToAll", sendToAll);
    saveConfig();
  }

  /**
   * Reloads the configuration.
   */
  public void reloadConfiguration() {
    final List<String> defaultAnnouncementMessages = new LinkedList<String>();
    defaultAnnouncementMessages.add("This is the first default announcement!");
    defaultAnnouncementMessages.add("Use /announce help to get info how to config this plugin.");
    defaultAnnouncementMessages.add("You can also configure this plugin with its 'config.yml' too!");

    reloadConfig();
    announcementTag = getConfig().getString("announcement.broadcast-tag", "Announcement");
    announcementMessageColor =
            ChatColor.valueOf(getConfig().getString("announcement.broadcast-color", "LIGHT_PURPLE"));
    announcementTagColor = ChatColor.valueOf(getConfig().getString("announcement.broadcast-tag-color",
            announcementMessageColor.name()));
    announcementMessages = getConfig().getStringList("announcement.messages");
    announcementInterval = getConfig().getInt("announcement.interval", 1000);
    enabled = getConfig().getBoolean("announcement.enabled", true);
    random = getConfig().getBoolean("announcement.random", false);
    sendToAll = getConfig().getBoolean("announcement.sendToAll", true);
  }

  /**
   * @return the announcement period.
   */
  public long getAnnouncementInterval() {
    return announcementInterval;
  }

  /**
   * Sets the announcement period.
   *
   * @param announcementInterval the period to set.
   */
  public void setAnnouncementInterval(long announcementInterval) {
    this.announcementInterval = announcementInterval;
    saveConfiguration();

    // Register the schedule
    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.cancelTasks(this);
    scheduler
            .scheduleSyncRepeatingTask(this, announcerThread, announcementInterval * 10, announcementInterval * 10);
  }

  /**
   * Adds a new announcement.
   *
   * @param message the message to announce.
   */
  public void addAnnouncement(String message) {
    announcementMessages.add(message);
    saveConfiguration();
  }

  /**
   * Returns the Announcement with the passed index.
   *
   * @param index 1 based index, like in /announce list.
   * @return the announcement string.
   */
  public String getAnnouncement(int index) {
    return announcementMessages.get(index - 1);
  }

  /**
   * @return the number of announcements.
   */
  public int numberOfAnnouncements() {
    return announcementMessages.size();
  }

  /**
   * Removes all announcements.
   */
  public void removeAnnouncements() {
    announcementMessages.clear();
    saveConfiguration();
  }

  /**
   * Removes the announcement with the passed index.
   *
   * @param index the index which selects the announcement to remove.
   */
  public void removeAnnouncement(int index) {
    announcementMessages.remove(index - 1);
    saveConfiguration();
  }

  /**
   * @return the logger used by this plugin.
   */
  public Logger getLogger() {
    return logger;
  }

  public boolean isAnnouncerEnabled() {
    return enabled;
  }

  public void setAnnouncerEnabled(boolean enabled) {
    this.enabled = enabled;
    saveConfiguration();
  }

  public boolean isRandom() {
    return random;
  }

  public void setRandom(boolean random) {
    this.random = random;
    saveConfiguration();
  }
}

