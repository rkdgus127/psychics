// Pommand.java (명령어 처리)
package Psychic.Command.Executer;

import Psychic.Core.AbilityClass.AbilityConcept;
import Psychic.Core.Manager.AbilityManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class Pommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        switch (args[0].toLowerCase()) {

            case "remove":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /psy remove <플레이어>");
                    return true;
                }
                Player targetRemove = Bukkit.getPlayerExact(args[1]);
                if (targetRemove == null) {
                    sender.sendMessage("§c플레이어를 찾을 수 없습니다.");
                    return true;
                }

                AbilityConcept currentAbilityConcept = AbilityManager.getAbility(targetRemove.getUniqueId());
                if (currentAbilityConcept != null) {
                    currentAbilityConcept.remove(targetRemove);
                    if (currentAbilityConcept instanceof Listener) {
                        HandlerList.unregisterAll((Listener) currentAbilityConcept);
                    }
                    AbilityManager.removeAbility(targetRemove.getUniqueId());
                    sender.sendMessage("§a" + targetRemove.getName() + "의 능력을 제거했습니다.");
                } else {
                    sender.sendMessage("§e이 플레이어는 능력이 없습니다.");
                }
                return true;

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

                    if (!(abilityObj instanceof AbilityConcept)) {
                        sender.sendMessage("§c이 클래스는 Ability 인터페이스를 구현하지 않았습니다.");
                        return true;
                    }

                    AbilityConcept abilityConcept = (AbilityConcept) abilityObj;
                    abilityConcept.apply(target);

                    AbilityManager.setAbility(target.getUniqueId(), abilityName, abilityConcept);
                    sender.sendMessage("§a" + target.getName() + "에게 능력 '" + abilityName + "' 부여됨.");
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("§c능력 적용 중 오류 발생: " + e.getClass().getSimpleName());
                }

                return true;
            }
            case "info":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /psy info <플레이어>");
                    return true;
                }
                Player targetInfo = Bukkit.getPlayerExact(args[1]);
                if (targetInfo == null) {
                    sender.sendMessage("§c플레이어를 찾을 수 없습니다.");
                    return true;
                }

                AbilityConcept abilityConcept = AbilityManager.getAbility(targetInfo.getUniqueId());
                if (abilityConcept != null) {
                    sender.sendMessage("§a" + targetInfo.getName() + "의 능력: " + abilityConcept.getClass().getSimpleName());
                } else {
                    sender.sendMessage("§e이 플레이어는 능력이 없습니다.");
                }
                return true;

            default:
                sender.sendMessage("§c알 수 없는 하위 명령입니다.");
                return false;
        }
    }
}