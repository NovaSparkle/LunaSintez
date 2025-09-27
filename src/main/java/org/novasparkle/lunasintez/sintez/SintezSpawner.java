package org.novasparkle.lunasintez.sintez;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunasintez.LunaSintez;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.menus.CloseableMenu;
import org.novasparkle.lunasintez.menus.mainMenu.SintezMainMenu;
import org.novasparkle.lunasintez.sintez.mobs.SintezMob;
import org.novasparkle.lunasintez.sintez.mobs.status.EvoStatus;
import org.novasparkle.lunasintez.sintez.mobs.status.SintezStatus;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.menus.IMenu;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.novasparkle.lunaspring.API.util.utilities.Localization;
import org.satellite.dev.progiple.satespawnerapi.api.ASpawner;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class SintezSpawner extends ASpawner {
    public static final String nbtEntity = "LunaSintezEntity";

    private final List<SintezMob> spawnerMobs;
    private final CreatureSpawner worldSpawner;
    private final Configuration config;

    public SintezSpawner(Location location) {
        super(location, LunaSintez.getInstance().getLunaSintezComponent());
        this.worldSpawner = (CreatureSpawner) location.getBlock().getState();
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


    public static NonMenuItem getSpawnerItem(EntityType mobType) {
        NonMenuItem spawner = new NonMenuItem(new IConfig(LunaSintez.getInstance().getDataFolder(), "menus/MainMenu").getSection("items.DROPPED_SPAWNER"));
        spawner.setDisplayName(spawner.getDisplayName().replace("[type]", Localization.localize(mobType)));
        NBTManager.setString(spawner.getItemStack(), nbtEntity, mobType.name());
        return spawner;
    }

    public void onDrop() {
        NonMenuItem dropItem = getSpawnerItem(this.worldSpawner.getSpawnedType());
        this.getLocation().getBlock().setType(Material.AIR);
        dropItem.dropNaturally(this.getLocation());
        this.onBreakSpawner(null);
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
    public void onBreakSpawner(BlockBreakEvent event) {
        IMenu iMenu = (IMenu) MenuManager.getActiveMenus(CloseableMenu.class, true).filter(menu -> menu.belongsToSpawner(this)).findFirst().orElse(null);
        if (iMenu != null) {
            List<HumanEntity> viewers = iMenu.getInventory().getViewers();
            viewers.forEach(HumanEntity::closeInventory);
        }
        this.deleteFile();
    }


    @Override
    public void onBlockClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        if (itemStack != null && itemStack.getType().name().endsWith("_SPAWN_EGG")) {
            EntityType entityType = EntityType.valueOf(itemStack.getType().name().replace("_SPAWN_EGG", ""));
            SintezMob mobToOpen = spawnerMobs.stream().filter(mob -> mob.getEntityType().equals(entityType)).findFirst().orElse(null);
            if (mobToOpen != null) {
                mobToOpen.setApplicator(SintezStatus.SINTEZ);
                ConfigManager.send(event.getPlayer(), "alreadySintezied", "newType-%-" + mobToOpen.getEntityType().name());
            }
        }

        if (!player.isSneaking()) {
            event.setCancelled(true);
            Set<ProtectedRegion> rgs = GuardManager.getRegions(event.getClickedBlock().getLocation());
            if (!rgs.isEmpty()) {
                ProtectedRegion region = rgs.iterator().next();
                if (!region.isMember(WorldGuardPlugin.inst().wrapPlayer(player))) return;
            }
            this.loadMobList();

            MenuManager.openInventory(new SintezMainMenu(player, this));
        }
    }


    private void loadMobList() {
        List<String> completedList = this.config.getStringList("completed");
        EntityType defaultEntity = this.worldSpawner.getSpawnedType();
        if (completedList.isEmpty()) {
            this.generateMobs(defaultEntity);

        } else {
            String current = config.getString("currentMob");
            completedList.forEach(e -> this.spawnerMobs.add(new SintezMob(EntityType.valueOf(e), this, SintezStatus.SINTEZ)));
            this.config.getSection("mobs").getValues(false).forEach((key, value) -> {
                ConfigurationSection mobSection = (ConfigurationSection) value;
                SintezMob sintezMob = new SintezMob(EntityType.valueOf(mobSection.getName()), this, SintezStatus.valueOf(mobSection.getString("status")));
                this.spawnerMobs.add(sintezMob);
            });

            // Если моб был переустановлен яйцом/командой
            if (current != null && !current.equals(defaultEntity.name())) {
                ListIterator<SintezMob> listIterator = this.spawnerMobs.listIterator();
                while (listIterator.hasNext()) {
                    SintezMob sintezMob = listIterator.next();
                    if (sintezMob.getEntityType().name().equals(current)) {
                        listIterator.set(new SintezMob(defaultEntity, this, SintezStatus.SINTEZ));
                        completedList.remove(current);
                        completedList.add(defaultEntity.name());
                        this.config.setStringList("completed", completedList);
                        this.config.setString("currentMob", defaultEntity.name());
                        break;
                    }
                }
            }
        }
        this.saveConfig();
    }


    @SneakyThrows
    private void generateMobs(EntityType defaultEntity) {
        this.config.setString("currentMob", defaultEntity.name());
        this.spawnerMobs.add(new SintezMob(defaultEntity, this, SintezStatus.SINTEZ));
        this.config.setStringList("completed", Collections.singletonList(defaultEntity.name()));

        ConfigurationSection allEntitiesSection = ConfigManager.getSection("entityTypes");

        List<String> entities = new ArrayList<>(allEntitiesSection.getKeys(false));
        List<Integer> order = new Configuration(LunaSintez.getInstance().getDataFolder(), "menus/MainMenu").getIntList("items.order");

        ConfigurationSection mobSection = this.config.createSection(this.getConfig().self(), "mobs");
        List<SintezMob> unsortedList = Lists.newArrayList();
        Set<EntityType> usedEntities = Sets.newHashSet(defaultEntity);
        for (int i = 0; i < order.size() - 1; i++) {
            EntityType entity;
            do {
                entity = EntityType.valueOf(entities.get(ThreadLocalRandom.current().nextInt(entities.size())));
            } while (usedEntities.contains(entity) || allEntitiesSection.getBoolean(String.format("%s.ignore", entity.name().toUpperCase())));

            usedEntities.add(entity);
            SintezMob mob = new SintezMob(entity, this, SintezStatus.LOCKED);
            unsortedList.add(mob);
        }

        Collections.sort(unsortedList);
        unsortedList.get(0).setApplicator(SintezStatus.NEXT);
        unsortedList.forEach(mob -> {
            mobSection.set(String.format("%s.status", mob.getEntityType().name()), ((SintezStatus) mob.getApplicator()).name());
        });
        this.spawnerMobs.addAll(unsortedList);
    }


    @Override
    public void onInvClick(InventoryClickEvent event) {
        MenuManager.openInventory(new SintezMainMenu((Player) event.getWhoClicked(), this));
    }


    public void saveConfig() {
        this.config.save();
    }
    private void unlockInSpawner(SintezMob sintezMob, Player player) {
        sintezMob.setApplicator(SintezStatus.SINTEZ);

        int index = this.spawnerMobs.indexOf(sintezMob);

        if (index == this.spawnerMobs.size() - 1) {
            ConfigManager.send(player, "fullSpawner");
        } else {
            this.spawnerMobs.get(index + 1).setApplicator(SintezStatus.NEXT);
        }
    }

    public void unlockMob(SintezMob mob, Player player) {
        Configuration config = this.getConfig();
        config.set(String.format("mobs.%s", mob.getEntityType().name()), null);
        ConfigurationSection section = config.getSection("mobs");
        config.set(String.format("evolutions.%s.status", mob.getEntityType().name()), EvoStatus.EVO_OPENED.name());
        for (String key : section.getKeys(false)) {
            section.set(String.format("%s.status", key), "NEXT");
            break;
        }
        List<String> completedList = config.getStringList("completed");
        completedList.add(mob.getEntityType().name());
        config.setStringList("completed", completedList);

        config.save();
        this.unlockInSpawner(mob, player);
    }

    public void unlockMob(Player player) {
        this.loadMobList();
        SintezMob mob = this.spawnerMobs.stream().filter(m -> m.getApplicator().equals(SintezStatus.NEXT)).findFirst().orElse(null);
        if (mob == null) {
            ConfigManager.send(player, "fullSpawner");
        } else {
            this.unlockMob(mob, player);
        }
    }

    @Override
    public boolean equals(Object spawner) {
        if (this == spawner) return true;
        if (spawner == null || getClass() != spawner.getClass()) return false;
        SintezSpawner that = (SintezSpawner) spawner;
        return Objects.equals(this.getLocation(), that.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorldSpawner());
    }
}
