package dev.zeith.lzvm.util;

@FunctionalInterface
public interface DoubleTernaryOperator
{
	double applyAsDouble(double a, double b, double c);
}