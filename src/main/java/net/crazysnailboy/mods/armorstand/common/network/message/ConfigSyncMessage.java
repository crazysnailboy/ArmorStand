package net.crazysnailboy.mods.armorstand.common.network.message;

import io.netty.buffer.ByteBuf;
import net.crazysnailboy.mods.armorstand.ArmorStand;
import net.crazysnailboy.mods.armorstand.common.config.ModConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;


public class ConfigSyncMessage implements IMessage
{

	private boolean enableConfigGui = ModConfiguration.enableConfigGui;
	private boolean overrideEntityInteract = ModConfiguration.overrideEntityInteract;


	public ConfigSyncMessage()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.enableConfigGui = (ByteBufUtils.readVarShort(buf) == 1);
		this.overrideEntityInteract = (ByteBufUtils.readVarShort(buf) == 1);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarShort(buf, (this.enableConfigGui ? 1 : 0));
		ByteBufUtils.writeVarShort(buf, (this.overrideEntityInteract ? 1 : 0));
	}


	public static final class MessageHandler implements IMessageHandler<ConfigSyncMessage, IMessage>
	{

		private IThreadListener getThreadListener(MessageContext ctx)
		{
			try
			{
				if (ctx.side == Side.SERVER) return (WorldServer)ctx.getServerHandler().player.world;
				else if (ctx.side == Side.CLIENT) return Minecraft.getMinecraft();
				else return null;
			}
			catch (Exception ex)
			{
				ArmorStand.LOGGER.catching(ex);
				return null;
			}
		}

		@Override
		public IMessage onMessage(final ConfigSyncMessage message, MessageContext ctx)
		{
			IThreadListener threadListener = this.getThreadListener(ctx);
			threadListener.addScheduledTask(new Runnable()
			{

				@Override
				public void run()
				{
					ModConfiguration.enableConfigGui = message.enableConfigGui;
					ModConfiguration.overrideEntityInteract = message.overrideEntityInteract;
				}
			});

			return null;
		}
	}

}
