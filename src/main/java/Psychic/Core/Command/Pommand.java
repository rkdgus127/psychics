package Psychic.Core.Command;

import Psychic.Core.AbilityConfig.Java.ConfigManager;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.PsychicInfo.AbilityInfo;
import Psychic.Core.InterFace.AbilityConcept;
import Psychic.Core.Main.Psychic;
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

import java.util.Set;

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
                            .filter(clazz -> clazz.isAnnotationPresent(Name.class) &&
                                    clazz.getAnnotation(Name.class).value().equals(abilityName))
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
                    AbilityInfo.openInfoInventory((Player) sender);
                    return true;
                }

                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cThis command for only player");
                    return true;
                }

                String input = args[1];
                String normalizedInput = input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

                try {
                    String basePackage = "Psychic.Ability.";
                    Reflections reflections = new Reflections(basePackage);
                    Set<Class<? extends Ability>> abilityClasses = reflections.getSubTypesOf(Ability.class);

                    Class<?> targetClass = null;

                    for (Class<?> cls : abilityClasses) {
                        String normalizedClassName = cls.getSimpleName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                        if (normalizedClassName.equals(normalizedInput)) {
                            targetClass = cls;
                            break;
                        }
                    }

                    if (targetClass == null) {
                        player.sendMessage("§cNot found Ability: " + input);
                        return true;
                    }

                    Class<?>[] declaredClasses = targetClass.getDeclaredClasses();
                    Class<?> infoClass = null;

                    for (Class<?> innerClass : declaredClasses) {
                        if (innerClass.getSimpleName().equals("AI")) {
                            infoClass = innerClass;
                            break;
                        }
                    }

                    if (infoClass == null) {
                        player.sendMessage("§c" + targetClass.getSimpleName() + " ability doesn't have Info class");
                        return true;
                    }

                    // Info 인스턴스 생성 및 인벤토리 열기
                    AbilityInfo info = (AbilityInfo) infoClass.getDeclaredConstructor().newInstance();
                    info.openInfoInventory(player);

                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("§cError occur: " + e.getClass().getSimpleName());
                    player.sendMessage("§c상세: " + e.getMessage());
                }
                return true;
            }


            case "remove": {
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /psy remove <Player>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer is null.");
                    return true;
                }

                AbilityManager.clearAllAbilities(target.getUniqueId());
                return true;
            }

            case "reload": {
                try {
                    // 플러그인 리로드
                    Psychic.getInstance().reloadConfig();

                    ConfigManager.reloadAllConfigs();

                    sender.sendMessage(ChatColor.GREEN + "Psychics have been reloaded successfully!");
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Error occurred while reloading: " + e.getMessage());
                    e.printStackTrace();
                }
                return true;
            }


            default:
                sender.sendMessage("§cI dont know about that");
                return false;

        }
    }
}