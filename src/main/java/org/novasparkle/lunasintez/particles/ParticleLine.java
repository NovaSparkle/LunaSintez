package org.novasparkle.lunasintez.particles;

import lombok.RequiredArgsConstructor;
import org.bukkit.Particle;
import org.bukkit.World;

import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class ParticleLine {
    private final double radius;
    private final Particle.DustOptions dustOptions;

    public void spawn(Vector centerVector, final World world, final double finalY, int amount) {
        double minX = centerVector.getX() - this.radius;
        double minZ = centerVector.getZ() - this.radius;
        double maxX = centerVector.getX() + this.radius;
        double maxZ = centerVector.getZ() + this.radius;

        Particle particle = Particle.REDSTONE;
        for (double x = minX; x <= maxX; x += 0.1) {
            world.spawnParticle(particle, x, finalY, minZ, amount, this.dustOptions);
            world.spawnParticle(particle, x, finalY, maxZ, amount, this.dustOptions);
        }

        for (double z = minZ; z <= maxZ; z += 0.1) {
            world.spawnParticle(particle, minX, finalY, z, amount, this.dustOptions);
            world.spawnParticle(particle, maxX, finalY, z, amount, this.dustOptions);
        }
    }
}
