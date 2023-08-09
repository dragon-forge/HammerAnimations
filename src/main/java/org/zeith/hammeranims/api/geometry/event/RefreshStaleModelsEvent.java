package org.zeith.hammeranims.api.geometry.event;

import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on CLIENT side when the geometries become stale, and should be refreshed.
 * In other words, this event is posted after all models are refreshed.
 */
public class RefreshStaleModelsEvent
		extends Event
{
}