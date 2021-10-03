package com.minecraftabnormals.abnormals_core.common.advancement.modification;

import com.google.gson.*;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.events.AdvancementBuildingEvent;
import com.minecraftabnormals.abnormals_core.core.util.modification.ConfiguredModifier;
import com.minecraftabnormals.abnormals_core.core.util.modification.ModificationManager;
import com.minecraftabnormals.abnormals_core.core.util.modification.TargetedModifier;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Data manager class for {@link com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers.IAdvancementModifier}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public final class AdvancementModificationManager extends ModificationManager<Advancement.Builder, Void, DeserializationContext> {
	private static final Gson GSON = (new GsonBuilder()).create();
	private static AdvancementModificationManager INSTANCE;

	private final PredicateManager lootPredicateManager;

	private AdvancementModificationManager(PredicateManager lootPredicateManager) {
		super(GSON, "modifiers/advancements");
		this.lootPredicateManager = lootPredicateManager;
	}

	/**
	 * Gets the instance of the {@link AdvancementModificationManager}.
	 * <p>This is initialized once it has been added as a reload listener.</p>
	 *
	 * @return The instance of the {@link AdvancementModificationManager}.
	 */
	public static AdvancementModificationManager getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) throws NoSuchFieldException, IllegalAccessException {
		INSTANCE = new AdvancementModificationManager(event.getDataPackRegistries().getPredicateManager());
		//Advancement modifiers must load before advancements
		Field field = AddReloadListenerEvent.class.getDeclaredField("dataPackRegistries");
		field.setAccessible(true);
		SimpleReloadableResourceManager reloadableResourceManager = (SimpleReloadableResourceManager) ((ServerResources) field.get(event)).getResourceManager();

		List<PreparableReloadListener> reloadListeners = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, reloadableResourceManager, "f_10871_");
		if (reloadListeners != null) {
			reloadListeners.add(4, INSTANCE);
		}
	}

	@SubscribeEvent
	public static void onBuildingAdvancement(AdvancementBuildingEvent event) {
		List<ConfiguredModifier<Advancement.Builder, ?, Void, DeserializationContext, ?>> modifiers = INSTANCE.getModifiers(event.getLocation());
		if (modifiers != null) {
			Advancement.Builder builder = event.getBuilder();
			modifiers.forEach(modifier -> {
				modifier.modify(builder);
			});
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
		this.reset();
		for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue;

			try {
				TargetedModifier<Advancement.Builder, Void, DeserializationContext> targetedAdvancementModifier = TargetedModifier.deserialize(entry.getValue().getAsJsonObject(), "advancement", new DeserializationContext(resourcelocation, this.lootPredicateManager), AdvancementModifiers.REGISTRY, true);
				this.addModifiers(targetedAdvancementModifier.getTarget(), targetedAdvancementModifier.getConfiguredModifiers());
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				AbnormalsCore.LOGGER.error("Parsing error loading Advancement Modifier: {}", resourcelocation, jsonparseexception);
			}
		}
		AbnormalsCore.LOGGER.info("Advancement Modification Manager has loaded {} sets of modifiers", this.size());
	}
}
