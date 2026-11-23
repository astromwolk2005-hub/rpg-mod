package com.beyondfivehorizons.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class HorizonSporeParticle extends SpriteBillboardParticle {
    protected HorizonSporeParticle(ClientWorld world, double x, double y, double z,
                                   double vx, double vy, double vz, SpriteProvider sprites) {
        super(world, x, y, z, vx, vy, vz);
        this.velocityX = vx;
        this.velocityY = vy;
        this.velocityZ = vz;
        this.scale = 0.14f + world.random.nextFloat() * 0.12f;
        this.maxAge = 35 + world.random.nextInt(25);
        this.setSpriteForAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.velocityX *= 0.96;
        this.velocityZ *= 0.96;
        this.alpha = Math.max(0.0f, 1.0f - ((float) this.age / (float) this.maxAge));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getBrightness(float tint) {
        return 0x00F000F0; // Fullbright glow
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world,
                                       double x, double y, double z, double vx, double vy, double vz) {
            return new HorizonSporeParticle(world, x, y, z, vx, vy, vz, sprites);
        }
    }
}
