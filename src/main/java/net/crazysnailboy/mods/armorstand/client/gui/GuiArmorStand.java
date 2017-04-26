package net.crazysnailboy.mods.armorstand.client.gui;

import java.io.IOException;
import net.crazysnailboy.mods.armorstand.ArmorStand;
import net.crazysnailboy.mods.armorstand.client.gui.GuiComponents.GuiNumberField;
import net.crazysnailboy.mods.armorstand.client.gui.GuiComponents.GuiToggleButton;
import net.crazysnailboy.mods.armorstand.common.network.ArmorStandSyncMessage;
import net.crazysnailboy.mods.armorstand.util.ArmorStandData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArmorStand extends GuiScreen
{

	private final EntityArmorStand entityArmorStand;
	private final ArmorStandData armorStandData;

	private final String[] buttonLabels = new String[] { "invisible", "no_base_plate", "no_gravity", "show_arms", "small", "rotation" };
	private final String[] sliderLabels = new String[] { "head", "body", "left_leg", "right_leg", "left_arm", "right_arm" };


	private GuiTextField rotationTextField;
	private GuiToggleButton[] toggleButtons = new GuiToggleButton[5];
	private GuiTextField[] poseTextFields = new GuiTextField[18];

	private GuiButton copyButton;
	private GuiButton pasteButton;
	private GuiButton doneButton;
	private GuiButton cancelButton;


	public GuiArmorStand(EntityArmorStand entityArmorStand)
	{
		this.entityArmorStand = entityArmorStand;

		this.armorStandData = new ArmorStandData();
		this.armorStandData.readFromNBT(entityArmorStand.writeToNBT(new NBTTagCompound()));

		for ( int i = 0 ; i < buttonLabels.length ; i++ ) buttonLabels[i] = I18n.format(String.format("%s.gui.label." + buttonLabels[i], ArmorStand.MODID));
		for ( int i = 0 ; i < sliderLabels.length ; i++ ) sliderLabels[i] = I18n.format(String.format("%s.gui.label." + sliderLabels[i], ArmorStand.MODID));
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}


	@Override
	public void initGui()
	{
		super.initGui();

		int offsetX = 110;
		int offsetY = 50;

		// toggle buttons
		for ( int i = 0 ; i < this.toggleButtons.length ; i++ )
		{
			int id = i;
			int x = offsetX;
			int y = offsetY + (i * 22);
			int width = 40;
			int height = 20;

			this.buttonList.add(this.toggleButtons[i] = new GuiToggleButton(id, x, y, width, height, this.armorStandData.getBooleanValue(i)));
		}

		// rotation textbox
		this.rotationTextField = new GuiNumberField(4, this.fontRenderer, 1 + offsetX, 1 + offsetY + (this.toggleButtons.length * 22), 38, 17, String.valueOf((int)this.armorStandData.rotation));

		// pose textboxes
		offsetX = this.width - 20 - 100;
		for ( int i = 0 ; i < this.poseTextFields.length ; i++ )
		{
			int id = 5 + i;
			int x = 1 + offsetX + ((i % 3) * 35);
			int y = 1 + offsetY + ((i / 3) * 22);
			int width = 28;
			int height = 17;
			String value = String.valueOf((int)this.armorStandData.pose[i]);

			this.poseTextFields[i] = new GuiNumberField(id, this.fontRenderer, x, y, width, height, value);
			this.poseTextFields[i].setMaxStringLength(3);
		}

		offsetY = this.height / 4 + 120 + 12;

		// copy & paste buttons
		offsetX = 20;
		this.copyButton = this.addButton(new GuiButton(96, offsetX, offsetY, 64, 20, I18n.format(String.format("%s.gui.label.copy", ArmorStand.MODID))));
		this.pasteButton = this.addButton(new GuiButton(97, offsetX + 66, offsetY, 64, 20, I18n.format(String.format("%s.gui.label.paste", ArmorStand.MODID))));

		// done & cancel buttons
		offsetX = this.width - 20;
		this.doneButton = this.addButton(new GuiButton(98, offsetX - ((2 * 96) + 2), offsetY, 96, 20, I18n.format("gui.done")));
		this.cancelButton = this.addButton(new GuiButton(99, offsetX - 96, offsetY, 96, 20, I18n.format("gui.cancel")));
	}


	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();

		// gui title
		this.drawCenteredString(this.fontRenderer, I18n.format(String.format("%s.gui.title", ArmorStand.MODID)), this.width / 2, 20, 0xFFFFFF);

		// textboxes
		rotationTextField.drawTextBox();
		for ( GuiTextField textField : poseTextFields ) textField.drawTextBox();

		int offsetY = 50;

		// left column labels
		int offsetX = 20;
		for ( int i = 0 ; i < buttonLabels.length ; i++ )
		{
			int x = offsetX;
			int y = offsetY + (i * 22) + (10 - (this.fontRenderer.FONT_HEIGHT / 2));
			this.drawString(this.fontRenderer, buttonLabels[i], x, y, 0xA0A0A0);
		}

		// right column labels
		offsetX = this.width - 20 - 100;
		// x, y, z
		this.drawString(this.fontRenderer, "X", offsetX + (0 * 35), 37, 0xA0A0A0);
		this.drawString(this.fontRenderer, "Y", offsetX + (1 * 35), 37, 0xA0A0A0);
		this.drawString(this.fontRenderer, "Z", offsetX + (2 * 35), 37, 0xA0A0A0);
		// pose textboxes
		for ( int i = 0 ; i < sliderLabels.length ; i++ )
		{
			int x = offsetX - this.fontRenderer.getStringWidth(sliderLabels[i]) - 10;
			int y = offsetY + (i * 22) + (10 - (this.fontRenderer.FONT_HEIGHT / 2));
			this.drawString(this.fontRenderer, sliderLabels[i], x, y, 0xA0A0A0);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}


	@Override
	public void updateScreen()
	{
		super.updateScreen();
		rotationTextField.updateCursorCounter();
		for ( GuiTextField textField : poseTextFields ) textField.updateCursorCounter();
	}


	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);

		if (rotationTextField.textboxKeyTyped(typedChar, keyCode))
		{
			this.textFieldUpdated(rotationTextField);
			return;
		}

		if (keyCode == 15) // tab
		{
			for ( int i = 0 ; i < poseTextFields.length ; i++ )
			{
				if (poseTextFields[i].isFocused())
				{
					poseTextFields[i].setCursorPositionEnd();
					poseTextFields[i].setFocused(false);

					int j = (!GuiScreen.isShiftKeyDown() ? (i == poseTextFields.length - 1 ? 0 : i + 1) : (i == 0 ? poseTextFields.length - 1 : i - 1));
					poseTextFields[j].setFocused(true);
					poseTextFields[j].setCursorPosition(0);
					poseTextFields[j].setSelectionPos(poseTextFields[j].getText().length());

					return;
				}
			}
		}
		else
		{
			for ( GuiTextField textField : poseTextFields )
			{
				if (textField.textboxKeyTyped(typedChar, keyCode))
				{
					this.textFieldUpdated(textField);
					return;
				}
			}

		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		rotationTextField.mouseClicked(mouseX, mouseY, mouseButton);
		for ( GuiTextField textField : poseTextFields ) textField.mouseClicked(mouseX, mouseY, mouseButton);
	}


	@Override
	protected void actionPerformed(net.minecraft.client.gui.GuiButton button)
	{
		if (button instanceof GuiToggleButton) ((GuiToggleButton)button).buttonPressed();

		if (button == copyButton)
		{
			NBTTagCompound compound = writeFieldsToNBT();
			String clipboardData = compound.toString();
			GuiScreen.setClipboardString(clipboardData);
			return;
		}
		else if (button == pasteButton)
		{
			try
			{
				String clipboardData = GuiScreen.getClipboardString();
				NBTTagCompound compound = JsonToNBT.getTagFromJson(clipboardData);
				this.readFieldsFromNBT(compound);
				updateEntity(compound);
			}
			catch (NBTException e) { }
			return;
		}
		else if (button == doneButton)
		{
			updateEntity(writeFieldsToNBT());
			this.mc.displayGuiScreen((GuiScreen)null);
			return;
		}
		else if (button == cancelButton)
		{
			updateEntity(this.armorStandData.writeToNBT());
			this.mc.displayGuiScreen((GuiScreen)null);
			return;
		}

		updateEntity(writeFieldsToNBT());
	}

	protected void textFieldUpdated(net.minecraft.client.gui.GuiTextField textField)
	{
		updateEntity(writeFieldsToNBT());
	}


	private NBTTagCompound writeFieldsToNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("Invisible", this.toggleButtons[0].getValue());
		compound.setBoolean("NoBasePlate", this.toggleButtons[1].getValue());
		compound.setBoolean("NoGravity", this.toggleButtons[2].getValue());
		compound.setBoolean("ShowArms", this.toggleButtons[3].getValue());
		compound.setBoolean("Small", this.toggleButtons[4].getValue());

		NBTTagList rotationTag = new NBTTagList();
		rotationTag.appendTag(new NBTTagFloat(Float.valueOf( this.rotationTextField.getText() )));
		compound.setTag("Rotation", rotationTag);

		NBTTagCompound poseTag = new NBTTagCompound();

		NBTTagList poseHeadTag = new NBTTagList();
		poseHeadTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[0].getText() )));
		poseHeadTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[1].getText() )));
		poseHeadTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[2].getText() )));
		poseTag.setTag("Head", poseHeadTag);

		NBTTagList poseBodyTag = new NBTTagList();
		poseBodyTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[3].getText() )));
		poseBodyTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[4].getText() )));
		poseBodyTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[5].getText() )));
		poseTag.setTag("Body", poseBodyTag);

		NBTTagList poseLeftLegTag = new NBTTagList();
		poseLeftLegTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[6].getText() )));
		poseLeftLegTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[7].getText() )));
		poseLeftLegTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[8].getText() )));
		poseTag.setTag("LeftLeg", poseLeftLegTag);

		NBTTagList poseRightLegTag = new NBTTagList();
		poseRightLegTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[9].getText() )));
		poseRightLegTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[10].getText() )));
		poseRightLegTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[11].getText() )));
		poseTag.setTag("RightLeg", poseRightLegTag);

		NBTTagList poseLeftArmTag = new NBTTagList();
		poseLeftArmTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[12].getText() )));
		poseLeftArmTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[13].getText() )));
		poseLeftArmTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[14].getText() )));
		poseTag.setTag("LeftArm", poseLeftArmTag);

		NBTTagList poseRightArmTag = new NBTTagList();
		poseRightArmTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[15].getText() )));
		poseRightArmTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[16].getText() )));
		poseRightArmTag.appendTag(new NBTTagFloat(Float.valueOf( this.poseTextFields[17].getText() )));
		poseTag.setTag("RightArm", poseRightArmTag);

		compound.setTag("Pose", poseTag);
		return compound;
	}


	private void readFieldsFromNBT(NBTTagCompound compound)
	{
		ArmorStandData armorStandData = new ArmorStandData();
		armorStandData.readFromNBT(compound);

		for ( int i = 0 ; i < toggleButtons.length ; i++ )
		{
			toggleButtons[i].setValue(armorStandData.getBooleanValue(i));
		}

		rotationTextField.setText(String.valueOf((int)armorStandData.rotation));

		for ( int i = 0 ; i < poseTextFields.length ; i++ )
		{
			poseTextFields[i].setText(String.valueOf((int)armorStandData.pose[i]));
		}
	}


	private void updateEntity(NBTTagCompound compound)
	{
		NBTTagCompound nbttagcompound = entityArmorStand.writeToNBT(new NBTTagCompound()).copy();
		nbttagcompound.merge(compound);
		entityArmorStand.readFromNBT(nbttagcompound);

		ArmorStand.INSTANCE.getNetwork().sendToServer(new ArmorStandSyncMessage(this.entityArmorStand.getEntityId(), compound));
	}


}
