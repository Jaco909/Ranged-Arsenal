package rangedarsenal.events;

import jdk.internal.icu.impl.BMPSet;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

public class GrenadeLauncherFireExplosionEvent extends ExplosionEvent implements Attacker {

    public GrenadeLauncherFireExplosionEvent() {
        this(0F, 0F, new GameDamage(0.0F,0f), null);
        this.destroysTiles = false;
        this.targetRangeMod = 0.66F;
        this.range = 25;
        this.owner = -1;
        this.knockback = 10;
        this.hitsOwner = true;
        this.sendOwnerData = true;
        this.sendCustomData = true;
    }

    public GrenadeLauncherFireExplosionEvent(float x, float y, GameDamage damage, Mob owner) {
        super(x, y, 25, damage, false, 0, owner);
        this.x = x;
        this.y = y;
        this.sendCustomData = true;
        this.sendOwnerData = true;
    }

    public static void spawnExplosionParticles(Level level, float x, float y, int particles, float minRange, float maxRange, ExplosionSpawnFunction spawnFunction) {
        for(int i = 0; i <= particles; ++i) {
            float anglePerParticle = 360.0F / (float)particles;
            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
            float range = (GameRandom.globalRandom.getFloatBetween(minRange, maxRange))/4;
            float dx = (float)Math.sin(Math.toRadians((double)angle));
            float dy = (float)Math.cos(Math.toRadians((double)angle));
            spawnFunction.spawn(level, x + dx * range, y + dy * range, dx * 20.0F, dy * 20.0F, 400, range);
        }

    }

    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(0.7F).pitch(1.5F));
        SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(this.x, this.y).volume(2.0F).pitch(1.25F));
        this.level.getClient().startCameraShake(this.x, this.y, 400, 50, 2F, 2F,true);
    }
}
