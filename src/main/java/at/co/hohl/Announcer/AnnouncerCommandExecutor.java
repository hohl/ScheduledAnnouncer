/*
 * Copyright (C) 2011-2012 Mi.Ho.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package at.co.hohl.Announcer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command Executor used by the AnnouncerPlugin.
 *
 * @author MiHo
 */
class AnnouncerCommandExecutor implements CommandExecutor {
    /**
     * Defines the numbers of entries per page at list command.
     */
    private static final int ENTRIES_PER_PAGE = 7;

    /**
     * The AnnouncerPlugin plugin, which holds this CommandExecutor.
     */
    private final AnnouncerPlugin plugin;

    /**
     * Allocates a new AnnouncerCommandExecutor.
     *
     * @param plugin the plugin, which holds this command.
     */
    AnnouncerCommandExecutor(AnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called, when command is called.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean success;

        if (args.length == 0 || args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("info")) {
            success = onVersionCommand(sender, command, label, args);
        } else if ("help".equalsIgnoreCase(args[0])) {
            success = onHelpCommand(sender, command, label, args);
        } else if ("add".equalsIgnoreCase(args[0])) {
            success = onAddCommand(sender, command, label, args);
        } else if ("broadcast".equalsIgnoreCase(args[0]) || "now".equalsIgnoreCase(args[0])) {
            success = onBroadcastCommand(sender, command, label, args);
        } else if ("list".equalsIgnoreCase(args[0])) {
            success = onListCommand(sender, command, label, args);
        } else if ("delete".equalsIgnoreCase(args[0])) {
            success = onDeleteCommand(sender, command, label, args);
        } else if ("interval".equalsIgnoreCase(args[0])) {
            success = onIntervalCommand(sender, command, label, args);
        } else if ("prefix".equalsIgnoreCase(args[0])) {
            success = onPrefixCommand(sender, command, label, args);
        } else if ("random".equalsIgnoreCase(args[0])) {
            success = onRandomCommand(sender, command, label, args);
        } else if ("enable".equalsIgnoreCase(args[0])) {
            success = onEnableCommand(sender, command, label, args);
        } else if ("reload".equalsIgnoreCase(args[0])) {
            success = onReloadCommand(sender, command, label, args);
        } else {
            success = false;
        }

        if (!success) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments! " +
                "Use '/announce help' to get a list of valid commands.");
        }

        return true;
    }

    /**
     * Called when user uses the /announce version command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onVersionCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(
            String.format("%s === %s [Version %s] === ", ChatColor.LIGHT_PURPLE, plugin.getDescription().getName(),
                plugin.getDescription().getVersion()));
        sender.sendMessage(String.format("Author: %s", plugin.getDescription().getAuthors().get(0)));
        sender.sendMessage(String.format("Website: %s", plugin.getDescription().getWebsite()));
        sender.sendMessage(String.format("Version: %s", plugin.getDescription().getVersion()));
        sender.sendMessage("Features:");
        sender.sendMessage("- InGame Configuration");
        sender.sendMessage("- Permissions Support");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "Use '/announce help' to get a list of valid commands.");

        return true;
    }

    /**
     * Called when user uses the /announce help command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onHelpCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(String.format("%s === %s [Version %s] === ", ChatColor.LIGHT_PURPLE,
            plugin.getDescription().getName(), plugin.getDescription().getVersion()));
        if (sender.hasPermission(AnnouncerPermissions.ADD)) {
            sender.sendMessage(ChatColor.GRAY + "/announce add <message>" + ChatColor.WHITE +
                " - Adds a new announcement");
        }
        if (sender.hasPermission(AnnouncerPermissions.BROADCAST)) {
            sender.sendMessage(ChatColor.GRAY + "/announce broadcast [<index>]" + ChatColor.WHITE +
                " - Broadcast an announcement NOW");
        }
        if (sender.hasPermission(AnnouncerPermissions.DELETE)) {
            sender.sendMessage(ChatColor.GRAY + "/announce delete <index>" + ChatColor.WHITE +
                " - Removes the announcement with the passed index");
        }
        if (sender.hasPermission(AnnouncerPermissions.MODERATOR)) {
            sender.sendMessage(ChatColor.GRAY + "/announce enable [true|false]" + ChatColor.WHITE +
                " - Enables or disables the announcer.");
            sender.sendMessage(ChatColor.GRAY + "/announce interval <seconds>" + ChatColor.WHITE +
                " - Sets the seconds between the announcements.");
            sender.sendMessage(ChatColor.GRAY + "/announce prefix <message>" + ChatColor.WHITE +
                " - Sets the prefix for all announcements.");
            sender.sendMessage(ChatColor.GRAY + "/announce list" + ChatColor.WHITE + " - Lists all announcements");
            sender.sendMessage(ChatColor.GRAY + "/announce random [true|false]" + ChatColor.WHITE +
                " - Enables or disables the random announcing mode.");
        }
        if (sender.hasPermission(AnnouncerPermissions.ADMINISTRATOR)) {
            sender.sendMessage(ChatColor.GRAY + "/announce reload" + ChatColor.WHITE + " - Reloads the config.yml");
        }

        return true;
    }

    /**
     * Called when user uses the /announce add command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onAddCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(AnnouncerPermissions.ADD)) {
            if (args.length > 1) {
                StringBuilder messageToAnnounce = new StringBuilder();
                for (int index = 1; index < args.length; ++index) {
                    messageToAnnounce.append(args[index]);
                    messageToAnnounce.append(" ");
                }
                plugin.addAnnouncement(messageToAnnounce.toString());

                sender.sendMessage(ChatColor.GREEN + "Added announcement successfully!");
            } else {
                sender.sendMessage(ChatColor.RED + "You need to pass a message to announce!");
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Called when user uses the /announce broadcast command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onBroadcastCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(AnnouncerPermissions.BROADCAST)) {
            if (args.length == 2) {
                try {
                    int index = Integer.parseInt(args[1]);

                    if (index > 0 && index <= plugin.numberOfAnnouncements()) {
                        plugin.announce(index);
                    } else {
                        sender.sendMessage(
                            ChatColor.RED + "There isn't any announcement with the passed index!");
                        sender.sendMessage(
                            ChatColor.RED + "Use '/announce list' to view all available announcements.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Index must be a integer!");
                }
            } else if (args.length == 1) {
                plugin.announce();
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid number of arguments! Use /announce help to view the help!");
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Called when user uses the /announce list command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onListCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(AnnouncerPermissions.MODERATOR)) {
            if (args.length == 1 || args.length == 2) {
                int page = 1;
                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid page number!");
                    }
                }
                sender.sendMessage(ChatColor.GREEN + String.format(" === Announcements [Page %d/%d] ===", page,
                    plugin.announcementMessages.size() / ENTRIES_PER_PAGE + 1));

                final int indexStart = Math.abs(page - 1) * ENTRIES_PER_PAGE;
                final int indexStop = Math.min(page * ENTRIES_PER_PAGE, plugin.announcementMessages.size());

                for (int index = indexStart + 1; index <= indexStop; ++index) {
                    sender.sendMessage(String.format("%d - %s", index, ChatColorHelper.replaceColorCodes(
                        plugin.getAnnouncement(index))));
                }
            } else {
                sender.sendMessage(
                    ChatColor.RED + "Invalid number of arguments! Use '/announce help' to view the help.");
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Called when user uses the /announce delete command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onDeleteCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(AnnouncerPermissions.DELETE)) {
            if (args.length == 2) {
                try {
                    int index = Integer.parseInt(args[1]);

                    if (index > 0 && index <= plugin.numberOfAnnouncements()) {
                        sender.sendMessage(String.format("%sRemoved announcement: '%s'", ChatColor.GREEN,
                            plugin.getAnnouncement(index)));
                        plugin.removeAnnouncement(index);
                    } else {
                        sender.sendMessage(
                            ChatColor.RED + "There isn't any announcement with the passed index!");
                        sender.sendMessage(
                            ChatColor.RED + "Use '/announce list' to view all available announcements.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Index must be a integer!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Too many arguments! Use '/announce help' to view the help.");
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Called when user uses the /announce interval command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onIntervalCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(AnnouncerPermissions.MODERATOR)) {
            if (args.length == 2) {
                try {
                    plugin.setAnnouncementInterval(Integer.parseInt(args[1]));

                    sender.sendMessage(
                        ChatColor.GREEN + "Set interval of scheduled announcements successfully!");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Interval must be a number!");
                } catch (ArithmeticException e) {
                    sender.sendMessage(ChatColor.RED + "Interval must be greater than 0!");
                }
            } else if (args.length == 1) {
                sender.sendMessage(String.format("%sPeriod duration is %d", ChatColor.LIGHT_PURPLE,
                    plugin.getAnnouncementInterval()));
            } else {
                sender.sendMessage(
                    ChatColor.RED + "Too many arguments! Use '/announce help' to view the help!");
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Called when user uses the /announce prefix command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onPrefixCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(AnnouncerPermissions.MODERATOR)) {
            if (args.length > 1) {
                StringBuilder prefixBuilder = new StringBuilder();
                for (int index = 1; index < args.length; ++index) {
                    prefixBuilder.append(args[index]);
                    prefixBuilder.append(" ");
                }
                plugin.setAnnouncementPrefix(prefixBuilder.toString());

                sender.sendMessage(ChatColor.GREEN + "Set prefix for all announcements successfully!");
            } else {
                sender.sendMessage(String.format("%sPrefix is %s", ChatColor.LIGHT_PURPLE,
                    ChatColorHelper.replaceColorCodes(plugin.getAnnouncementPrefix())));
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Called when user uses the /announce random command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onRandomCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(AnnouncerPermissions.MODERATOR)) {
            if (args.length == 2) {
                if ("true".equalsIgnoreCase(args[1])) {
                    plugin.setRandom(true);
                    sender.sendMessage(ChatColor.GREEN + "Random mode enabled!");
                } else if ("false".equalsIgnoreCase(args[1])) {
                    plugin.setRandom(false);
                    sender.sendMessage(ChatColor.GREEN + "Sequential mode enabled!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Use true or false to enable or disable! " +
                        "Use '/announce help' to view the help.");
                }
            } else if (args.length == 1) {
                if (plugin.isRandom()) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Random mode is enabled.");
                } else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Sequential mode is enabled.");
                }
            } else {
                sender.sendMessage(
                    ChatColor.RED + "Invalid number of arguments! Use '/announce help' to view the help.");
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Called when user uses the /announce enable command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onEnableCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(AnnouncerPermissions.MODERATOR)) {
            if (args.length == 2) {
                if ("true".equalsIgnoreCase(args[1])) {
                    plugin.setAnnouncerEnabled(true);
                    sender.sendMessage(ChatColor.GREEN + "Announcer enabled!");
                } else if ("false".equalsIgnoreCase(args[1])) {
                    plugin.setAnnouncerEnabled(false);
                    sender.sendMessage(ChatColor.GREEN + "Announcer disabled!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Use ture or false to enable or disable! " +
                        "Use '/announce help' to view the help.");
                }
            } else if (args.length == 1) {
                if (plugin.isRandom()) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Announcer is enabled.");
                } else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Announcer is disabled.");
                }
            } else {
                sender.sendMessage(
                    ChatColor.RED + "Invalid number of arguments! Use '/announce help' to view the help.");
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Called when user uses the /announce reload command.
     *
     * @param sender  the sender. (In most case a player.)
     * @param command the command send.
     * @param label   the label used for the command
     * @param args    the arguments.
     * @return true if a valid command, otherwise false
     */
    boolean onReloadCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(AnnouncerPermissions.MODERATOR)) {
            if (args.length == 1) {
                plugin.reloadConfiguration();
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Configuration reloaded.");
            } else {
                sender.sendMessage(ChatColor.RED + "Any arguments needed! Use '/announce help' to view the help.");
            }
            return true;
        } else {
            return false;
        }
    }
}
