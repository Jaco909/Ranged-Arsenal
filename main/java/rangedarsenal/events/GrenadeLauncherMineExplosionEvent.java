package rangedarsenal.events;

import necesse.engine.sound.SoundManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;

public class GrenadeLauncherMineExplosionEvent extends ExplosionEvent implements Attacker {
    public GrenadeLauncherMineExplosionEvent() {
        this(0.0F, 0.0F, new GameDamage(150.0F,0F), (Mob)null);
    }
    public GrenadeLauncherMineExplosionEvent(float x, float y, GameDamage damage, Mob owner) {
        super(x, y, 175, damage, true, 10, owner);
        getTotalMobDamage(0.1f);
        getTotalObjectDamage(20f);
        this.sendCustomData = true;
        this.sendOwnerData = true;
    }
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(2.0F).pitch(1.3F));
        this.level.getClient().startCameraShake(this.x, this.y, 400, 50, 3.0F, 3.0F,true);
    }
}
