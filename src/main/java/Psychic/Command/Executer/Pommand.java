package Psychic.Command.Executer;

import Psychic.Core.AbilityClass.AbilityConcept;
import Psychic.Core.Manager.AbilityManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Pommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        switch (args[0].toLowerCase()) {

            case "remove": {
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /psy remove <플레이어>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage("§c플레이어를 찾을 수 없습니다.");
                    return true;
                }

                AbilityManager.clearAllAbilities(target.getUniqueId());
                sender.sendMessage("§a" + target.getName() + "의 모든 능력을 제거했습니다.");
                return true;
            }

            case "attach": {
                if (args.length < 3) {
                    sender.sendMessage("§c사용법: /psy attach <능력이름> <플레이어>");
                    return true;
                }

                String abilityName = args[1];
                Player target = Bukkit.getPlayerExact(args[2]);

                if (target == null) {
                    sender.sendMessage("§c플레이어를 찾을 수 없습니다.");
                    return true;
                }

                try {
                    String className = "Psychic.Ability." + abilityName;
                    Class<?> clazz = Class.forName(className);
                    Object abilityObj = clazz.getDeclaredConstructor().newInstance();

                    if (!(abilityObj instanceof AbilityConcept abilityConcept)) {
                        sender.sendMessage("§c이 클래스는 AbilityConcept 인터페이스를 구현하지 않았습니다.");
                        return true;
                    }

                    AbilityManager.addAbility(target.getUniqueId(), abilityConcept);
                    sender.sendMessage("§a" + target.getName() + "에게 능력 '" + abilityName + "' 부여됨.");
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("§c능력 적용 중 오류 발생: " + e.getClass().getSimpleName());
                }

                return true;
            }

            case "info": {
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /psy info <능력이름>");
                    return true;
                }

                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
                    return true;
                }

                String abilityNameForInfo = args[1];

                try {
                    String className = "Psychic.Ability." + abilityNameForInfo + "$Info";
                    Class<?> infoClass = Class.forName(className);
                    Object infoInstance = infoClass.getDeclaredConstructor().newInstance();

                    if (!(infoInstance instanceof Psychic.Core.AbilityClass.AbilityInfo info)) {
                        player.sendMessage("§c" + abilityNameForInfo + " 능력은 정보 GUI를 제공하지 않습니다.");
                        return true;
                    }

                    info.openInfoInventory(player);

                } catch (ClassNotFoundException e) {
                    player.sendMessage("§c해당 능력의 정보를 찾을 수 없습니다: " + abilityNameForInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("§c정보 GUI를 여는 중 오류 발생: " + e.getClass().getSimpleName());
                }
                return true;
            }
            case "about": {
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /psy about <플레이어>");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage("§c플레이어를 찾을 수 없습니다.");
                    return true;
                }

                var abilities = AbilityManager.getAbilities(target.getUniqueId());

                if (abilities.isEmpty()) {
                    sender.sendMessage("§e" + target.getName() + "는 현재 능력을 가지고 있지 않습니다.");
                    return true;
                }

                sender.sendMessage("§a" + target.getName() + "의 능력 목록:");
                for (AbilityConcept ability : abilities) {
                    sender.sendMessage(" §7- §f" + ability.getClass().getSimpleName());
                }

                return true;
            }


            default:
                sender.sendMessage("§c알 수 없는 하위 명령입니다.");
                return false;
        }
    }
}
