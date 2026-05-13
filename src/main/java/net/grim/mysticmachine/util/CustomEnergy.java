package net.grim.mysticmachine.util;

import net.neoforged.neoforge.energy.EnergyStorage;

public abstract class CustomEnergy extends EnergyStorage {
    public CustomEnergy(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    // This allows us to manually set the energy level during sync
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public abstract void onContentsChanged();
}