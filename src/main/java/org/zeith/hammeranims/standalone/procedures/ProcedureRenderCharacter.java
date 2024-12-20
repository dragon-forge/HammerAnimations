package org.zeith.hammeranims.standalone.procedures;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.standalone.IProcedure;
import org.zeith.hammeranims.standalone.contexts.impl.GeometryConsumer;
import org.zeith.hammeranims.standalone.jobs.*;
import org.zeith.hammeranims.standalone.jobs.impl.RenderGeometryJob;
import org.zeith.hammeranims.standalone.procedures.ProcedureRenderAnimation.TexturedGeoJob;
import org.zeith.hammeranims.standalone.utils.Args;
import shaded.json.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.zeith.hammeranims.standalone.ArgPredicate.*;
import static org.zeith.hammeranims.standalone.ArgTransformer.POSITIVE_INTEGER;

public class ProcedureRenderCharacter
		implements IProcedure
{
	protected final File dumpFile;
	protected final File outputFile;
	
	protected final List<TexturedGeoJob> geometry;
	
	@Getter
	protected RenderGeometryJob.GeometryBounds bounds;
	
	@Getter
	protected GeometryPose pose = new GeometryPose();
	
	protected final int size, ds;
	
	protected final String character;
	
	@SneakyThrows
	public ProcedureRenderCharacter(Args args)
	{
		HammerAnimations.init();
		
		dumpFile = new File(requireArg(args, "dump", ZIP_FILE));
		outputFile = new File(requireArg(args, "o", args.hasKey("f") ? WRITABLE_FILE : NON_EXISTING_FILE));
		size = requireArg(args, "s", POSITIVE_INTEGER);
		ds = requireArg(args, "ds", POSITIVE_INTEGER);
		
		character = requireArg(args, "c", STRING);
		
		HammerAnimations.LOG.info("Loading...");
		
		List<TexturedGeoJob> geometry = new ArrayList<>();
		
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
		}
		
		this.geometry = List.copyOf(geometry);
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
		HammerAnimations.LOG.info("Character: {}", character);
		
		var post = IRenderJobSource.start();
		
		var setup = IRenderJobSource.start()
				.then(IRenderJobSource.of(w -> w.context.put(GeometryConsumer.GEOMETRY_DUMP, new GeometryConsumer())));
		
		var render = IRenderJobSource.start().then(frame());
		
		IRenderJob.singleShot(setup, render, post).render(new GlWindow());
	}
	
	protected IRenderJobSource frame()
	{
		var frame = IRenderJobSource.start()
				.then(w -> w.context.get(GeometryConsumer.GEOMETRY_DUMP).reset());
		
		for(TexturedGeoJob geo : geometry)
			frame.then(geo.asJob());
		
		return frame;
	}
}