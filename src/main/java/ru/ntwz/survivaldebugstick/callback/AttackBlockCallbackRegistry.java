package ru.ntwz.survivaldebugstick.callback;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;

public class AttackBlockCallbackRegistry {
    public static void register(Logger logger) {
        AttackBlockCallback.EVENT.register(
                (player, world, hand, pos, direction) -> {
                    if (player.isSpectator() || player.isCreative()) return ActionResult.PASS;
                    ItemStack item = player.getStackInHand(hand);
                    if (!item.isOf(Items.DEBUG_STICK)) return ActionResult.PASS;

                    if (!player.getItemCooldownManager().isCoolingDown(item)) {
                        player.getItemCooldownManager().set(item, 5);
                    } else {
                        return ActionResult.PASS;
                    }

                    item.getItem().canMine(item, world.getBlockState(pos), world, pos, player);
                    return ActionResult.PASS;
                }
        );
        logger.info("Registered attack block callback event");
    }
}
