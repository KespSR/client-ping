package com.ewpratten.client_ping.mixin;

import com.ewpratten.client_ping.ModEntrypoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatMessageTag;
import net.minecraft.client.sound.Sound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.network.message.MessageSignature;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

import net.minecraft.util.ModStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.logic.Ping;
import com.ewpratten.client_ping.logic.PingRegistry;

@Mixin(ChatHud.class)
public class InGameHudMixin {
	MinecraftClient player = MinecraftClient.getInstance();
	SoundEvent sound;
	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignature;Lnet/minecraft/client/gui/hud/ChatMessageTag;)V", cancellable = true)
	public void addMessage(Text message, MessageSignature signature, ChatMessageTag tag, CallbackInfo info) {

		// Get as a regular string
		String messageString = message.getString();
		String[] parts = messageString.split(" ", 3);

		if (parts.length < 3) {
			return; // No es el formato esperado, retorna temprano
		}
		String chatBody = "";
		String username = "";
		if(parts[0].startsWith("[War]")){
			String city2 = "";
			boolean matchFound = false;
			String[] cities = {"Catalan", "Murcia", "Balearic_Islands", "Vascongadas", "Leon", "Seville", "Madrid", "Andalusia", "Havana", "Santiago", "Manile"};
			String[] parts2 = messageString.split(" ");
			for (String city : cities){
				if (city.equals(parts2[4])) {
					city2 = city;
					matchFound = true;
					break;
				}
			}
			if (matchFound){
				String coordinatesPart = messageString.split("at ")[1];
				coordinatesPart = coordinatesPart.replaceAll("[()]", "");
				String[] coordinates = coordinatesPart.split(", ");
				chatBody = String.format("Ping at {minecraft:overworld:(%s, %s, %s)}",
					coordinates[0].trim(),
					coordinates[1].trim(),
					coordinates[2].trim());
				username = city2;
				sound = ModEntrypoint.soundEvent2;
			}
		}
		else {
			username = parts[1].endsWith(":") ? parts[1].substring(0, parts[1].length() - 1) : parts[1];

// El cuerpo del mensaje es la tercera parte después de dividir por el segundo espacio
			chatBody = parts[2];
			sound = ModEntrypoint.soundEvent;


		}
		Ping parseResult = Ping.deserialize(chatBody, username);
		if (parseResult == null) {
			return;
		}
		else {
			player.player.playSound(sound, SoundCategory.PLAYERS, 1.0F,1.0F);
		}


// El nombre de usuario estaría en la segunda parte, después de omitir el prefijo [Town]/[Nation]
// Además, eliminamos los dos puntos al final si están presentes

		// If the config says to hide ping messages, cancel the event
		if (!Globals.CONFIG.showPingsInChat()) {
			info.cancel();
		}

		// Ignore messages from the current player
		MinecraftClient mc = MinecraftClient.getInstance();
		if (username.equals(mc.player.getName().getString())) {
			Globals.LOGGER.info("Dropping ping message from self");
			return;
		}

		// We have a ping message.
		Globals.LOGGER.info("Received ping message from " + username + " at " + parseResult.position().toString());

		// Store in the registry
		PingRegistry.getInstance().register(parseResult);

	}

}
