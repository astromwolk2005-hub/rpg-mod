package com.beyondfivehorizons.progression;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.dimension.HorizonDimension;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Server-authoritative persistent progression storage for Beyond the Five Horizons.
 */
public class HorizonProgressionState extends PersistentState {
    private static final String STATE_KEY = BeyondFiveHorizons.MOD_ID + "_progression";
    private static final String TAG_PLAYERS = "Players";
    private static final String TAG_DISCOVERED = "Discovered";
    private static final String TAG_DEFEATED_BOSSES = "DefeatedBosses";
    private static final String TAG_PURIFIED_CORES = "PurifiedCores";

    private static final Type<HorizonProgressionState> TYPE = new Type<>(
            HorizonProgressionState::new,
            HorizonProgressionState::createFromNbt,
            null
    );

    private final Map<UUID, EnumSet<HorizonDimension>> discoveredByPlayer = new HashMap<>();
    private final Map<UUID, EnumSet<HorizonDimension>> defeatedBossesByPlayer = new HashMap<>();
    private final Map<UUID, Integer> purifiedCoresByPlayer = new HashMap<>();

    public static HorizonProgressionState get(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            return new HorizonProgressionState();
        }
        return overworld.getPersistentStateManager().getOrCreate(TYPE, STATE_KEY);
    }

    public boolean markDiscovered(UUID playerId, HorizonDimension dimension) {
        EnumSet<HorizonDimension> set = discoveredByPlayer.computeIfAbsent(
                playerId, id -> EnumSet.noneOf(HorizonDimension.class));
        if (set.add(dimension)) {
            markDirty();
            return true;
        }
        return false;
    }

    public Set<HorizonDimension> getDiscovered(UUID playerId) {
        EnumSet<HorizonDimension> set = discoveredByPlayer.get(playerId);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    public boolean markBossDefeated(UUID playerId, HorizonDimension dimension) {
        EnumSet<HorizonDimension> set = defeatedBossesByPlayer.computeIfAbsent(
                playerId, id -> EnumSet.noneOf(HorizonDimension.class));
        if (set.add(dimension)) {
            markDirty();
            return true;
        }
        return false;
    }

    public Set<HorizonDimension> getDefeatedBosses(UUID playerId) {
        EnumSet<HorizonDimension> set = defeatedBossesByPlayer.get(playerId);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    public void incrementPurifiedCores(UUID playerId) {
        purifiedCoresByPlayer.merge(playerId, 1, Integer::sum);
        markDirty();
    }

    public int getPurifiedCores(UUID playerId) {
        return purifiedCoresByPlayer.getOrDefault(playerId, 0);
    }

    public boolean hasUnlockedHeartOfReality(UUID playerId) {
        return getDefeatedBosses(playerId).size() == HorizonDimension.values().length;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound playersNbt = new NbtCompound();

        Set<UUID> allPlayers = new HashSet<>(discoveredByPlayer.keySet());
        allPlayers.addAll(defeatedBossesByPlayer.keySet());
        allPlayers.addAll(purifiedCoresByPlayer.keySet());

        for (UUID uuid : allPlayers) {
            NbtCompound playerEntry = new NbtCompound();

            NbtList discoveredList = new NbtList();
            for (HorizonDimension dim : getDiscovered(uuid)) {
                discoveredList.add(NbtString.of(dim.getId()));
            }
            playerEntry.put(TAG_DISCOVERED, discoveredList);

            NbtList bossList = new NbtList();
            for (HorizonDimension dim : getDefeatedBosses(uuid)) {
                bossList.add(NbtString.of(dim.getId()));
            }
            playerEntry.put(TAG_DEFEATED_BOSSES, bossList);
            playerEntry.putInt(TAG_PURIFIED_CORES, getPurifiedCores(uuid));

            playersNbt.put(uuid.toString(), playerEntry);
        }

        nbt.put(TAG_PLAYERS, playersNbt);
        return nbt;
    }

    private static HorizonProgressionState createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        HorizonProgressionState state = new HorizonProgressionState();
        if (!nbt.contains(TAG_PLAYERS, NbtElement.COMPOUND_TYPE)) {
            return state;
        }

        NbtCompound playersNbt = nbt.getCompound(TAG_PLAYERS);
        for (String key : playersNbt.getKeys()) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            NbtCompound playerEntry = playersNbt.getCompound(key);

            NbtList discoveredList = playerEntry.getList(TAG_DISCOVERED, NbtElement.STRING_TYPE);
            EnumSet<HorizonDimension> discoveredSet = EnumSet.noneOf(HorizonDimension.class);
            for (int i = 0; i < discoveredList.size(); i++) {
                HorizonDimension.fromId(discoveredList.getString(i)).ifPresent(discoveredSet::add);
            }
            if (!discoveredSet.isEmpty()) {
                state.discoveredByPlayer.put(uuid, discoveredSet);
            }

            NbtList bossList = playerEntry.getList(TAG_DEFEATED_BOSSES, NbtElement.STRING_TYPE);
            EnumSet<HorizonDimension> bossSet = EnumSet.noneOf(HorizonDimension.class);
            for (int i = 0; i < bossList.size(); i++) {
                HorizonDimension.fromId(bossList.getString(i)).ifPresent(bossSet::add);
            }
            if (!bossSet.isEmpty()) {
                state.defeatedBossesByPlayer.put(uuid, bossSet);
            }

            if (playerEntry.contains(TAG_PURIFIED_CORES)) {
                state.purifiedCoresByPlayer.put(uuid, playerEntry.getInt(TAG_PURIFIED_CORES));
            }
        }

        return state;
    }
}
