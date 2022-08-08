package com.teamabnormals.blueprint.core.util.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.item.BlueprintBoatItem;
import com.teamabnormals.blueprint.common.item.FuelItem;
import com.teamabnormals.blueprint.core.registry.BoatTypeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A basic {@link AbstractSubRegistryHelper} for items.
 * <p>This contains some useful registering methods for items.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class ItemSubRegistryHelper extends AbstractSubRegistryHelper<Item> {

	public ItemSubRegistryHelper(RegistryHelper parent, DeferredRegister<Item> deferredRegister) {
		super(parent, deferredRegister);
	}

	public ItemSubRegistryHelper(RegistryHelper parent) {
		super(parent, DeferredRegister.create(ForgeRegistries.ITEMS, parent.getModId()));
	}

	/**
	 * Creates and registers a {@link StandingAndWallBlockItem}.
	 *
	 * @param floorBlock The floor {@link Block}.
	 * @param wallBlock  The wall {@link Block}.
	 * @param itemGroup  The {@link CreativeModeTab} for the {@link StandingAndWallBlockItem}.
	 * @return The created {@link StandingAndWallBlockItem}.
	 * @see StandingAndWallBlockItem
	 */
	public static BlockItem createStandingAndWallBlockItem(Block floorBlock, Block wallBlock, CreativeModeTab itemGroup) {
		return new StandingAndWallBlockItem(floorBlock, wallBlock, new Item.Properties().tab(itemGroup));
	}

	/**
	 * Creates and registers a {@link DoubleHighBlockItem}.
	 *
	 * @param blockForInput The {@link Block} for the item.
	 * @param itemGroup     The {@link CreativeModeTab} for the {@link DoubleHighBlockItem}.
	 * @return The created {@link DoubleHighBlockItem}.
	 * @see DoubleHighBlockItem
	 */
	public static BlockItem createDoubleHighBlockItem(Block blockForInput, CreativeModeTab itemGroup) {
		return new DoubleHighBlockItem(blockForInput, new Item.Properties().tab(itemGroup));
	}

	/**
	 * Creates a {@link FuelItem}.
	 *
	 * @param burnTime  How long the item will burn (measured in ticks).
	 * @param itemGroup The {@link CreativeModeTab} for the {@link FuelItem}.
	 * @return The created {@link FuelItem}.
	 */
	public static FuelItem createFuelItem(int burnTime, CreativeModeTab itemGroup) {
		return new FuelItem(burnTime, new Item.Properties().tab(itemGroup));
	}

	/**
	 * Creates a {@link BlockItem} with a specified {@link Block} and {@link CreativeModeTab}.
	 *
	 * @param blockForInput The {@link Block} for the {@link BlockItem}.
	 * @param itemGroup     The {@link CreativeModeTab} for the {@link BlockItem}, null to have it be in no group.
	 * @return The BlockItem.
	 */
	public static BlockItem createSimpleBlockItem(Block blockForInput, @Nullable CreativeModeTab itemGroup) {
		return new BlockItem(blockForInput, new Item.Properties().tab(itemGroup));
	}

	/**
	 * Creates a simple {@link Item.Properties} with a stack size and {@link CreativeModeTab}.
	 *
	 * @param stackSize The item's max stack size.
	 * @param itemGroup The item's CreativeModeTab.
	 * @return The simple {@link Item.Properties}.
	 */
	public static Item.Properties createSimpleItemProperty(int stackSize, CreativeModeTab itemGroup) {
		return new Item.Properties().tab(itemGroup).stacksTo(stackSize);
	}

	/**
	 * Registers an {@link Item}.
	 *
	 * @param name     The name for the item.
	 * @param supplier A {@link Supplier} containing the {@link Item}.
	 * @return A {@link RegistryObject} containing the {@link Item}.
	 */
	public <I extends Item> RegistryObject<I> createItem(String name, Supplier<? extends I> supplier) {
		return this.deferredRegister.register(name, supplier);
	}

	/**
	 * Creates and registers a compat {@link Item}.
	 *
	 * @param modId      The mod id of the mod this item is compatible for, set to "indev" for dev tests.
	 * @param name       The name for the item.
	 * @param properties The item's properties.
	 * @param group      The {@link CreativeModeTab} for the {@link Item}.
	 * @return A {@link RegistryObject} containing the {@link Item}.
	 */
	public RegistryObject<Item> createCompatItem(String modId, String name, Item.Properties properties, CreativeModeTab group) {
		return this.deferredRegister.register(name, () -> new Item(properties.tab(areModsLoaded(modId) ? group : null)));
	}

	/**
	 * Creates and registers a compat {@link Item}.
	 *
	 * @param name       The name for the item.
	 * @param properties The item's properties.
	 * @param group      The {@link CreativeModeTab} for the {@link Item}.
	 * @param modIds     The mod ids of the mods this block is compatible for.
	 * @return A {@link RegistryObject} containing the {@link Item}.
	 */
	public RegistryObject<Item> createCompatItem(String name, Item.Properties properties, CreativeModeTab group, String... modIds) {
		return this.deferredRegister.register(name, () -> new Item(properties.tab(areModsLoaded(modIds) ? group : null)));
	}

	/**
	 * Creates and registers a {@link ForgeSpawnEggItem}.
	 *
	 * @param entityName     The name of the entity this spawn egg spawns.
	 * @param supplier       The supplied {@link EntityType}.
	 * @param primaryColor   The egg's primary color.
	 * @param secondaryColor The egg's secondary color.
	 * @return A {@link RegistryObject} containing the {@link ForgeSpawnEggItem}.
	 * @see ForgeSpawnEggItem
	 */
	public RegistryObject<ForgeSpawnEggItem> createSpawnEggItem(String entityName, Supplier<EntityType<? extends Mob>> supplier, int primaryColor, int secondaryColor) {
		return this.deferredRegister.register(entityName + "_spawn_egg", () -> new ForgeSpawnEggItem(supplier, primaryColor, secondaryColor, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
	}

	/**
	 * Registers a new boat type and registers two new {@link BlueprintBoatItem} instances for a boat and a chest boat.
	 *
	 * @param wood  The name of the wood, e.g. "oak".
	 * @param block The {@link Block} for the boat to drop.
	 * @return A {@link Pair} instance containing the boat item and the chest boat item.
	 */
	public Pair<RegistryObject<Item>, RegistryObject<Item>> createBoatAndChestBoatItem(String wood, RegistryObject<Block> block) {
		String type = this.parent.getModId() + ":" + wood;
		RegistryObject<Item> boat = this.deferredRegister.register(wood + "_boat", () -> new BlueprintBoatItem(false, type, createSimpleItemProperty(1, CreativeModeTab.TAB_TRANSPORTATION)));
		RegistryObject<Item> chestBoat = this.deferredRegister.register(wood + "_chest_boat", () -> new BlueprintBoatItem(true, type, createSimpleItemProperty(1, CreativeModeTab.TAB_TRANSPORTATION)));
		BoatTypeRegistry.registerBoat(type, boat, chestBoat, block);
		return Pair.of(boat, chestBoat);
	}

}
