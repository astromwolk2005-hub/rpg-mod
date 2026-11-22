package com.beyondfivehorizons.item;

import com.beyondfivehorizons.dimension.HorizonDimension;
import com.beyondfivehorizons.progression.HorizonProgressionState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

/**
 * Foundation item for the Explorer's Journal.
 * In Stage 1, prints discovered Horizons and boss progression from PersistentState.
 * Full graphical journal and bestiary UI will be wired in later stages.
 */
public class ExplorerJournalItem extends Item {
    public ExplorerJournalItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer && serverPlayer.getServer() != null) {
            HorizonProgressionState state = HorizonProgressionState.get(serverPlayer.getServer());
            Set<HorizonDimension> discovered = state.getDiscovered(serverPlayer.getUuid());
            Set<HorizonDimension> bosses = state.getDefeatedBosses(serverPlayer.getUuid());

            serverPlayer.sendMessage(Text.translatable("message.beyondfivehorizons.journal_header")
                    .formatted(Formatting.GOLD, Formatting.BOLD), false);

            for (HorizonDimension dim : HorizonDimension.values()) {
                boolean seen = discovered.contains(dim);
                boolean cleared = bosses.contains(dim);
                MutableText line = Text.literal(" - ")
                        .append(Text.translatable(dim.getTranslationKey())
                                .formatted(seen ? Formatting.AQUA : Formatting.DARK_GRAY))
                        .append(Text.literal(" ["))
                        .append(Text.translatable(seen
                                ? "message.beyondfivehorizons.status_discovered"
                                : "message.beyondfivehorizons.status_unknown")
                                .formatted(seen ? Formatting.GREEN : Formatting.GRAY))
                        .append(Text.literal(" | "))
                        .append(Text.translatable(cleared
                                ? "message.beyondfivehorizons.boss_defeated"
                                : "message.beyondfivehorizons.boss_alive")
                                .formatted(cleared ? Formatting.GOLD : Formatting.RED))
                        .append(Text.literal("]"));
                serverPlayer.sendMessage(line, false);
            }
        }
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.beyondfivehorizons.explorer_journal").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
