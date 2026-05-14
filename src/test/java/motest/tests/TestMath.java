package motest.tests;

import org.junit.jupiter.api.Test;

import static motest.util.LzTestRunner.*;

/**
 * Test all math functions from
 * <a href="https://bedrock.dev/docs/stable/Molang">bedrock.dev</a>
 */
public class TestMath
{
	@Test
	public void abs()
	{
		// Absolute value of value
		runTrue("math.abs(-1) == 1");
		runTrue("math.abs(0) == 0");
		runTrue("math.abs(1) == 1");
	}
	
	@Test
	public void acos()
	{
		// arccos of value
		runTrue("math.acos(-1) == 180");
		runTrue("math.acos(0) == 90");
		runTrue("math.acos(1) == 0");
	}
	
	@Test
	public void asin()
	{
		// arcsin of value
		runTrue("math.asin(-1) == -90");
		runTrue("math.asin(0) == 0");
		runTrue("math.asin(1) == 90");
	}
	
	@Test
	public void atan()
	{
		// arctan of value
		runTrue("math.atan(-1) == -45");
		runTrue("math.atan(0) == 0");
		runTrue("math.atan(1) == 45");
	}
	
	@Test
	public void atan2()
	{
		// arctan of y/x.
		runTrue("math.atan2(0, -1) == 180");
		runTrue("math.atan2(0, -0.5) == 180");
		runTrue("math.atan2(0, 0.5) == 0");
		runTrue("math.atan2(0, 1) == 0");
		
		runTrue("math.atan2(0, 0) == 0");
		
		runTrue("math.atan2(1, 0) == 90");
		runTrue("math.atan2(0.5, 0) == 90");
		runTrue("math.atan2(-0.5, 0) == -90");
		runTrue("math.atan2(-1, 0) == -90");
		
		runTrue("math.atan2(1, 1) == 45");
		runTrue("math.atan2(1, -1) == 135");
		runTrue("math.atan2(-1, -1) == -135");
		runTrue("math.atan2(-1, 1) == -45");
	}
	
	@Test
	public void ceil()
	{
		// Round value up to nearest integral number
		runTrue("math.ceil(-1.8) == -1");
		runTrue("math.ceil(-1.1) == -1");
		runTrue("math.ceil(-1) == -1");
		runTrue("math.ceil(-0.1) == 0");
		runTrue("math.ceil(0) == 0");
		runTrue("math.ceil(0.1) == 1");
		runTrue("math.ceil(0.99) == 1");
	}
	
	@Test
	public void clamp()
	{
		// Clamp value to between min and max inclusive
		runTrue("math.clamp(0, 0, 1) == 0");
		runTrue("math.clamp(-10, 0, 1) == 0");
		runTrue("math.clamp(0, 0, 1) == 0");
		runTrue("math.clamp(0.5, 0, 1) == 0.5");
		runTrue("math.clamp(2.5, 0, 1) == 1");
		runTrue("math.clamp(1, 0, 1) == 1");
	}
	
	@Test
	public void copy_sign()
	{
		// Clamp value to between min and max inclusive
		runTrue("math.copy_sign(-14.6, 10) == 14.6");
		runTrue("math.copy_sign(-14.6, -10) == -14.6");
		runTrue("math.copy_sign(14.6, 10) == 14.6");
		runTrue("math.copy_sign(14.6, -10) == -14.6");
	}
	
	@Test
	public void cos()
	{
		// Cosine (in degrees) of value
		runTrue("math.cos(0) == 1");
		runTrue("math.cos(90) >= 0 && math.cos(90) < 0.0001");
		runTrue("math.cos(180) == -1");
		runTrue("math.cos(270) > -0.0001 && math.cos(270) <= 0");
		runTrue("math.cos(360) == 1");
	}
	
	@Test
	public void die_roll()
	{
		// returns the sum of 'num' random numbers, each with a value from low to high`. Note: the generated random numbers are not integers like normal dice. For that, use `math.die_roll_integer`.
		for(int i = 0; i < 16; i++)
		{
			runTrue("t.var = math.die_roll(1, 1, 6); t.var >= 1 && t.var < 6");
		}
	}
	
	@Test
	public void die_roll_integer()
	{
		// returns the sum of 'num' random integer numbers, each with a value from low to high`. Note: the generated random numbers are integers like normal dice.
		for(int i = 0; i < 16; i++)
		{
			runTrue("t.var = math.die_roll_integer(1, 1, 6); t.var >= 1 && t.var <= 6");
		}
	}
	
