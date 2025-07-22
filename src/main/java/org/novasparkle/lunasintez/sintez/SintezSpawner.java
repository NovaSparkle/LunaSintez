package org.novasparkle.lunasintez.sintez;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.menus.mainMenu.SintezMainMenu;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunasintez.sintez.mobs.status.MobStatus;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.satellite.dev.progiple.satespawnerapi.api.ASpawner;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SintezSpawner extends ASpawner {
    public static final String nbtEntity = "LunaSintezEntity";
    @Getter
    private final List<SintezMob> spawnerMobs;
    @Getter
    private final CreatureSpawner worldSpawner;
    @Getter
    private final Configuration config;
    private final Player player;
    public SintezSpawner(Location location, Player player) {
        super(location, LunaSintez.getInstance().getLunaSintezComponent());
        this.worldSpawner = (CreatureSpawner) location.getBlock().getState();
        this.player = player;
        this.spawnerMobs = new ArrayList<>();

        File file = new File(String.format("%s/spawners/%s_%d_%d_%d.yml",
                LunaSintez.getInstance().getDataFolder().getPath(),
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()));

        this.config = new Configuration(file);

        this.getComponent().register(this);
    }

    public static ItemStack getSpawnerItem(String mobType) {
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        NBTManager.setString(spawner, nbtEntity, mobType);
        return spawner;
    }

    public void onDrop() {
        ItemStack dropItem = getSpawnerItem(this.worldSpawner.getSpawnedType().name());
        this.getLocation().getBlock().setType(Material.AIR);
        this.getLocation().getWorld().dropItemNaturally(this.getLocation(), dropItem);
        this.deleteFile();
    }

    public void setMob(EntityType entityType) {
        this.worldSpawner.setSpawnedType(entityType);
        this.worldSpawner.update();
        this.config.setString("currentMob", this.worldSpawner.getSpawnedType().name());
        this.saveConfig();
    }
    private void deleteFile() {
        if (this.config.getFile().exists())
            if (!this.config.getFile().delete())
                throw new RuntimeException(String.format("Не удалось удалить файл конфигурации %s", this.config.getFile().getName()));
    }

    @Override
    public void onPlaceSpawner(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (NBTManager.hasTag(item, nbtEntity)) this.setMob(EntityType.valueOf(NBTManager.getString(item, nbtEntity)));
    }

    @Override
    public void onBreakSpawner(BlockBreakEvent e) {
        this.deleteFile();
    }

    @Override
    public void onBlockClick(PlayerInteractEvent event) {

        ItemStack itemStack = event.getItem();
        if (itemStack != null && itemStack.getType().name().endsWith("_SPAWN_EGG")) {
            EntityType entityType = EntityType.valueOf(itemStack.getType().name().replace("_SPAWN_EGG", ""));
            SintezMob mobToOpen = spawnerMobs.stream().filter(mob -> mob.getEntityType().equals(entityType)).findFirst().orElse(null);
            if (mobToOpen != null) {
                mobToOpen.setApplicator(MobStatus.SINTEZ);
                ConfigManager.send(event.getPlayer(), "alreadySintezied", "newType-%-" + mobToOpen.getEntityType().name());
            }
        }

        if (this.player.isSneaking()) {
            this.loadMobList();
            event.setCancelled(true);
            MenuManager.openInventory(this.player, new SintezMainMenu(this.player, this));
        }
    }

    private void loadMobList() {
        List<String> completedList = this.config.getStringList("completed");
        EntityType defaultEntity = this.worldSpawner.getSpawnedType();
        if (completedList.isEmpty()) {
            this.generateMobs(defaultEntity);

        } else {
            String current = config.getString("currentMob");
            completedList.forEach(e -> this.spawnerMobs.add(new SintezMob(EntityType.valueOf(e), this, MobStatus.SINTEZ)));
            this.config.getSection("mobs").getValues(false).forEach((key, value) -> {
                ConfigurationSection mobSection = (ConfigurationSection) value;
                SintezMob sintezMob = new SintezMob(EntityType.valueOf(mobSection.getName()), this, MobStatus.valueOf(mobSection.getString("status")));
                this.spawnerMobs.add(sintezMob);
            });

            // Если моб был переустановлен яйцом/командой
            if (current != null && !current.equals(defaultEntity.name())) {
                ListIterator<SintezMob> listIterator = this.spawnerMobs.listIterator();
                while (listIterator.hasNext()) {
                    SintezMob sintezMob = listIterator.next();
                    if (sintezMob.getEntityType().name().equals(current)) {
                        listIterator.set(new SintezMob(defaultEntity, this, MobStatus.SINTEZ));
                    }
                }
                completedList.remove(current);
                completedList.add(defaultEntity.name());
                this.config.setStringList("completed", completedList);
                this.config.setString("currentMob", defaultEntity.name());
            }
        }
        this.saveConfig();
    }

    @SneakyThrows
    private void generateMobs(EntityType defaultEntity) {
        this.config.setString("currentMob", defaultEntity.name());
        this.spawnerMobs.add(new SintezMob(defaultEntity, this, MobStatus.SINTEZ));
        this.config.setStringList("completed", Collections.singletonList(defaultEntity.name()));

        List<String> entities = new ArrayList<>(ConfigManager.getSection("entityTypes").getKeys(false));
        List<Integer> order = new Configuration(LunaSintez.getInstance().getDataFolder(), "MainMenu").getIntList("items.order");

        ConfigurationSection mobSection = this.config.createSection(this.getConfig().self(), "mobs");
        List<SintezMob> unsortedList = Lists.newArrayList();
        Set<EntityType> usedEntities = Sets.newHashSet(defaultEntity);
        for (int i = 0; i < order.size() - 1; i++) {
            EntityType entity;
            do {
                entity = EntityType.valueOf(entities.get(ThreadLocalRandom.current().nextInt(entities.size())));
            } while (usedEntities.contains(entity));

            usedEntities.add(entity);
            SintezMob mob = new SintezMob(entity, this, MobStatus.LOCKED);
            unsortedList.add(mob);
        }

        Collections.sort(unsortedList);
        unsortedList.get(0).setApplicator(MobStatus.NEXT);
        unsortedList.forEach(mob -> {
            mobSection.set(String.format("%s.status", mob.getEntityType().name()), ((MobStatus) mob.getApplicator()).name());
        });
        this.spawnerMobs.addAll(unsortedList);
    }
    @Override
    public void onInvClick(InventoryClickEvent e) {
        MenuManager.openInventory(this.player, new SintezMainMenu(this.player, this));
    }

    public void saveConfig() {
        this.config.save();
    }
    public void addCompletedMob() {
        Iterator<SintezMob> iter = this.spawnerMobs.iterator();

        SintezMob mob;
        do {
            mob = iter.next();
        } while (iter.hasNext() && !mob.getApplicator().equals(MobStatus.NEXT));
        mob.setApplicator(MobStatus.SINTEZ);

        SintezMob next = iter.next();
        if (next != null) next.setApplicator(MobStatus.NEXT);
    }

    public void openViaCommand() {
        ConfigurationSection section = this.config.getSection("mobs");

        Iterator<String> iter = section.getKeys(false).iterator();
        List<String> completedList = this.config.getStringList("completed");
        while (iter.hasNext()) {
            String key = iter.next();
            ConfigurationSection statusSection = section.getConfigurationSection(key);
            assert statusSection != null;
            if (Objects.equals(statusSection.getString("status"), "NEXT")) {
                section.set(key, null);
                try {
                    String nextKey = iter.next();
                    ConfigurationSection sSection = section.getConfigurationSection(nextKey);
                    assert sSection != null;
                    sSection.set("status", "NEXT");
                    ConfigManager.send(this.player, "nextMobOpened");

                } catch (NoSuchElementException e) {
                    ConfigManager.send(this.player, "fullSpawner");

                } finally {
                    completedList.add(key);
                    this.config.setStringList("completed", completedList);
                    this.saveConfig();
                }
            }
        }
    }
}
