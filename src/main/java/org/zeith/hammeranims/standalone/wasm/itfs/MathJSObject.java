package org.zeith.hammeranims.standalone.wasm.itfs;

import org.teavm.jso.*;

@JSClass(name = "mathjs")
public interface MathJSObject
		extends JSObject
{
	@JSProperty
	@JSExport
	double getPi();
	
	@JSExport
	double cos(double x);
	
	@JSExport
	double sin(double x);
	
	@JSExport
	double abs(double x);
	
	@JSExport
	double clamp(double value, double min, double max);
	
	@JSExport
	double pow(double base, double exponent);
	
	@JSExport
	double sqrt(double x);
	
	@JSExport
	double asin(double x);
	
	@JSExport
	double acos(double x);
	
	@JSExport
	double atan(double x);
	
	@JSExport
	double atan2(double y, double x);
	
	@JSExport
	double random(double low, double high);
	
	@JSExport
	int random_integer(int low, int high);
	
	@JSExport
	double ceil(double x);
	
	@JSExport
	double floor(double x);
	
	@JSExport
	double ln(double x);
	
	@JSExport
	double exp(double x);
	
	@JSExport
	double hermite_blend(double t);
	
	@JSExport
	double die_roll(int num, double low, double high);
	
	@JSExport
	int die_roll_integer(int num, int low, int high);
	
	@JSExport
	double round(double x);
	
	@JSExport
	int trunc(double value);
	
	@JSExport
	double mod(double value, double denominator);
	
	@JSExport
	double lerp(double a, double b, double O_to_1);
	
	@JSExport
	double max(double a, double b);
	
	@JSExport
	double min(double a, double b);
	
	@JSExport
	double max(double a, double b, double... extraValues);
	
	@JSExport
	double min(double a, double b, double... extraValues);
	
	@JSExport
	double max(double b);
	
	@JSExport
	double min(double b);
	
	@JSExport
	double min_angle(double value);
	
	@JSExport
	double lerprotate(double start, double end, double t);
}