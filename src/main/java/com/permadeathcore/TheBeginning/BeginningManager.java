package com.permadeathcore.TheBeginning;

import com.permadeathcore.Main;
import com.permadeathcore.Util.Manager.Data.BeginningDataManager;
import com.permadeathcore.TheBeginning.WorldGenerator.BeginningGenerator;
import com.permadeathcore.TheBeginning.WorldGenerator.BeginningLootTable;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class BeginningManager implements Listener {

    private Main main;
    private World beginningWorld;

    private BeginningDataManager data;
    private boolean closed = false;

    public BeginningManager(Main main) {
        this.main = main;
        this.beginningWorld = null;
        this.data = main.getBeData();

        if (main.getDays() >= 40) {
            generateWorld();
            main.getServer().getPluginManager().registerEvents(this, main);
        }
    }

    public World getBeginningWorld() {
        return beginningWorld;
    }
    private void generateWorld() {

        if (Bukkit.getWorld("pdc_the_beginning") == null) {
            WorldCreator creator = new WorldCreator("pdc_the_beginning");
            creator.environment(World.Environment.THE_END);
            creator.generator(new BeginningGenerator());
            creator.generateStructures(false);
            this.beginningWorld = creator.createWorld();
            if (main.getConfig().getBoolean("Toggles.Doble-Mob-Cap")) {
                beginningWorld.setMonsterSpawnLimit(140);
            }
            beginningWorld.setGameRule(GameRule.MOB_GRIEFING, false);
        } else {
            this.beginningWorld = Bukkit.getWorld("pdc_the_beginning");
        }
    }
    public void generatePortal(boolean overworld, Location to) {

        if (!data.generatedOverWorldBeginningPortal() && overworld) {

            int x = main.getConfig().getInt("TheBeginning.X-Limit");
            int z = main.getConfig().getInt("TheBeginning.Z-Limit");

            int ranX = new Random().nextInt(x);
            int ranZ = new Random().nextInt(z);

            if (new Random().nextBoolean()) {
                ranX = ranX * -1;
            }
            if (new Random().nextBoolean()) {

                ranZ = ranZ * -1;
            }
            Location loc = new Location(main.world, ranX, 0, ranZ);

            int highestBlockAt = main.world.getHighestBlockAt(loc).getY();
            if (highestBlockAt == -1) {
                highestBlockAt = 50;
            }

            highestBlockAt = highestBlockAt + 15;
            loc.setY(highestBlockAt);
            pasteSchematic(loc, new File(main.getDataFolder().getAbsolutePath() + "/schematics/beginning_portal.schem"));
            data.setOverWorldPortal(loc);
        }

        if (!data.generatedBeginningPortal() && !overworld) {
            this.beginningWorld.loadChunk(to.getChunk());

            pasteSchematic(to, new File(main.getDataFolder().getAbsolutePath() + "/schematics/beginning_portal.schem"));
            data.setBeginningPortal(to);
        }
    }

    private void pasteSchematic(Location loc, File schematic) {
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(loc.getWorld());
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            Clipboard clipboard = reader.read();
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld,
                    -1)) {
                Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                        .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).ignoreAirBlocks(true).build();
                try {
                    Operations.complete(operation);
                    editSession.flushSession();
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeBeginning() {
        if (beginningWorld == null) return;
        beginningWorld.getPlayers().forEach(p -> {
            p.teleport(main.world.getSpawnLocation());
            p.playSound(p.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0F, 1.0F);
        });
        Bukkit.broadcastMessage(main.format(main.tag + "&eThe Beginning ha cerrado temporalmente (DeathTrain)."));
        this.closed = true;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        if (main.isRunningPaperSpigot()) {
            return;
        }
        Player p = e.getPlayer();
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.END_GATEWAY) return;
        if (p.getWorld().getName().equalsIgnoreCase(main.world.getName())) {
            try {
                e.getClass().getDeclaredMethod("setCanCreatePortal", Boolean.class).invoke(e, false);
            } catch (Exception x) {}
        }

        if (p.getWorld().getName().equalsIgnoreCase(beginningWorld.getName())) {
            if (p.getLocation().getBlock().getState() instanceof EndGateway) {
                EndGateway gateway = (EndGateway) p.getLocation().getBlock().getState();
                gateway.setExitLocation(null);
                gateway.update();
                p.getLocation().getBlock().getState().update();
            }
            try {
                e.getClass().getDeclaredMethod("setCanCreatePortal", Boolean.class).invoke(e, false);
            } catch (Exception x) {}
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {

        Player p = e.getPlayer();

        if (e.getCause() != PlayerTeleportEvent.TeleportCause.END_GATEWAY) return;

        if (isClosed()) {
            e.setCancelled(true);
            return;
        }

        if (main.getDays() < 50) {
            if (e.getPlayer().getWorld().getName().equalsIgnoreCase(main.world.getName()) || e.getPlayer().getWorld().getName().equalsIgnoreCase(beginningWorld.getName())) {
                e.getPlayer().setNoDamageTicks(e.getPlayer().getMaximumNoDamageTicks());
                e.getPlayer().damage(e.getPlayer().getHealth() + 1.0D, null);
                e.getPlayer().setNoDamageTicks(0);
                Bukkit.broadcastMessage(main.format("&c&lEl jugador &4&l" + e.getPlayer().getName() + " &c&lentró a TheBeginning antes de tiempo."));
            }
            return;
        }

        if (p.getWorld().getName().equalsIgnoreCase(main.world.getName())) {

            e.getPlayer().sendMessage(main.format("&eBienvenido a The Beginning."));
            e.getPlayer().teleport(beginningWorld.getSpawnLocation());
            e.setCancelled(true);

            Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                @Override
                public void run() {
                    e.getPlayer().teleport(beginningWorld.getSpawnLocation());
                }
            }, 20L);
        }

        int x = (int) p.getLocation().getX();
        int z = (int) p.getLocation().getZ();
        if (p.getWorld().getName().equalsIgnoreCase(beginningWorld.getName()) && x != 200 && z != 200) {

            if (p.getLocation().getBlock().getState() instanceof EndGateway) {

                EndGateway gateway = (EndGateway) p.getLocation().getBlock().getState();
                gateway.setExitLocation(null);
                gateway.update();
                p.getLocation().getBlock().getState().update();
            }

            e.getPlayer().teleport(main.world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (p.getWorld().getName().equalsIgnoreCase(beginningWorld.getName())) {
            if (e.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) e.getBlock().getState();
                populateChest(chest);
            }
        }
        if (e.getBlock().getType() == Material.SPAWNER && main.getNetheriteBlock() != null) {
            if (main.getDays() < 60) {
                main.getNetheriteBlock().checkForBreak(e);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        if (e.getClickedBlock() != null && p.getWorld().getName().equalsIgnoreCase(beginningWorld.getName())) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getState() instanceof Chest) {
                    Chest chest = (Chest) e.getClickedBlock().getState();
                    populateChest(chest);
                }
            }
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().getInventory().getItemInMainHand() != null && e.getClickedBlock() != null && e.getBlockFace() != null && main.getNetheriteBlock() != null) {
            if (e.getClickedBlock().getType() == Material.CHEST) return;
            ItemStack ih = e.getPlayer().getInventory().getItemInMainHand();
            if (ih.getType() == Material.DIAMOND && ih.getItemMeta().isUnbreakable()) {
                main.getNetheriteBlock().placeCustomBlock(main.getNetheriteBlock().blockFaceToLocation(e.getClickedBlock(), e.getBlockFace()));
                if (ih.getAmount() > 0) {
                    ih.setAmount(ih.getAmount() - 1);
                } else {
                    ih = null;
                }
                e.getPlayer().getInventory().setItemInMainHand(ih);
                e.getPlayer().updateInventory();
            }
        }
    }

    @EventHandler
    public void onCreatePortal(PortalCreateEvent e) {
        if (e.getWorld().getName().equalsIgnoreCase(beginningWorld.getName())) {
            for (BlockState s : e.getBlocks()) {
                Block b = s.getBlock();
                if (b.getType() == Material.END_GATEWAY || b.getType() == Material.BEDROCK || s instanceof EndGateway) {
                    if (b.getChunk().getX() == 0 && b.getChunk().getZ() == 0) {
                        e.getBlocks().remove(s);
                        s.setType(Material.AIR);
                    }
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent e) {
        if (beginningWorld == null) return;
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase(beginningWorld.getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWater(BlockDispenseEvent e) {
        if (beginningWorld == null) return;
        if (e.getItem() != null) {
            if (e.getItem().getType() == Material.BUCKET || e.getItem().getType() == Material.WATER_BUCKET) {
                if (e.getBlock().getWorld().getName().equalsIgnoreCase(beginningWorld.getName())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent e) {

        if (beginningWorld == null) return;
        if (e.isCancelled()) return;
        CreatureSpawner spawner = e.getSpawner();
        if (e.getEntity().getWorld().getName().equalsIgnoreCase(beginningWorld.getName())) {
            if (e.getEntityType() != EntityType.ARMOR_STAND) {
                spawner.setSpawnedType(e.getEntityType());
                spawner.update();
            }
            if (e.getEntityType() == EntityType.WITHER) {
                Wither w = (Wither) e.getEntity();
                w.setRemoveWhenFarAway(true);
            }
            if (e.getEntityType() == EntityType.GHAST) {

                Ghast g = (Ghast) main.getNmsHandler().spawnCustomGhast(e.getLocation().add(0, 5, 0), CreatureSpawnEvent.SpawnReason.CUSTOM, true);
                g.setCustomName(main.format("&6Ender Ghast Definitivo"));
                main.getNmsAccesor().setMaxHealth(g, 150.0D, true);
                e.setCancelled(true);
            }
            if (e.getEntityType() == EntityType.CREEPER) {

                e.getEntity().setCustomName(main.format("&6Quantum Creeper"));
                ((Creeper)e.getEntity()).setPowered(true);
                e.getEntity().getPersistentDataContainer().set(new NamespacedKey(main, "quantum_creeper"), PersistentDataType.BYTE, (byte) 1);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {

        if (beginningWorld == null) return;

        if (e.getPlayer().getWorld().getName().equalsIgnoreCase(beginningWorld.getName())) {
            if (e.getBlock().getState() instanceof Chest) {
                BeginningDataManager ma = main.getBeData();
                ma.addPopulatedChest(e.getBlock().getLocation());
            }
        }
    }

    private void populateChest(Chest chest) {

        if (data.getConfig().contains("PopulatedChests")) {
            if (data.hasPopulatedChest(chest.getLocation())) return;
            if (main.getDays() < 60) {
                new BeginningLootTable(this).populateChest(chest);
            }
            data.addPopulatedChest(chest.getLocation());
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
