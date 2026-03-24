package rkdgus.core.command;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import rkdgus.ability.Ability;
import rkdgus.core.psychics;

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

                Player target = Bukkit.getPlayer(args[1]);
                Ability ability = plugin.getAbilityRegistry().create(args[2]);

                if (target == null) {
                    sender.sendMessage("플레이어 없음");
                    return true;
                }

                if (ability == null) {
                    sender.sendMessage("능력 없음");
                    return true;
                }

                plugin.getAbilityManager().add(target, ability);
                sender.sendMessage("부여 완료");
            }

            case "detach" -> {
                if (args.length < 3) return false;

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    sender.sendMessage("플레이어 없음");
                    return true;
                }

                boolean removed = plugin.getAbilityManager().remove(target, args[2]);

                if (!removed) {
                    sender.sendMessage("그 능력 없음");
                    return true;
                }

                sender.sendMessage("제거 완료");
            }

            case "info" -> {
                sender.sendMessage("TODO");
            }
            case "reload" -> {

                plugin.getConfigManager().resetAll();

                // 🔥 모든 능력 config 강제 생성
                for (String name : plugin.getAbilityRegistry().getNames()) {
                    Ability ability = plugin.getAbilityRegistry().create(name);
                    plugin.getConfigManager().createDefault(name, ability);
                }

                plugin.getAbilityManager().reloadAll();

                sender.sendMessage("콘피그 초기화 완료");
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
            list.add("reload");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("attach") || args[0].equalsIgnoreCase("detach") || args[0].equalsIgnoreCase("info")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("attach") || args[0].equalsIgnoreCase("detach")) {
                list.addAll(plugin.getAbilityRegistry().getNames());
            }
        }

        return list;
    }
}