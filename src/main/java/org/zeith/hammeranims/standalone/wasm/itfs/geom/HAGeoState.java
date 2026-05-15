package org.zeith.hammeranims.standalone.wasm.itfs.geom;

import org.teavm.jso.*;
import org.teavm.jso.typedarrays.Float32Array;
import org.zeith.hammeranims.standalone.wasm.itfs.anim.HAAnimationPose;

@JSClass(name = "GeometryState")
public interface HAGeoState
{
	@JSProperty
	@JSExport
	Float32Array getVertices();
	
	@JSProperty
	@JSExport
	Float32Array getUvs();
	
	@JSProperty
	@JSExport
	Float32Array getNormals();
	
	@JSProperty
	@JSExport
	JSObject getMinBound();
	
	@JSProperty
	@JSExport
	JSObject getMaxBound();
	
	@JSProperty
	@JSExport
	int getVertexCount();
	
	@JSExport
	void resetPose();
	
	@JSExport
	void updatePose(HAAnimationPose pose);
	
	@JSExport
	void flush();
}