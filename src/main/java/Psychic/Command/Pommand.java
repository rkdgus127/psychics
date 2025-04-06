package Psychic.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Psychic.Core.Psychic;

public class Pommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        switch (args[0].toLowerCase()) {
            case "reload":
                Psychic.getInstance().reloadConfig();
                sender.sendMessage("§a[Psychic] 리로드 완료.");
                return true;

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
                // 실제 능력 제거 로직은 나중에 구현
                sender.sendMessage("§a" + targetRemove.getName() + "의 능력을 제거했습니다.");
                return true;

            case "attach":
                if (args.length < 3) {
                    sender.sendMessage("§c사용법: /psy attach <능력이름> <플레이어>");
                    return true;
                }
                String ability = args[1];
                Player targetAttach = Bukkit.getPlayerExact(args[2]);
                if (targetAttach == null) {
                    sender.sendMessage("§c플레이어를 찾을 수 없습니다.");
                    return true;
                }
                // 실제 능력 부여 로직은 나중에 구현
                sender.sendMessage("§a" + targetAttach.getName() + "에게 능력 '" + ability + "'를 부여했습니다.");
                return true;

            default:
                sender.sendMessage("§c알 수 없는 하위 명령입니다.");
                return false;
        }
    }
}