	@Test
	public void ease_in_back()
	{
		// Output goes from start to end via 0_to_1, overshooting backward before accelerating into the end
		runTrue("math.ease_in_back(0, 1, 0) == 0");
		runLog("math.ease_in_back(0, 1, 0.5)");
		runTrue("math.ease_in_back(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_bounce()
	{
		// Output goes from start to end via 0_to_1, starting with bounce oscillations and settling into the end
		runTrue("math.ease_in_bounce(0, 1, 0) == 0");
		runLog("math.ease_in_bounce(0, 1, 0.5)");
		runTrue("math.ease_in_bounce(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_circ()
	{
		// Output goes from start to end via 0_to_1, starting slow and accelerating along a circular curve toward the end
		runTrue("math.ease_in_circ(0, 1, 0) == 0");
		runLog("math.ease_in_circ(0, 1, 0.5)");
		runTrue("math.ease_in_circ(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_cubic()
	{
		// Output goes from start to end via 0_to_1, starting slow and accelerating rapidly toward the end
		runTrue("math.ease_in_cubic(0, 1, 0) == 0");
		runLog("math.ease_in_cubic(0, 1, 0.5)");
		runTrue("math.ease_in_cubic(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_elastic()
	{
		// Output goes from start to end via 0_to_1, starting with elastic oscillations before accelerating into the end
		runTrue("math.ease_in_elastic(0, 1, 0) == 0");
		runLog("math.ease_in_elastic(0, 1, 0.5)");
		runTrue("math.ease_in_elastic(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_expo()
	{
		// Output goes from start to end via 0_to_1, starting slow and accelerating extremely rapidly toward the end
		runTrue("math.ease_in_expo(0, 1, 0) == 0");
		runLog("math.ease_in_expo(0, 1, 0.5)");
		runTrue("math.ease_in_expo(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_back()
	{
		// Output goes from start to end via 0_to_1, overshooting at both start and end, with smoother change in the middle
		runTrue("math.ease_in_out_back(0, 1, 0) == 0");
		runLog("math.ease_in_out_back(0, 1, 0.5)");
		runTrue("math.ease_in_out_back(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_bounce()
	{
		// Output goes from start to end via 0_to_1, starting and ending with bounce oscillations, smoother in the middle
		runTrue("math.ease_in_out_bounce(0, 1, 0) == 0");
		runLog("math.ease_in_out_bounce(0, 1, 0.5)");
		runTrue("math.ease_in_out_bounce(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_circ()
	{
		// Output goes from start to end via 0_to_1, starting and ending slow, with circular acceleration and deceleration in the middle
		runTrue("math.ease_in_out_circ(0, 1, 0) == 0");
		runLog("math.ease_in_out_circ(0, 1, 0.5)");
		runTrue("math.ease_in_out_circ(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_cubic()
	{
		// Output goes from start to end via 0_to_1, starting slow, accelerating rapidly in the middle, then slowing again at the end
		runTrue("math.ease_in_out_cubic(0, 1, 0) == 0");
		runLog("math.ease_in_out_cubic(0, 1, 0.5)");
		runTrue("math.ease_in_out_cubic(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_elastic()
	{
		// Output goes from start to end via 0_to_1, oscillating elastically at both start and end, with stable change in the middle
		runTrue("math.ease_in_out_elastic(0, 1, 0) == 0");
		runLog("math.ease_in_out_elastic(0, 1, 0.5)");
		runTrue("math.ease_in_out_elastic(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_expo()
	{
		// Output goes from start to end via 0_to_1, starting and ending slow, with extremely rapid change in the middle
		runTrue("math.ease_in_out_expo(0, 1, 0) == 0");
		runLog("math.ease_in_out_expo(0, 1, 0.5)");
		runTrue("math.ease_in_out_expo(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_quad()
	{
		// Output goes from start to end via 0_to_1, starting slow, accelerating in the middle, then slowing again at the end
		runTrue("math.ease_in_out_quad(0, 1, 0) == 0");
		runLog("math.ease_in_out_quad(0, 1, 0.5)");
		runTrue("math.ease_in_out_quad(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_quart()
	{
		// Output goes from start to end via 0_to_1, starting slow, accelerating very rapidly in the middle, then slowing again at the end
		runTrue("math.ease_in_out_quart(0, 1, 0) == 0");
		runLog("math.ease_in_out_quart(0, 1, 0.5)");
		runTrue("math.ease_in_out_quart(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_quint()
	{
		// Output goes from start to end via 0_to_1, starting slow, accelerating extremely rapidly in the middle, then slowing again at the end
		runTrue("math.ease_in_out_quint(0, 1, 0) == 0");
		runLog("math.ease_in_out_quint(0, 1, 0.5)");
		runTrue("math.ease_in_out_quint(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_out_sine()
	{
		// Output goes from start to end via 0_to_1, starting and ending slow, with smoother change in the middle
		runTrue("math.ease_in_out_sine(0, 1, 0) == 0");
		runLog("math.ease_in_out_sine(0, 1, 0.5)");
		runTrue("math.ease_in_out_sine(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_quad()
	{
		// Output goes from start to end via 0_to_1, starting slow and accelerating toward the end
		runTrue("math.ease_in_quad(0, 1, 0) == 0");
		runLog("math.ease_in_quad(0, 1, 0.5)");
		runTrue("math.ease_in_quad(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_quart()
	{
		// Output goes from start to end via 0_to_1, starting slow and accelerating very rapidly toward the end
		runTrue("math.ease_in_quart(0, 1, 0) == 0");
		runLog("math.ease_in_quart(0, 1, 0.5)");
		runTrue("math.ease_in_quart(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_quint()
	{
		// Output goes from start to end via 0_to_1, starting slow and accelerating extremely rapidly toward the end
		runTrue("math.ease_in_quint(0, 1, 0) == 0");
		runLog("math.ease_in_quint(0, 1, 0.5)");
		runTrue("math.ease_in_quint(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_in_sine()
	{
		// Output goes from start to end via 0_to_1, starting slow and accelerating smoothly toward the end
		runTrue("math.ease_in_sine(0, 1, 0) == 0");
		runLog("math.ease_in_sine(0, 1, 0.5)");
		runTrue("math.abs(math.ease_in_sine(0, 1, 1) - 1) < 0.0001");
	}
	
	@Test
	public void ease_out_back()
	{
		// Output goes from start to end via 0_to_1, overshooting past the end before settling into it
		runTrue("math.ease_out_back(0, 1, 0) == 0");
		runLog("math.ease_out_back(0, 1, 0.5)");
		runTrue("math.ease_out_back(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_out_bounce()
	{
		// Output goes from start to end via 0_to_1, approaching the end with bounce oscillations that diminish over time
		runTrue("math.ease_out_bounce(0, 1, 0) == 0");
		runLog("math.ease_out_bounce(0, 1, 0.5)");
		runTrue("math.ease_out_bounce(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_out_circ()
	{
		// Output goes from start to end via 0_to_1, starting fast and decelerating along a circular curve toward the end
		runTrue("math.ease_out_circ(0, 1, 0) == 0");
		runLog("math.ease_out_circ(0, 1, 0.5)");
		runTrue("math.ease_out_circ(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_out_cubic()
	{
		// Output goes from start to end via 0_to_1, starting fast and decelerating rapidly toward the end
		runTrue("math.ease_out_cubic(0, 1, 0) == 0");
		runLog("math.ease_out_cubic(0, 1, 0.5)");
		runTrue("math.ease_out_cubic(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_out_elastic()
	{
		// Output goes from start to end via 0_to_1, overshooting the end with elastic oscillations before settling
		runTrue("math.ease_out_elastic(0, 1, 0) == 0");
		runLog("math.ease_out_elastic(0, 1, 0.5)");
		runTrue("math.ease_out_elastic(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_out_expo()
	{
		// Output goes from start to end via 0_to_1, starting extremely fast and decelerating gradually toward the end
		runTrue("math.ease_out_expo(0, 1, 0) == 0");
		runLog("math.ease_out_expo(0, 1, 0.5)");
		runTrue("math.ease_out_expo(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_out_quad()
	{
		// Output goes from start to end via 0_to_1, starting fast and decelerating toward the end
		runTrue("math.ease_out_quad(0, 1, 0) == 0");
		runLog("math.ease_out_quad(0, 1, 0.5)");
		runTrue("math.ease_out_quad(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_out_quart()
	{
		// Output goes from start to end via 0_to_1, starting fast and decelerating very rapidly toward the end
		runTrue("math.ease_out_quart(0, 1, 0) == 0");
		runLog("math.ease_out_quart(0, 1, 0.5)");
		runTrue("math.ease_out_quart(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_out_quint()
	{
		// Output goes from start to end via 0_to_1, starting fast and decelerating extremely rapidly toward the end
		runTrue("math.ease_out_quint(0, 1, 0) == 0");
		runLog("math.ease_out_quint(0, 1, 0.5)");
		runTrue("math.ease_out_quint(0, 1, 1) == 1");
	}
	
	@Test
	public void ease_out_sine()
	{
		// Output goes from start to end via 0_to_1, starting fast and decelerating smoothly toward the end
		runTrue("math.ease_out_sine(0, 1, 0) == 0");
		runLog("math.ease_out_sine(0, 1, 0.5)");
		runTrue("math.ease_out_sine(0, 1, 1) == 1");
	}
	
	@Test
	public void exp()
	{
		// Calculates e to the value'th power
		runTrue("math.exp(0) == 1");
		runTrue("math.abs(math.exp(1) - math.e) < 0.0001");
	}
	
	@Test
	public void floor()
	{
		// Round value down to nearest integral number
		runTrue("math.floor(-1.8) == -2");
		runTrue("math.floor(-1.1) == -2");
		runTrue("math.floor(-1) == -1");
		runTrue("math.floor(-0.1) == -1");
		runTrue("math.floor(0) == 0");
		runTrue("math.floor(0.1) == 0");
		runTrue("math.floor(0.99) == 0");
		runTrue("math.floor(1) == 1");
		runTrue("math.floor(1.5) == 1");
	}
	
	@Test
	public void hermite_blend()
	{
		// Useful for simple smooth curve interpolation using one of the Hermite Basis functions: `3t^2 - 2t^3`. Note that while any valid float is a valid input, this function works best in the range [0,1].
		runTrue("math.hermite_blend(0) == 0");
		runTrue("math.hermite_blend(1) == 1");
	}
	
	@Test
	public void inverse_lerp()
	{
		// Returns the normalized progress between start and end given value
		runTrue("math.inverse_lerp(0, 10, 5) == 0.5");
		runTrue("math.inverse_lerp(0, 10, 0) == 0");
		runTrue("math.inverse_lerp(0, 10, 10) == 1");
		runTrue("math.inverse_lerp(10, 20, 15) == 0.5");
		runTrue("math.inverse_lerp(0, 10, 20) == 2");
		runTrue("math.inverse_lerp(0, 10, -5) == -0.5");
	}
	
	@Test
	public void lerp()
	{
		// Lerp from start to end via 0_to_1
		runTrue("math.lerp(0, 100, 0) == 0");
		runTrue("math.lerp(0, 100, 0.1) == 10");
		runTrue("math.lerp(0, 100, 0.25) == 25");
		runTrue("math.lerp(0, 100, 1) == 100");
		runTrue("math.lerp(-100, 100, 0.5) == 0");
	}
	
	@Test
	public void lerprotate()
	{
		// Lerp the shortest direction around a circle from start degrees to end degrees via 0_to_1
		runTrue("math.lerprotate(350, 10, 0.5) == 0");
		runTrue("math.lerprotate(350, 10, 1) == 10");
		runTrue("math.lerprotate(350, 10, 0) == 350");
		runTrue("math.lerprotate(180, 0, 0.5) == 90");
		runTrue("math.lerprotate(0, 180, 0.5) == 90");
	}
	
	@Test
	public void ln()
	{
		// Natural logarithm of value
		runTrue("math.ln(1) == 0");
		runTrue("math.ln(math.exp(1)) == 1");
		runTrue("math.ln(math.exp(100)) == 100");
	}
	
	@Test
	public void max()
	{
		// Return highest value of A or B
		runTrue("math.max(0, 1) == 1");
		runTrue("math.max(1, 0) == 1");
		runTrue("math.max(0, 0) == 0");
		runTrue("math.max(-1, 0) == 0");
		runTrue("math.max(0, -1) == 0");
	}
	
	@Test
	public void min()
	{
		// Return lowest value of A or B
		runTrue("math.min(0, 1) == 0");
		runTrue("math.min(1, 0) == 0");
		runTrue("math.min(0, 0) == 0");
		runTrue("math.min(-1, 0) == -1");
		runTrue("math.min(0, -1) == -1");
	}
	
	@Test
	public void min_angle()
	{
		// Minimize angle magnitude (in degrees) into the range [-180, 180)
		runTrue("math.min_angle(-180) == -180");
		runTrue("math.min_angle(0) == 0");
		runTrue("math.min_angle(45) == 45");
		runTrue("math.min_angle(90) == 90");
		runTrue("math.min_angle(180) == -180");
	}
	
	@Test
	public void mod()
	{
		// Return the remainder of value / denominator
		runTrue("math.mod(0, 2) == 0");
		runTrue("math.mod(1, 2) == 1");
		runTrue("math.mod(1.5, 2) == 1.5");
		runTrue("math.mod(2, 2) == 0");
		runTrue("math.mod(2.5, 2) == 0.5");
		runTrue("math.mod(-1, 2) == -1");
		
		// MoLang++
		runTrue("0 % 2 == 0");
		runTrue("1 % 2 == 1");
		runTrue("1.5 % 2 == 1.5");
		runTrue("2 % 2 == 0");
		runTrue("2.5 % 2 == 0.5");
	}
	
	@Test
	public void pi()
	{
		// Returns the float representation of the constant pi.
		runTrue("math.round(math.pi * 1000) == 3142");
	}
	
	// MoLang++
	@Test
	public void e()
	{
		// Returns the float representation of the constant e.
		runTrue("math.round(math.e * 1000) == 2718");
	}
	
	@Test
	public void pow()
	{
		// Elevates `base` to the `exponent`'th power
		runTrue("math.pow(1, 1) == 1");
		runTrue("math.pow(1, 2) == 1");
		runTrue("math.pow(2, 1) == 2");
		runTrue("math.pow(2, 2) == 4");
		runTrue("math.pow(2, -2) == 1 / math.pow(2, 2)");
	}
	
	@Test
	public void random()
	{
		// Random value between low and high inclusive
		for(int i = 0; i < 100; i++)
		{
			runTrue("v.test = math.random(0, 10); v.test >= 0 && v.test <= 10");
		}
	}
	
	@Test
	public void random_integer()
	{
		// Random integer value between low and high inclusive
		for(int i = 0; i < 100; i++)
		{
			runTrue("v.test = math.random_integer(0, 10); v.test >= 0 && v.test <= 10 && v.test % 1 == 0");
		}
	}
	
	@Test
	public void round()
	{
		// Round value to nearest integral number
		runTrue("math.round(-1) == -1");
		runTrue("math.round(-0.75) == -1");
		runTrue("math.round(-0.5) == 0");
		runTrue("math.round(-0.4) == 0");
		runTrue("math.round(0) == 0");
		runTrue("math.round(0.25) == 0");
		runTrue("math.round(0.5) == 1");
		runTrue("math.round(0.75) == 1");
	}
	
	@Test
	public void sign()
	{
		// Returns 1 if value is positive, -1 otherwise
		runTrue("math.sign(-1) == -1");
		runTrue("math.sign(-100) == -1");
		runTrue("math.sign(0) == -1");
		runTrue("math.sign(1) == 1");
		runTrue("math.sign(2002) == 1");
	}
	
	@Test
	public void sin()
	{
		// Sine (in degrees) of value
		runTrue("math.sin(0) == 0");
		runTrue("math.sin(90) == 1");
		runTrue("math.sin(180) >= 0 && math.sin(180) < 0.0001");
		runTrue("math.sin(270) == -1");
		runTrue("math.sin(360) > -0.0001 && math.sin(360) <= 0");
	}
	
	@Test
	public void sqrt()
	{
		// Square root of value
		runTrue("math.sqrt(4) == 2");
		runTrue("math.sqrt(1) == 1");
		runTrue("math.sqrt(math.pow(math.exp(1), 2)) == math.exp(1)");
	}
	
	@Test
	public void trunc()
	{
		// Round value towards zero
		runTrue("math.trunc(-1) == -1");
		runTrue("math.trunc(-0.75) == 0");
		runTrue("math.trunc(-0.5) == 0");
		runTrue("math.trunc(-0.4) == 0");
		runTrue("math.trunc(0) == 0");
		runTrue("math.trunc(0.25) == 0");
		runTrue("math.trunc(0.5) == 0");
		runTrue("math.trunc(0.75) == 0");
		runTrue("math.trunc(1) == 1");
	}
}