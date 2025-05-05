package Psychic.Core.Command;

import Psychic.Core.Abstract.AbilityInfo;
import Psychic.Core.InterFace.AbilityConcept;
import Psychic.Core.Manager.Ability.AbilityManager;
import org.bukkit.Bukkit;
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
            // remove와 know 케이스는 수정이 필요없으므로 그대로 유지...

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
                    // 전체 클래스패스에서 해당 이름의 Ability 클래스 찾기
                    Reflections reflections = new Reflections(
                            new ConfigurationBuilder()
                                    .setUrls(ClasspathHelper.forClassLoader())
                    );

                    Class<?> foundClass = reflections.getSubTypesOf(AbilityConcept.class).stream()
                            .filter(clazz -> clazz.getSimpleName().equals(abilityName))
                            .findFirst()
                            .orElseThrow(() -> new ClassNotFoundException("능력을 찾을 수 없습니다: " + abilityName));

                    Object abilityObj = foundClass.getDeclaredConstructor().newInstance();

                    if (!(abilityObj instanceof AbilityConcept abilityConcept)) {
                        sender.sendMessage("§c이 클래스는 AbilityConcept 인터페이스를 구현하지 않았습니다.");
                        return true;
                    }

                    AbilityManager.addAbility(target.getUniqueId(), abilityConcept);
                    sender.sendMessage("§a" + target.getName() + "에게 능력 '" + abilityName + "' 부여됨.");
                } catch (ClassNotFoundException e) {
                    sender.sendMessage("§c해당 이름의 능력을 찾을 수 없습니다.");
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
                    // 전체 클래스패스에서 해당 이름의 Info 클래스 찾기
                    Reflections reflections = new Reflections(
                            new ConfigurationBuilder()
                                    .setUrls(ClasspathHelper.forClassLoader())
                    );

                    Class<?> foundInfoClass = reflections.getSubTypesOf(AbilityInfo.class).stream()
                            .filter(clazz -> clazz.getSimpleName().equals(abilityNameForInfo + "$Info"))
                            .findFirst()
                            .orElseThrow(() -> new ClassNotFoundException("Info 클래스를 찾을 수 없습니다."));

                    Object infoInstance = foundInfoClass.getDeclaredConstructor().newInstance();

                    if (!(infoInstance instanceof AbilityInfo info)) {
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

            default:
                sender.sendMessage("§c알 수 없는 하위 명령입니다.");
                return false;
        }
    }
}