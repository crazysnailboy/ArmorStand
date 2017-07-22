package net.crazysnailboy.mods.armorstand.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class ArmorStandSyncMessage implements IMessage
{

	private int entityId;
	private NBTTagCompound entityTag;

	public ArmorStandSyncMessage()
	{
	}

	public ArmorStandSyncMessage(int entityId, NBTTagCompound entityTag)
	{
		this.entityId = entityId;
		this.entityTag = entityTag;
	}


	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entityId = ByteBufUtils.readVarInt(buf, 4);
		this.entityTag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, this.entityId, 4);
		ByteBufUtils.writeTag(buf, this.entityTag);
	}


	public static final class MessageHandler implements IMessageHandler<ArmorStandSyncMessage, IMessage>
	{

		@Override
		public IMessage onMessage(final ArmorStandSyncMessage message, final MessageContext ctx)
		{
			final World world = ctx.getServerHandler().player.world;

			IThreadListener threadListener = (WorldServer)world;
			threadListener.addScheduledTask(new Runnable()
			{

				@Override
				public void run()
				{
					Entity entity = world.getEntityByID(message.entityId);
					if (entity instanceof EntityArmorStand)
					{
						EntityArmorStand entityArmorStand = (EntityArmorStand)entity;

						NBTTagCompound nbttagcompound = entityArmorStand.writeToNBT(new NBTTagCompound()).copy();
						nbttagcompound.merge(message.entityTag);
						entityArmorStand.readFromNBT(nbttagcompound);


//						NBTTagCompound entityTag = new NBTTagCompound();
//						entityArmorStand.writeToNBT(entityTag);
//						entityTag.merge(message.entityTag);
//						entityArmorStand.readFromNBT(entityTag);
					}
				}
			});

			return null;
		}
	}

}
