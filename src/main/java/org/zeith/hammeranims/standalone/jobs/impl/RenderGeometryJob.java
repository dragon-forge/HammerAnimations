package org.zeith.hammeranims.standalone.jobs.impl;

import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;
import org.zeith.hammeranims.core.impl.api.geometry.decoder.ModelMaterialInfo;
import org.zeith.hammeranims.core.utils.PoseStack;
import org.zeith.hammeranims.standalone.contexts.impl.GeometryConsumer;
import org.zeith.hammeranims.standalone.hammeranims.GatheringVertexOutput;
import org.zeith.hammeranims.standalone.jobs.GlWindow;
import org.zeith.hammeranims.standalone.jobs.IRenderJob;
import org.zeith.hammeranims.standalone.utils.math.MathHelper;
import shaded.util.math.Vec3d;

import java.util.List;
import java.util.function.Supplier;

public class RenderGeometryJob
		implements IRenderJob
{
	private final IGeometricModel model;
	private final Supplier<GeometryPose> pose;
	
	private final Supplier<GeometryBounds> bounds;
	
	private final RenderData render = new RenderData();
	
	private final String texture;
	
	public RenderGeometryJob(
			IGeometryContainer geom,
			Supplier<GeometryPose> pose,
			GeometryBounds bounds,
			String texture
	)
	{
		this(geom, pose, () -> bounds, texture);
	}
	
	public RenderGeometryJob(
			IGeometryContainer geom,
			Supplier<GeometryPose> pose,
			Supplier<GeometryBounds> bounds,
			String texture
	)
	{
		this.model = geom.createModel();
		this.bounds = bounds;
		this.pose = pose;
		this.texture = texture;
	}
	
	@Override
	public void render(GlWindow window)
	{
		PoseStack pose = render.pose;
		pose.reset();
		
		var bounds = this.bounds.get();
		var offset = bounds.visibleBoundsOffset();
		
		float size = Math.max(bounds.visibleBoundsWidth(), bounds.visibleBoundsHeight());
		float scale = 5F / size;
		
		pose.scale(scale, scale, scale);
		pose.translate(-offset.x, -offset.y, -offset.z);
		
		GatheringVertexOutput gatherer = new GatheringVertexOutput();
		render.renderer = gatherer;
		GeometryPose gp;
		if(this.pose != null && (gp = this.pose.get()) != null) model.applyPose(gp);
		else model.resetPose();
		model.getRoot().getRotation().add(0, MathHelper.TO_RAD_F * 180, 0);
		model.renderModel(render);
		var vtMap = gatherer.getVertices();
		
		GeometryConsumer d = window.context.get(GeometryConsumer.GEOMETRY_DUMP);
		if(d != null)
			for(VertexType vt : VertexType.values())
				d.dump(texture, vtMap.getOrDefault(vt, List.of()));
	}
	
	public record GeometryBounds(float visibleBoundsWidth, float visibleBoundsHeight, Vec3d visibleBoundsOffset)
	{
		public GeometryBounds max(GeometryBounds other)
		{
			if(other.visibleBoundsWidth * other.visibleBoundsHeight > visibleBoundsWidth * visibleBoundsHeight
			   || other.visibleBoundsOffset.length() > visibleBoundsOffset.length())
				return other;
			return this;
		}
		
		public static GeometryBounds of(ModelMaterialInfo mat)
		{
			return new GeometryBounds((float) mat.getVisibleBoundsWidth(), (float) mat.getVisibleBoundsHeight(), mat.getVisibleBoundsOffset());
		}
	}
}