package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.world.modification.HasModdedBiomeSource;
import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(SurfaceSystem.class)
public final class SurfaceSystemMixin implements HasModdedBiomeSource {
	@Unique
	@Nullable
	private ModdedBiomeSource moddedBiomeSource;

	public void setModdedBiomeSource(@Nullable ModdedBiomeSource moddedBiomeSource) {
		this.moddedBiomeSource = moddedBiomeSource;
	}

	@Override
	@Nullable
	public ModdedBiomeSource getModdedBiomeSource() {
		return this.moddedBiomeSource;
	}
}
