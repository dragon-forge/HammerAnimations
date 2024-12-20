package org.zeith.hammeranims.api.geometry.event;

import shaded.event.Event;

/**
 * This event is fired when the geometries become stale, and should be refreshed.
 * In other words, this event is posted after all models are refreshed.
 */
public class RefreshStaleModelsEvent
		extends Event
{
}