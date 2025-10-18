package ru.ntwz.jasds.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DebugStickStateComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.ntwz.jasds.config.ConfigManager;

import java.util.Collection;
import java.util.Map;

@Mixin(DebugStickItem.class)
public abstract class DebugStickMixin extends Item {

    public DebugStickMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    private static void sendMessage(PlayerEntity player, Text message) {
    }

    @Shadow
    private static <T extends Comparable<T>> String getValueString(BlockState state, Property<T> property) {
        return null;
    }

    @Shadow
    private static <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property, boolean inverse) {
        return null;
    }

    @Shadow
    private static <T> T cycle(Iterable<T> values, @Nullable T value, boolean inverse) {
        return null;
    }

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    private void onUse(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (player.isCreativeLevelTwoOp()) return;

        RegistryEntry<Block> blockEntry = state.getRegistryEntry();
        Collection<Property<?>> properties = blockEntry.value().getStateManager().getProperties();

        DebugStickStateComponent stickState = stack.get(DataComponentTypes.DEBUG_STICK_STATE);
        if (stickState == null) stickState = new DebugStickStateComponent(Map.of());

        Property<?> currentProperty = stickState.properties().get(blockEntry);
        boolean inverse = player.shouldCancelInteraction();

        if (update) {
            handleUpdateMode(player, world, pos, stack, state, blockEntry, stickState, currentProperty, properties, inverse);
        } else {
            handleSelectMode(player, stack, state, blockEntry, stickState, currentProperty, properties, inverse);
        }

        applyCooldown(player, cir);
    }

    @Unique
    private void handleUpdateMode(PlayerEntity player, WorldAccess world, BlockPos pos, ItemStack stack,
                                  BlockState state, RegistryEntry<Block> blockEntry,
                                  DebugStickStateComponent stickState, @Nullable Property<?> currentProperty,
                                  Collection<Property<?>> properties, boolean inverse) {

        if (currentProperty != null) {
            if (!checkRestrictions(state, currentProperty)) {
                summonRestrictedParticles(world, pos);
                return;
            }

            BlockState newState = cyclePropertyValue(state, currentProperty, inverse);
            world.setBlockState(pos, newState, 18);
            sendMessage(player, Text.translatable(
                    getTranslationKey() + ".update",
                    currentProperty.getName(),
                    getPropertyValueString(newState, currentProperty)
            ));
        } else {
            selectNextProperty(player, stack, state, blockEntry, stickState, properties, null, inverse);
        }
    }

    @Unique
    private void handleSelectMode(PlayerEntity player, ItemStack stack, BlockState state,
                                  RegistryEntry<Block> blockEntry, DebugStickStateComponent stickState,
                                  @Nullable Property<?> currentProperty, Collection<Property<?>> properties, boolean inverse) {

        selectNextProperty(player, stack, state, blockEntry, stickState, properties, currentProperty, inverse);
    }

    @Unique
    private void selectNextProperty(PlayerEntity player, ItemStack stack, BlockState state,
                                    RegistryEntry<Block> blockEntry, DebugStickStateComponent stickState,
                                    Collection<Property<?>> properties, @Nullable Property<?> currentProperty, boolean inverse) {

        Property<?> next = getNextProperty(properties, currentProperty, inverse);
        if (next != null) {
            stack.set(DataComponentTypes.DEBUG_STICK_STATE, stickState.with(blockEntry, next));
            sendMessage(player, Text.translatable(
                    getTranslationKey() + ".select",
                    next.getName(),
                    getPropertyValueString(state, next)
            ));
        }
    }

    @Unique
    private void applyCooldown(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack defaultStack = getDefaultStack();
        if (!player.getItemCooldownManager().isCoolingDown(defaultStack)) {
            player.getItemCooldownManager().set(defaultStack, ConfigManager.getConfig().useCooldown);
        }
        cir.setReturnValue(true);
    }

    @Unique
    private Property<?> getNextProperty(Collection<Property<?>> properties, @Nullable Property<?> currentProperty, boolean inverse) {
        if (properties.isEmpty()) return null;
        return currentProperty == null ? properties.iterator().next() : cycle(properties, currentProperty, inverse);
    }

    @Unique
    private <T extends Comparable<T>> BlockState cyclePropertyValue(BlockState state, Property<T> property, boolean inverse) {
        return cycle(state, property, inverse);
    }

    @Unique
    private <T extends Comparable<T>> String getPropertyValueString(BlockState state, Property<T> property) {
        return getValueString(state, property);
    }

    @Unique
    private boolean checkRestrictions(BlockState state, Property<?> property) {
        String propertyName = property.getName();
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        boolean whitelistEnabled = ConfigManager.getConfig().whitelistEnabled;
        boolean blacklistEnabled = ConfigManager.getConfig().blacklistEnabled;

        if (whitelistEnabled) {
            boolean inWhitelist = ConfigManager.getWhitelist().blocks.contains(blockId)
                    || ConfigManager.getWhitelist().properties.contains(propertyName);
            if (!inWhitelist) return false;
        }

        if (blacklistEnabled) {
            return !ConfigManager.getBlacklist().blocks.contains(blockId)
                    && !ConfigManager.getBlacklist().properties.contains(propertyName);
        }

        return true;
    }

    @Unique
    private void summonRestrictedParticles(WorldAccess world, BlockPos pos) {
        ServerWorld serverWorld = (ServerWorld) world;
        serverWorld.spawnParticles(
                ParticleTypes.ANGRY_VILLAGER,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                4,
                0.5,
                1.0,
                0.5,
                0.1
        );
    }
}