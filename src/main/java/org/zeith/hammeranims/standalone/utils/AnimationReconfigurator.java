package org.zeith.hammeranims.standalone.utils;

import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;

import java.util.function.UnaryOperator;

public record AnimationReconfigurator(
		Float weight,
		Float speed,
		Float startTime,
		Float freezeAt,
		Float transitionTime,
		Boolean reversed
)
		implements UnaryOperator<ConfiguredAnimation>
{
	@Override
	public ConfiguredAnimation apply(ConfiguredAnimation ca)
	{
		if(weight != null) ca = ca.weight(weight);
		if(speed != null) ca = ca.speed(speed);
		if(startTime != null) ca = ca.startTime(startTime);
		if(freezeAt != null) ca = ca.freezeAt(freezeAt);
		if(transitionTime != null) ca = ca.transitionTime(transitionTime);
		if(reversed != null) ca = ca.reversed(reversed);
		return ca;
	}
}