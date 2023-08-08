package org.zeith.hammer.models.api.animation.data;

import com.zeitheron.hammercore.lib.zlib.json.JSONObject;
import net.minecraft.util.math.Vec3d;
import org.zeith.hammer.models.api.animation.interp.*;

import javax.annotation.*;

public class BoneAnimation
{
	public final @Nonnull Vec3Animation rotation;
	public final @Nonnull Vec3Animation position;
	public final @Nonnull Vec3Animation scale;
	
	public BoneAnimation(@Nullable Vec3Animation rotation, @Nullable Vec3Animation position, @Nullable Vec3Animation scale)
	{
		this.rotation = rotation != null ? rotation : Vec3Animation.ZERO;
		this.position = position != null ? position : Vec3Animation.ZERO;
		this.scale = scale != null ? scale : Vec3Animation.ONE;
	}
	
	public Vec3d getTranslation(Query query)
	{
		return position.get(query);
	}
	
	public Vec3d getScale(Query query)
	{
		return scale.get(query);
	}
	
	public Vec3d getRotation(Query query)
	{
		return rotation.get(query);
	}
	
	public static BoneAnimation parse(JSONObject bone)
	{
		Vec3Animation rotation = Vec3Animation.parse(bone.opt("rotation"));
		Vec3Animation position = Vec3Animation.parse(bone.opt("position"));
		Vec3Animation scale = Vec3Animation.parse(bone.opt("scale"));
		if(rotation == null && position == null && scale == null) return null;
		return new BoneAnimation(rotation, position, scale);
	}
	
	@Override
	public String toString()
	{
		return "BoneAnimation{" +
				"rotation=" + rotation +
				", position=" + position +
				", scale=" + scale +
				'}';
	}
}