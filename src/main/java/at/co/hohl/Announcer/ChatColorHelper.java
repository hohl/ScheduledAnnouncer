/*
 * Copyright (c) 2011, Michael Hohl
 *
 * All rights reserved.
 */

package at.co.hohl.Announcer;

import org.bukkit.ChatColor;

/**
 * Helper class for using color codes.
 *
 * @author Michael Hohl
 */
public class ChatColorHelper {
  /**
   * Replace the &x with the color code of color x.
   *
   * @param message the string to format
   * @return the string ready to output.
   */
  public static String replaceColorCodes(String message) {
    for (ChatColor color : ChatColor.values()) {
      message = message.replaceAll(String.format("&%x", color.getCode()), color.toString());
    }

    return message;
  }
}
