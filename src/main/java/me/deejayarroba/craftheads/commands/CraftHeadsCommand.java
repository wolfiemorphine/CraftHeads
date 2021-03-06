package me.deejayarroba.craftheads.commands;

import me.deejayarroba.craftheads.Main;
import me.deejayarroba.craftheads.menu.MenuManager;
import me.deejayarroba.craftheads.skulls.Skulls;
import me.deejayarroba.craftheads.util.AbstractCommand;
import me.deejayarroba.craftheads.util.Items;
import me.deejayarroba.craftheads.util.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftHeadsCommand extends AbstractCommand {

    MessageManager msg = MessageManager.getInstance();

    public CraftHeadsCommand(String command, String usage, String description) {
        super(command, usage, description);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("craftheads")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

                if (!sender.hasPermission("craftheads.use")) {
                    msg.bad(p, "You don't have permission to use this command.");
                    return false;
                }

                if (args.length > 0) {

                    // Here the command would be: /craftheads <playername>

                    // TODO: implement buying other people's heads

                    float otherHeadPrice = Main.instance.getConfig().getInt("player-other-head-price");

                    if (Main.economy != null) {
                        double balance = Main.economy.getBalance(p);
                        if (balance < otherHeadPrice && otherHeadPrice > 0) {
                            msg.bad(p, "You can't your afford this player's head!");
                            return true;
                        }
                    }

                    // Check if the inventory is full
                    if (p.getInventory().firstEmpty() == -1) {
                        msg.bad(p, "Your inventory is full!");
                        return true;
                    } else {
                        String playerName = args[0];
                        ItemStack head = Items.editor(Skulls.getPlayerSkull(playerName))
                                .setName(ChatColor.GOLD + "Head: " + ChatColor.AQUA + args[0])
                                .build();

                        if (Main.economy != null && otherHeadPrice > 0) {
                            Main.economy.withdrawPlayer(p, otherHeadPrice);
                            msg.good(p, "You bought " + ChatColor.AQUA + playerName + ChatColor.GREEN + "'s head for " + ChatColor.AQUA + otherHeadPrice);
                        }

                        p.getInventory().addItem(head);
                        msg.good(p, "You now have " + args[0] + "'s head!");
                        return true;
                    }
                } else {
                    // Open the menu

                    Inventory inv = MenuManager.mainMenu.getInventory();
                    p.openInventory(inv);

                    return true;
                }
            } else {
                sender.sendMessage("You can only run this command as a player.");
                return true;
            }
        }
        return false;
    }

}
