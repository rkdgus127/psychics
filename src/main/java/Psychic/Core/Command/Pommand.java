package Psychic.Core.Command;

import Psychic.Core.Abstract.AbilityInfo;
import Psychic.Core.InterFace.AbilityConcept;
import Psychic.Core.Manager.Ability.AbilityManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class Pommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        switch (args[0].toLowerCase()) {

            case "attach": {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /psy attach <Ability> <Player>");
                    return true;
                }

                String abilityName = args[1];
                Player target = Bukkit.getPlayerExact(args[2]);

                if (target == null) {
                    sender.sendMessage("§cPlayer is null");
                    return true;
                }

                try {
                    // 전체 클래스패스에서 해당 이름의 Ability 클래스 찾기
                    Reflections reflections = new Reflections(
                            new ConfigurationBuilder()
                                    .setUrls(ClasspathHelper.forClassLoader())
                    );

                    Class<?> foundClass = reflections.getSubTypesOf(AbilityConcept.class).stream()
                            .filter(clazz -> clazz.getSimpleName().equals(abilityName))
                            .findFirst()
                            .orElseThrow(() -> new ClassNotFoundException("I can't found: " + abilityName));

                    Object abilityObj = foundClass.getDeclaredConstructor().newInstance();

                    if (!(abilityObj instanceof AbilityConcept abilityConcept)) {
                        return true;
                    }

                    AbilityManager.addAbility(target.getUniqueId(), abilityConcept);
                } catch (ClassNotFoundException e) {
                    sender.sendMessage("§cWe can't found that.");
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("§cAbility attach error: " + e.getClass().getSimpleName());
                }

                return true;
            }

            case "info": {
                if (args.length < 2) {
                    sender.sendMessage("§cUsage /psy info <Ability>");
                    return true;
                }

                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cThis command for only player");
                    return true;
                }

                String abilityNameForInfo = args[1];

                try {
                    // 전체 클래스패스에서 해당 이름의 Info 클래스 찾기
                    Reflections reflections = new Reflections(
                            new ConfigurationBuilder()
                                    .setUrls(ClasspathHelper.forClassLoader())
                    );

                    Class<?> foundInfoClass = reflections.getSubTypesOf(AbilityInfo.class).stream()
                            .filter(clazz -> clazz.getSimpleName().equals(abilityNameForInfo + "$Info"))
                            .findFirst()
                            .orElseThrow(() -> new ClassNotFoundException("Can't found " + abilityNameForInfo));

                    Object infoInstance = foundInfoClass.getDeclaredConstructor().newInstance();

                    if (!(infoInstance instanceof AbilityInfo info)) {
                        player.sendMessage("§c" + abilityNameForInfo + " ability doesn't have Info");
                        return true;
                    }

                    info.openInfoInventory(player);

                } catch (ClassNotFoundException e) {
                    player.sendMessage("§cCan't Open: " + abilityNameForInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("§cWhat the fuck error: " + e.getClass().getSimpleName());
                }
                return true;
            }
            case "reload": {
                sender.sendMessage(ChatColor.GREEN + "Psy reload complete");
            }
            case "know": {
                if (args.length < 2) {
                    sender.sendMessage("§cUsage /psy know <player>");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage("§cCan't found the " + ChatColor.GREEN + target.getName());
                    return true;
                }

                var abilities = AbilityManager.getAbilities(target.getUniqueId());

                if (abilities.isEmpty()) {
                    sender.sendMessage(target.getName() + "doesn't have Any Ability");
                    return true;
                }

                sender.sendMessage("§a" + target.getName() + "'s Ability List :");
                for (AbilityConcept ability : abilities) {
                    sender.sendMessage(" §7- §f" + ability.getClass().getSimpleName());
                }

                return true;
            }

            default:
                sender.sendMessage("§cI dont know about that");
                return false;
        }
    }
}