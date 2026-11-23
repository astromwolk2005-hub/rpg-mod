package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.entity.boss.HeartOfTheForestEntity;
import com.beyondfivehorizons.entity.mobs.ArborealGuardianEntity;
import com.beyondfivehorizons.entity.mobs.FireflyEntity;
import com.beyondfivehorizons.entity.mobs.PetalmawEntity;
import com.beyondfivehorizons.entity.mobs.RootwalkerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.Heightmap;

public final class ModEntities {
    public static final EntityType<FireflyEntity> FIREFLY = Registry.register(
            Registries.ENTITY_TYPE,
            BeyondFiveHorizons.id("firefly"),
            EntityType.Builder.create(FireflyEntity::new, SpawnGroup.AMBIENT)
                    .dimensions(0.45f, 0.45f)
                    .maxTrackingRange(8)
                    .build()
    );

    public static final EntityType<RootwalkerEntity> ROOTWALKER = Registry.register(
            Registries.ENTITY_TYPE,
            BeyondFiveHorizons.id("rootwalker"),
            EntityType.Builder.create(RootwalkerEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.75f, 1.85f)
                    .maxTrackingRange(8)
                    .build()
    );

    public static final EntityType<PetalmawEntity> PETALMAW = Registry.register(
            Registries.ENTITY_TYPE,
            BeyondFiveHorizons.id("petalmaw"),
            EntityType.Builder.create(PetalmawEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.95f, 1.25f)
                    .maxTrackingRange(8)
                    .build()
    );

    public static final EntityType<ArborealGuardianEntity> ARBOREAL_GUARDIAN = Registry.register(
            Registries.ENTITY_TYPE,
            BeyondFiveHorizons.id("arboreal_guardian"),
            EntityType.Builder.create(ArborealGuardianEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.45f, 2.85f)
                    .maxTrackingRange(10)
                    .build()
    );

    public static final EntityType<HeartOfTheForestEntity> HEART_OF_THE_FOREST = Registry.register(
            Registries.ENTITY_TYPE,
            BeyondFiveHorizons.id("heart_of_the_forest"),
            EntityType.Builder.create(HeartOfTheForestEntity::new, SpawnGroup.MONSTER)
                    .dimensions(2.6f, 4.8f)
                    .maxTrackingRange(12)
                    .build()
    );

    private ModEntities() {
    }

    public static void register() {
        FabricDefaultAttributeRegistry.register(FIREFLY, FireflyEntity.createFireflyAttributes());
        FabricDefaultAttributeRegistry.register(ROOTWALKER, RootwalkerEntity.createRootwalkerAttributes());
        FabricDefaultAttributeRegistry.register(PETALMAW, PetalmawEntity.createPetalmawAttributes());
        FabricDefaultAttributeRegistry.register(ARBOREAL_GUARDIAN, ArborealGuardianEntity.createArborealGuardianAttributes());
        FabricDefaultAttributeRegistry.register(HEART_OF_THE_FOREST, HeartOfTheForestEntity.createBossAttributes());

        SpawnRestriction.register(FIREFLY, SpawnLocationTypes.UNRESTRICTED,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FireflyEntity::canSpawn);
        SpawnRestriction.register(ROOTWALKER, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RootwalkerEntity::canSpawn);
        SpawnRestriction.register(PETALMAW, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PetalmawEntity::canSpawn);
        SpawnRestriction.register(ARBOREAL_GUARDIAN, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ArborealGuardianEntity::canSpawn);

        BeyondFiveHorizons.LOGGER.info("Registered Elysium entities and Heart of the Forest boss.");
    }
}
