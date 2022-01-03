package com.permadeathcore.CustomMobs.v1_16_R3.PigmanJockeys;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Bee;

public class SpecialBee extends EntityBee {

    public SpecialBee(Location loc) {
        super(EntityTypes.BEE, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(100.0D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(12.0D);
        Bee bee = (Bee) getBukkitEntity();
        bee.setHealth(100.0D);
        bee.setAnger(10000);
        bee.setRemoveWhenFarAway(true);

        this.goalSelector.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.0D, true));


    }

    @Override
    public void saveData(NBTTagCompound tag) {
        super.saveData(tag);
        tag.setInt("anger", 100000);
    }
}