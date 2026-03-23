package rkdgus.core.command;

import rkdgus.core.psychics;
import rkdgus.ability.Ability;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PsychicsCommand implements CommandExecutor, TabCompleter {

    private final psychics plugin;

    public PsychicsCommand(psychics plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length < 1) return false;

        switch (args[0].toLowerCase()) {

            case "attach" -> {
                if (args.length < 3) return false;

                Ability ability = plugin.getAbilityRegistry().create(args[1]);
                Player target = Bukkit.getPlayer(args[2]);

                if (ability == null) {
                    sender.sendMessage("능력 없음");
                    return true;
                }

                if (target == null) {
                    sender.sendMessage("플레이어 없음");
                    return true;
                }

                plugin.getAbilityManager().add(target, ability);
                sender.sendMessage("부여 완료");
            }

            case "detach" -> {
                if (args.length < 3) return false;

                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage("플레이어 없음");
                    return true;
                }

                plugin.getAbilityManager().remove(target, args[1]);
                sender.sendMessage("제거 완료");
            }

            case "info" -> {
                sender.sendMessage("TODO");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("attach");
            list.add("detach");
            list.add("info");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("attach") || args[0].equalsIgnoreCase("detach")) {
                list.addAll(plugin.getAbilityRegistry().getNames());
            } else if (args[0].equalsIgnoreCase("info")) {
                for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
            }
        }

        if (args.length == 3) {
            for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
        }

        return list;
    }
}