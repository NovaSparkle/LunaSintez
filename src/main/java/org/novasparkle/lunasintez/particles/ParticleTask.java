package org.novasparkle.lunasintez.particles;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;

import java.util.Objects;

@RequiredArgsConstructor
public class ParticleTask extends BukkitRunnable {
    private final Location location;
    private final ConfigurationSection section;
    @Override
    @SneakyThrows
    public void run() {
        Location blockCenter = this.location.clone().add(0.5, 0, 0.5);
        double radius = section.getDouble("radius");
        double y = blockCenter.getY();

        String[] split = Objects.requireNonNull(section.getString("color")).split(", ");
        Color color = Color.fromBGR(LunaMath.toInt(split[0]), LunaMath.toInt(split[1]), LunaMath.toInt(split[2]));

        int msTime = this.section.getInt("delay");
        int amount = this.section.getInt("amount");
        double additiveHeight = this.section.getDouble("addHeight");
        while (y < this.location.getBlockY() + 1) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(color, 0.85F);
            ParticleLine particleLine = new ParticleLine(radius, dustOptions);
            y += additiveHeight;
            particleLine.spawn(blockCenter.toVector(), this.location.getWorld(), y, amount);
            Thread.sleep(msTime);
        }
    }
}
