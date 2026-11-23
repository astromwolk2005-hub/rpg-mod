package com.beyondfivehorizons.client;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.client.model.ArborealGuardianModel;
import com.beyondfivehorizons.client.model.FireflyModel;
import com.beyondfivehorizons.client.model.HeartOfTheForestModel;
import com.beyondfivehorizons.client.model.ModModelLayers;
import com.beyondfivehorizons.client.model.PetalmawModel;
import com.beyondfivehorizons.client.model.RootwalkerModel;
import com.beyondfivehorizons.client.particle.HorizonSporeParticle;
import com.beyondfivehorizons.client.renderer.ArborealGuardianRenderer;
import com.beyondfivehorizons.client.renderer.FireflyRenderer;
import com.beyondfivehorizons.client.renderer.HeartOfTheForestRenderer;
import com.beyondfivehorizons.client.renderer.PetalmawRenderer;
import com.beyondfivehorizons.client.renderer.RootwalkerRenderer;
import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModEntities;
import com.beyondfivehorizons.registry.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class BeyondFiveHorizonsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BeyondFiveHorizons.LOGGER.info("Initializing Beyond the Five Horizons client (Stage 2)...");

        // Block render layers (cutout / translucent)
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ELYSIUM_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ELYSIUM_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ELYSIUM_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EVOLVING_SPROUT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.NIGHTBLOOM_FLOWER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REACTIVE_BLOSSOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ELYSIUM_GLASS, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ELYSIUM_PORTAL, RenderLayer.getTranslucent());

        // Custom particle factories
        ParticleFactoryRegistry.getInstance().register(ModParticles.LIVING_SPORE, HorizonSporeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLIGHT_SPORE, HorizonSporeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.CRYSTAL_SHIMMER, HorizonSporeParticle.Factory::new);

        // Entity model layers
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.FIREFLY, FireflyModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.ROOTWALKER, RootwalkerModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PETALMAW, PetalmawModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.ARBOREAL_GUARDIAN, ArborealGuardianModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.HEART_OF_THE_FOREST, HeartOfTheForestModel::getTexturedModelData);

        // Entity renderers
        EntityRendererRegistry.register(ModEntities.FIREFLY, FireflyRenderer::new);
        EntityRendererRegistry.register(ModEntities.ROOTWALKER, RootwalkerRenderer::new);
        EntityRendererRegistry.register(ModEntities.PETALMAW, PetalmawRenderer::new);
        EntityRendererRegistry.register(ModEntities.ARBOREAL_GUARDIAN, ArborealGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.HEART_OF_THE_FOREST, HeartOfTheForestRenderer::new);
    }
}
