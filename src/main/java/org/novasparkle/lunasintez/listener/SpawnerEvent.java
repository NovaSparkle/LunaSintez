package org.novasparkle.lunasintez.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.novasparkle.lunasintez.sintez.SintezSpawner;
import org.novasparkle.lunaspring.API.commands.annotations.LunaHandler;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;

@LunaHandler
public class SpawnerEvent implements Listener {
    private final CooldownPrevent<Player> eventCooldownPrevent;

    public SpawnerEvent() {
        this.eventCooldownPrevent = new CooldownPrevent<>();
        this.eventCooldownPrevent.setCooldownMS(100);
    }

    @EventHandler
    private void onBlockClick(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && block.getType().equals(Material.SPAWNER) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (!eventCooldownPrevent.isCancelled(event, event.getPlayer())) {
                SintezSpawner spawner = new SintezSpawner(block.getLocation(), event.getPlayer());
                spawner.onBlockClick(event);
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.SPAWNER))
            new SintezSpawner(block.getLocation(), event.getPlayer()).onBreakSpawner(event);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType().equals(Material.SPAWNER))
            new SintezSpawner(block.getLocation(), event.getPlayer()).onPlaceSpawner(event);
    }
}
