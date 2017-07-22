package net.crazysnailboy.mods.armorstand.client.init;

import org.lwjgl.input.Keyboard;
import net.crazysnailboy.mods.armorstand.ArmorStand;
import net.crazysnailboy.mods.armorstand.common.network.message.EntityFlagMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModKeyBindings
{

	private static final String CATEGORY_GENERAL = "general";

	public static final KeyBinding OPEN_ARMOR_STAND_GUI = createKeyBinding("openArmorStandGui", KeyConflictContext.IN_GAME, Keyboard.KEY_LSHIFT, CATEGORY_GENERAL);


	private static final KeyBinding createKeyBinding(String description, IKeyConflictContext keyConflictContext, int keyCode, String category)
	{
		return new KeyBinding(ArmorStand.MODID + ".key." + description, keyConflictContext, keyCode, ArmorStand.MODID + ".key.categories." + category);
	}


	public static void registerKeyBindings()
	{
		ClientRegistry.registerKeyBinding(OPEN_ARMOR_STAND_GUI);
	}


	@EventBusSubscriber
	public static class EventHandlers
	{

		private static boolean previousState = false;

		@SubscribeEvent
		public static void clientTick(final TickEvent.ClientTickEvent event)
		{
			if (event.phase != TickEvent.Phase.END) return;

			boolean currentState = OPEN_ARMOR_STAND_GUI.isKeyDown();
			if (currentState != previousState)
			{
				ArmorStand.NETWORK.sendToServer(new EntityFlagMessage(Minecraft.getMinecraft().player, 2, currentState));

				previousState = currentState;
			}

		}
	}

}
