package com.ewpratten.client_ping;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class ModEntrypoint implements ModInitializer {
	public static final Identifier soundId = new Identifier("client_ping", "ping_sound");
	public static final Identifier soundId2 = new Identifier("client_ping", "ping_sound2");
	public static final SoundEvent soundEvent = SoundEvent.createFixedRangeEvent(soundId, 1.0F);
	public static final SoundEvent soundEvent2 = SoundEvent.createFixedRangeEvent(soundId2, 1.0F);

	@Override
	public void onInitialize(ModContainer mod) {
		Globals.LOGGER.info("Mod Loaded. Today will be a great day!");
		Registry.register(Registries.SOUND_EVENT, soundId, soundEvent);
		Registry.register(Registries.SOUND_EVENT, soundId2, soundEvent2);
	}

}
