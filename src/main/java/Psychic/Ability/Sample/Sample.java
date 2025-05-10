package Psychic.Ability.Sample;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.AbilityDamage.AbilityDamage;
import Psychic.Core.AbilityEffect.AbilityFW;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.PsychicInfo.AbilityInfo;
import Psychic.Core.Abstract.PsychicInfo.Info;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.CoolDown.Cool;
import Psychic.Core.Manager.Mana.Mana;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

/*
개발자들을 위한 셈플 클래스
 */


//Name을 이용하여서 TabComplete 할때 쓰일 인수 선정
@Name("sample")
public class Sample extends Ability {

    //Config 어노테이션 아래에 선언된 변수들은 전부 Name.yml 속에 저장됌
    //또한 mana, Active, cool, duration, wand, description과 같은 특정 변수들은 전부 같은 이름이여야함
    @Config
    public static double mana = 10.0;

    @Config
    public static boolean Active = true;

    @Config
    public static int cool = 1;

    @Config
    public static int damage1 = 4;

    @Config
    public static int direction = 20;

    @Config
    public static Material wand = Material.STICK;
    /*
    Info 어노테이션은 psy info를 이용하여 새로운 인벤토리를 열때
    그 속에있는 아이템에 설정할 lore 들을 설정하는 것 입니다.
     */
    @Info
    public static String damage = ChatColor.GOLD + "데미지: " + damage1;

    @Info
    public static String directionInfo = ChatColor.RED + "거리: " + direction;


    //description은 꼭 Config 어노테이션에게 지배당해야함
    @Config
    public static String description = "지정된 완드을 우클릭하면 적에게 데미지를 줍니다.";
    /*
    꼭 필요한 내부 클래스
    없다면 Info가 안생김
     */
    public static class AI extends AbilityInfo {
        @Override
        public void setupItems() {
            autoSetupItems(Sample.class);
        }
    }

    /*
    능력 사용 예시 로직
    매우 간편하고 쉽게 작성됌
     */
    @EventHandler
    public void onAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        //능력이 있는지 확인 하는 코드
        if (!AbilityManager.hasAbility(player, Sample.class)) return;
        //쿨타임 체크 (자동으로 return 해줌)
        Cool.Check(player, wand);
        if (event.getAction().isRightClick()) {
            //기본적인 마인크래프트 플러그인 구문
            if (event.getItem() == null || event.getItem().getType() != wand) return;
            if (player.getTargetEntity(100) == null || !(player.getTargetEntity(100) instanceof LivingEntity)) return;
            LivingEntity target = (LivingEntity) player.getTargetEntity(100);


            //AbilityDamage의 PsychicDamage 라는 변수를 통하여 플레이어의 방어력을 합쳐서 공격력 측정
            target.damage(AbilityDamage.PsychicDamage(player, damage1), player);


            //AbilityFw 라는 클래스를 통하여 쉽게 폭죽 이펙트 활용 가능
            AbilityFW.FW(target, FireworkEffect.Type.STAR, Color.RED, 0);
            player.setCooldown(wand, (int) cool * 20);


            //디버깅용 메시지 출력
            player.sendMessage("" + AbilityDamage.PsychicDamage(player,damage1));

            //여기서도 또한 마나 감소시 자동 return 존재함
            Mana.consume(player, mana);
        }
    }
}
