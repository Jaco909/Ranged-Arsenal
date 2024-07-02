package rangedarsenal.events;

import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystallizeShatterEvent;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.Particle.GType;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.CrystalBulletProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;

public class CrystalSplosionEvent extends MobAbilityLevelEvent {
    protected CrystalSplosionEvent.ParticleType type;
    protected Mob source;
    protected Mob owner;

    public CrystalSplosionEvent(){
    }
    public CrystalSplosionEvent(Mob owner, Mob source) {
        super(owner, new GameRandom());
        this.type = ParticleType.SAPPHIRE;
        this.owner = owner;
        this.source = source;
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.type = (CrystalSplosionEvent.ParticleType) reader.getNextEnum(CrystalSplosionEvent.ParticleType.class);
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextEnum(this.type);
    }

    public void init() {
        super.init();
        if (this.isClient() && this.owner != null) {
            this.shatterCrystallizeBuff();
        }

        this.over();
    }

    public void shatterCrystallizeBuff() {
        Screen.playSound(GameResources.shatter2, SoundEffect.effect(this.owner).volume(2.0F).pitch(1.0F));
        this.spawnShatterParticles();
    }

    private void spawnShatterParticles() {
        int particleCount = 25;
        GameRandom random = GameRandom.globalRandom;
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{GType.CRITICAL, GType.IMPORTANT_COSMETIC, GType.COSMETIC});
        float anglePerParticle = 360.0F / (float)particleCount;
        GameTextureSection textureSection = (GameTextureSection)this.type.textureGetter.get();

        for(int i = 0; i < particleCount; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians((double)angle)) * 50.0F;
            float dy = (float)Math.cos(Math.toRadians((double)angle)) * 50.0F;
            this.owner.getLevel().entityManager.addParticle(this.owner, typeSwitcher.next()).sprite(textureSection.sprite(random.nextInt(4), 0, 18, 24)).sizeFades(22, 44).movesFriction(dx * random.getFloatBetween(1.0F, 2.0F), dy * random.getFloatBetween(1.0F, 2.0F), 0.8F).heightMoves(0.0F, -30.0F).lifeTime(500);
        }

    }

    public static enum ParticleType {
        AMETHYST(() -> {
            return GameResources.amethystShardParticles;
        }),
        SAPPHIRE(() -> {
            return GameResources.sapphireShardParticles;
        }),
        EMERALD(() -> {
            return GameResources.emeraldShardParticles;
        }),
        RUBY(() -> {
            return GameResources.rubyShardParticles;
        });

        public Supplier<GameTextureSection> textureGetter;

        private ParticleType(Supplier textureGetter) {
            this.textureGetter = textureGetter;
        }
    }
}