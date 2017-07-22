package net.crazysnailboy.mods.armorstand.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;


public class ArmorStandData
{

	public boolean invisible = false;
	public boolean noBasePlate = false;
	public boolean noGravity = false;
	public boolean showArms = false;
	public boolean small = false;

	public float rotation = 0F;

	public float[] pose = new float[18];


	public boolean getBooleanValue(int index)
	{
		switch (index)
		{
			case 0:
				return this.invisible;
			case 1:
				return this.noBasePlate;
			case 2:
				return this.noGravity;
			case 3:
				return this.showArms;
			case 4:
				return this.small;
		}
		return false;
	}


	public void readFromNBT(NBTTagCompound compound)
	{
		this.invisible = compound.getBoolean("Invisible");
		this.noBasePlate = compound.getBoolean("NoBasePlate");
		this.noGravity = compound.getBoolean("NoGravity");
		this.showArms = compound.getBoolean("ShowArms");
		this.small = compound.getBoolean("Small");

		if (compound.hasKey("Rotation"))
		{
			this.rotation = compound.getTagList("Rotation", NBT.TAG_FLOAT).getFloatAt(0);
		}
		if (compound.hasKey("Pose"))
		{
			NBTTagCompound poseTag = (NBTTagCompound)compound.getTag("Pose");

			String[] keys = new String[] { "Head", "Body", "LeftLeg", "RightLeg", "LeftArm", "RightArm" };
			for (int i = 0; i < keys.length; i++)
			{
				String key = keys[i];
				if (poseTag.hasKey(key))
				{
					NBTTagList tagList = poseTag.getTagList(key, NBT.TAG_FLOAT);
					for (int j = 0; j <= 2; j++)
					{
						int k = (i * 3) + j;
						this.pose[k] = tagList.getFloatAt(j);
					}
				}
			}

		}

	}

	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("Invisible", this.invisible);
		compound.setBoolean("NoBasePlate", this.noBasePlate);
		compound.setBoolean("NoGravity", this.noGravity);
		compound.setBoolean("ShowArms", this.showArms);
		compound.setBoolean("Small", this.small);

		NBTTagList rotationTag = new NBTTagList();
		rotationTag.appendTag(new NBTTagFloat(this.rotation));
		compound.setTag("Rotation", rotationTag);

		NBTTagCompound poseTag = new NBTTagCompound();

		NBTTagList poseHeadTag = new NBTTagList();
		poseHeadTag.appendTag(new NBTTagFloat(this.pose[0]));
		poseHeadTag.appendTag(new NBTTagFloat(this.pose[1]));
		poseHeadTag.appendTag(new NBTTagFloat(this.pose[2]));
		poseTag.setTag("Head", poseHeadTag);

		NBTTagList poseBodyTag = new NBTTagList();
		poseBodyTag.appendTag(new NBTTagFloat(this.pose[3]));
		poseBodyTag.appendTag(new NBTTagFloat(this.pose[4]));
		poseBodyTag.appendTag(new NBTTagFloat(this.pose[5]));
		poseTag.setTag("Body", poseBodyTag);

		NBTTagList poseLeftLegTag = new NBTTagList();
		poseLeftLegTag.appendTag(new NBTTagFloat(this.pose[6]));
		poseLeftLegTag.appendTag(new NBTTagFloat(this.pose[7]));
		poseLeftLegTag.appendTag(new NBTTagFloat(this.pose[8]));
		poseTag.setTag("LeftLeg", poseLeftLegTag);

		NBTTagList poseRightLegTag = new NBTTagList();
		poseRightLegTag.appendTag(new NBTTagFloat(this.pose[9]));
		poseRightLegTag.appendTag(new NBTTagFloat(this.pose[10]));
		poseRightLegTag.appendTag(new NBTTagFloat(this.pose[11]));
		poseTag.setTag("RightLeg", poseRightLegTag);

		NBTTagList poseLeftArmTag = new NBTTagList();
		poseLeftArmTag.appendTag(new NBTTagFloat(this.pose[12]));
		poseLeftArmTag.appendTag(new NBTTagFloat(this.pose[13]));
		poseLeftArmTag.appendTag(new NBTTagFloat(this.pose[14]));
		poseTag.setTag("LeftArm", poseLeftArmTag);

		NBTTagList poseRightArmTag = new NBTTagList();
		poseRightArmTag.appendTag(new NBTTagFloat(this.pose[15]));
		poseRightArmTag.appendTag(new NBTTagFloat(this.pose[16]));
		poseRightArmTag.appendTag(new NBTTagFloat(this.pose[17]));
		poseTag.setTag("RightArm", poseRightArmTag);

		compound.setTag("Pose", poseTag);
		return compound;
	}

}
