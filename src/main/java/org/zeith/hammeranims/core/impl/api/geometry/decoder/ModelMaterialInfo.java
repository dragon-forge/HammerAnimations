package org.zeith.hammeranims.core.impl.api.geometry.decoder;

import lombok.Getter;
import shaded.util.math.Vec3d;
import shaded.json.JSONObject;
import org.zeith.hammeranims.core.utils.GsonHelper;

@Getter
public class ModelMaterialInfo
{
	private final int textureWidth;
	private final int textureHeight;
	
	private final double visibleBoundsWidth, visibleBoundsHeight;
	private final Vec3d visibleBoundsOffset;
	
	public ModelMaterialInfo(
			int textureWidth, int textureHeight,
			double visibleBoundsWidth, double visibleBoundsHeight,
			Vec3d visibleBoundsOffset
	)
	{
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.visibleBoundsWidth = visibleBoundsWidth;
		this.visibleBoundsHeight = visibleBoundsHeight;
		this.visibleBoundsOffset = visibleBoundsOffset;
	}
	
	public ModelMaterialInfo(JSONObject description)
	{
		this(
				GsonHelper.getAsInt(description, "texture_width"),
				GsonHelper.getAsInt(description, "texture_height"),
				
				GsonHelper.getAsDouble(description, "visible_bounds_width"),
				GsonHelper.getAsDouble(description, "visible_bounds_height"),
				
				new Vec3d(GsonHelper.getAsVec3f(description, "visible_bounds_offset"))
		);
	}
}