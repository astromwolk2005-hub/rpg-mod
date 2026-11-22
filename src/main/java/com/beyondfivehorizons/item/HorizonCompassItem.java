package com.beyondfivehorizons.item;

import com.beyondfivehorizons.dimension.HorizonDimension;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/**
 * Spatial orientation device for Beyond the Five Horizons.
 * Reports the player's current Horizon and spatial coordinates.
 */
public class HorizonCompassItem extends Item {
    public HorizonCompassItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            Optional<HorizonDimension> current = HorizonDimension.fromWorldKey(world.getRegistryKey());
            Text realmName = current
                    .map(dim -> Text.translatable(dim.getTranslationKey()))
                    .orElseGet(() -> Text.literal(world.getRegistryKey().getValue().toString()));
            user.sendMessage(
                    Text.translatable("message.beyondfivehorizons.compass_reading",
                            realmName, user.getBlockX(), user.getBlockY(), user.getBlockZ()),
                    true
            );
        }
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.beyondfivehorizons.horizon_compass").formatted(Formatting.AQUA));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
