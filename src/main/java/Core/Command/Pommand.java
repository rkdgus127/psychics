package Core.Command;

import Core.AbilityConcept;
import Core.AbilityConfig.Java.ConfigManager;
import Core.AbilityConfig.Java.Name;
import Core.AbilityDamage.PsychicsTag;
import Core.Abstract.Ability;
import Core.Abstract.PsychicInfo.AbilityInfo;
import Core.Manager.Ability.AbilityManager;
import Core.Psychic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.util.Set;

public class Pommand implements CommandExecutor {


    /*
    PSYCHICS의 모든 명령어 제어 클래스
    이 클래스는 AI와 제가 50 : 50 비율로 작성 하였습니다.
     */

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
                    Reflections reflections = new Reflections("Ability");

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

            case "enchant": {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "이 명령어는 플레이어만 사용할 수 있습니다.");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "§cUsage: /psy enchant <level>");
                    return true;
                }

                int level;
                try {
                    level = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "§c레벨은 숫자여야 합니다.");
                    return true;
                }

                if (level < 1 || level > 5) {
                    sender.sendMessage(ChatColor.RED + "§c레벨은 1에서 5 사이여야 합니다.");
                    return true;
                }

                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || item.getType() == Material.AIR) {
                    sender.sendMessage(ChatColor.RED + "§c손에 아이템을 들고 있어야 합니다.");
                    return true;
                }

                // 방어구만 허용
                if (!item.getType().toString().endsWith("_HELMET") &&
                        !item.getType().toString().endsWith("_CHESTPLATE") &&
                        !item.getType().toString().endsWith("_LEGGINGS") &&
                        !item.getType().toString().endsWith("_BOOTS")) {
                    sender.sendMessage(ChatColor.RED + "§c초월 인첸트는 방어구에만 적용 가능합니다.");
                    return true;
                }

                PsychicsTag.addTag(item, level);
                sender.sendMessage(ChatColor.GREEN + "§a아이템에 초월 인첸트 레벨 " + level + "이(가) 부여되었습니다.");
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