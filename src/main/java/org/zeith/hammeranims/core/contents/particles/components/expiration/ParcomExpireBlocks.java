package org.zeith.hammeranims.core.contents.particles.components.expiration;

import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class ParcomExpireBlocks
		implements IParticleComponent
{
	public List<Block> blocks = new ArrayList<>();
	private BlockPos.Mutable pos = new BlockPos.Mutable();
	
	public ParcomExpireBlocks(JsonElement element)
	{
		if(!element.isJsonArray()) return;
		for(JsonElement value : element.getAsJsonArray())
		{
			ResourceLocation location = InstanceHelpers.tryParseLocation(value.getAsString());
			Block block = ForgeRegistries.BLOCKS.getValue(location);
			if(block != null) this.blocks.add(block);
		}
	}
	
	public Block getBlock(ParticleEmitter emitter, BedrockParticle particle)
	{
		if(emitter.world == null) return Blocks.AIR;
		Vector3d position = particle.getGlobalPosition(emitter);
		this.pos.set(position.x(), position.y(), position.z());
		return emitter.world.getBlockState(this.pos).getBlock();
	}
}