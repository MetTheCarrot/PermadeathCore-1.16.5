package com.permadeathcore.Entity.MobFactory;

import com.permadeathcore.Main;

import org.bukkit.*;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class MobFactory {

    private Main instance;

    public MobFactory(Main instance) {
        this.instance = instance;
    }

    public Creeper spawnEnderCreeper(Location l, Creeper c) {

        //Creeper c = (Creeper) instance.getNmsHandler().spawnCustomCreeper(l, CreatureSpawnEvent.SpawnReason.CUSTOM, true);

        if (c == null) {
            c = l.getWorld().spawn(l, Creeper.class);
        }

        c.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));

        c.setCustomName(format("&6Ender Creeper"));
        addPersistentData(c, "ender_creeper");

        return c;
    }

    public Creeper spawnQuantumCreeper(Location l, @Nullable Creeper c) {

        //Creeper c = (Creeper) instance.getNmsHandler().spawnCustomCreeper(l, CreatureSpawnEvent.SpawnReason.CUSTOM, false);
        if (c == null) {
            c = l.getWorld().spawn(l, Creeper.class);
        }
        c.setCustomName(format("&6Quantum Creeper"));
        addPersistentData(c, "quantum_creeper");
        c.setExplosionRadius(instance.getConfig().getInt("Toggles.Quantum-Explosion-Power"));
        return c;
    }

    public Creeper spawnEnderQuantumCreeper(Location l, @Nullable Creeper c) {

        //Creeper c = (Creeper) instance.getNmsHandler().spawnCustomCreeper(l, CreatureSpawnEvent.SpawnReason.CUSTOM, true);

        if (c == null) {
            c = l.getWorld().spawn(l, Creeper.class);
        }
        c.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        c.setCustomName(format("&6Ender Quantum Creeper"));
        addPersistentData(c, "ender_quantum_creeper");
        c.setExplosionRadius(instance.getConfig().getInt("Toggles.Quantum-Explosion-Power"));

        return c;
    }

    public boolean hasData(Entity entity, String id) {
        return entity.getPersistentDataContainer().has(new NamespacedKey(instance, id), PersistentDataType.BYTE);
    }

    public void addPersistentData(Entity entity, String id) {

        entity.getPersistentDataContainer().set(new NamespacedKey(instance, id), PersistentDataType.BYTE, (byte) 1);
    }

    public String format(String s) {
        return instance.format(s);
    }
}