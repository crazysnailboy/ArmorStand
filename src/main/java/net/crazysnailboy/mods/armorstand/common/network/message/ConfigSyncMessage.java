package net.crazysnailboy.mods.armorstand.common.network.message;

import io.netty.buffer.ByteBuf;
import net.crazysnailboy.mods.armorstand.ArmorStand;
import net.crazysnailboy.mods.armorstand.common.config.ModConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;


public class ConfigSyncMessage implements IMessage
{

	private boolean enableConfigGui = ModConfiguration.enableConfigGui;
	private boolean overrideEntityInteract = ModConfiguration.overrideEntityInteract;
	private boolean enableNameTags = ModConfiguration.enableNameTags;


	public ConfigSyncMessage()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.enableConfigGui = buf.readBoolean();
		this.overrideEntityInteract = buf.readBoolean();
		this.enableNameTags = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(this.enableConfigGui);
		buf.writeBoolean(this.overrideEntityInteract);
		buf.writeBoolean(this.enableNameTags);
	}


	public static final class MessageHandler implements IMessageHandler<ConfigSyncMessage, IMessage>
	{

		private IThreadListener getThreadListener(MessageContext ctx)
		{
			try
			{
				if (ctx.side == Side.SERVER) return (WorldServer)ctx.getServerHandler().playerEntity.world;
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
					ModConfiguration.enableNameTags = message.enableNameTags;
				}
			});

			return null;
		}
	}

}
