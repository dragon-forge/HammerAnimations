package org.zeith.hammeranims.standalone.procedures;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.IAnimationSource;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.standalone.IProcedure;
import org.zeith.hammeranims.standalone.contexts.impl.GeometryConsumer;
import org.zeith.hammeranims.standalone.jobs.*;
import org.zeith.hammeranims.standalone.jobs.impl.RenderGeometryJob;
import org.zeith.hammeranims.standalone.utils.AnimationState;
import org.zeith.hammeranims.standalone.utils.Args;
import shaded.json.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.zeith.hammeranims.standalone.ArgPredicate.*;
import static org.zeith.hammeranims.standalone.ArgTransformer.*;

public class ProcedureRenderAnimation
		implements IProcedure
{
	protected final File dumpFile;
	protected final File outputFile;
	
	protected final List<TexturedGeoJob> geometry;
	protected final AnimationState animations;
	
	@Getter
	protected RenderGeometryJob.GeometryBounds bounds;
	
	@Getter
	protected GeometryPose pose = new GeometryPose();
	
	protected final int fps;
	
	@SneakyThrows
	public ProcedureRenderAnimation(Args args)
	{
		HammerAnimations.init();
		
		dumpFile = new File(requireArg(args, "dump", ZIP_FILE));
		outputFile = new File(requireArg(args, "o", args.hasKey("f") ? WRITABLE_FILE : NON_EXISTING_FILE));
		fps = requireArg(args, "fps", POSITIVE_INTEGER);
		
		String character = requireArg(args, "c", STRING);
		JSONArray animations = requireArg(args, "a", JSON_ARRAY);
		
		HammerAnimations.LOG.info("Loading...");
		
		List<TexturedGeoJob> geometry = new ArrayList<>();
		List<IAnimationSource> animationSources = new ArrayList<>();
		
		try(ZipFile zf = new ZipFile(dumpFile))
		{
			//<editor-fold desc="Characters">
			var che = read(zf, "characters/" + character + ".json");
			if(che == null) problem("Character " + character + " not found in dump.");
			assert che != null;
			JSONObject o = (JSONObject) new JSONTokener(new String(che, StandardCharsets.UTF_8)).nextValue();
			JSONArray meshes = o.getJSONArray("meshes");
			for(int i = 0; i < meshes.length(); i++)
			{
				JSONObject mesh = meshes.getJSONObject(i);
				
				String geoID = mesh.getString("geo");
				
				byte[] geoBytes = read(zf, "geo/%s.json".formatted(geoID.replace(':', '/')));
				if(geoBytes == null)
					problem("Geometry " + geoID + " not found.");
				
				//noinspection DataFlowIssue
				IGeometryContainer geo = HammerAnimations.loadGeo(geoID, new String(geoBytes, StandardCharsets.UTF_8));
				if(!(geo.getGeometry() instanceof GeometryDataImpl))
					problem("Geometry " + geoID + " could not be decoded.");
				
				var txJob = mesh.getString("tex");
				
				var bb = RenderGeometryJob.GeometryBounds.of(geo.getGeometry().getMaterial());
				if(bounds == null) bounds = bb;
				else bounds = bb.max(bounds);
				
				RenderGeometryJob geoJob = new RenderGeometryJob(geo, this::getPose, this::getBounds, txJob);
				
				geometry.add(new TexturedGeoJob(txJob, geoJob));
			}
			//</editor-fold>
			
			for(int i = 0; i < animations.length(); i++)
			{
				JSONObject a = animations.getJSONObject(i);
				String animID = a.getString("a");
				
				animationSources.add(HammerAnimations.loadAnim(animID, readAnimation(zf, "animations/%s.json".formatted(animID))));
			}
		}
		
		this.geometry = List.copyOf(geometry);
//		this.animations = new AnimationState(animationSources.stream().distinct().toList());
		this.animations = null;
	}
	
	private String readAnimation(ZipFile file, String entry)
	{
		byte[] data = read(file, entry);
		if(data == null)
		{
			problem("Unable to find animation " + entry);
			return null;
		}
		
		return addAnimationSalt(new String(data, StandardCharsets.UTF_8));
	}
	
	public static String addAnimationSalt(String animation)
	{
		// HammerAnimations salt:
		return "{ \"format_version\": \"1.8.0\", \"animations\": { \"main\": %s } }"
				.formatted(animation);
	}
	
	@SneakyThrows
	private byte @Nullable [] read(ZipFile file, String entry)
	{
		ZipEntry ze = file.getEntry(entry);
		if(ze == null) return null;
		try(var in = file.getInputStream(ze))
		{
			return in.readAllBytes();
		}
	}
	
	@SneakyThrows
	@Override
	public void run()
	{
		HammerAnimations.LOG.info("Bounds: {}", bounds);
		HammerAnimations.LOG.info("Animations: {}", animations.getAnimationSystem().getLayers().length);
		HammerAnimations.LOG.info("Length: {} seconds. ({}Looping)", animations.expectedDuration, animations.loop ? "" : "Not ");
		
		var post = IRenderJobSource.start();
		
		var setup = IRenderJobSource.start()
				.then(IRenderJobSource.of(w -> w.context.put(GeometryConsumer.GEOMETRY_DUMP, new GeometryConsumer())));
		
		var render = IRenderJobSource.start();
		
		int frameCount = Math.max(1, (int) Math.ceil(fps * animations.expectedDuration));
		HammerAnimations.LOG.info("Frame count: {}", frameCount);
		
		for(int i = 0; i < frameCount; i++)
		{
			double progress = i / (double) frameCount;
			render.then(frame(progress * animations.expectedDuration));
		}
		
		IRenderJob.singleShot(setup, render, post).render(new GlWindow());
	}
	
	protected IRenderJobSource frame(double time)
	{
		var frame = IRenderJobSource.start()
				.then(w ->
				{
					w.context.get(GeometryConsumer.GEOMETRY_DUMP).reset();
					pose = animations.poseAt(time);
				});
		
		for(TexturedGeoJob geo : geometry)
			frame.then(geo.asJob());
		
		return frame;
	}
	
	public record TexturedGeoJob(String texture, RenderGeometryJob geo)
			implements IRenderJobSource
	{
		@Override
		public IRenderJob asJob()
		{
			return geo;
		}
	}
}