package org.zeith.hammeranims.standalone.wasm.itfs.character;

import org.teavm.jso.*;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.typedarrays.Float32Array;

@JSClass
public interface HAGeoBuffers
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
	JSArray<JSNumber> getMinBound();
	
	@JSProperty
	@JSExport
	JSArray<JSNumber> getMaxBound();
	
	@JSProperty
	@JSExport
	int getVertexCount();
	
	@JSExport
	void resetPose();
	
	@JSExport
	void updatePose(Object pose);
	
	@JSExport
	void flush();
}