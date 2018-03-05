package lycanite.lycanitesmobs.api.network;

import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class MessageGUIRequest implements IMessage, IMessageHandler<MessageGUIRequest, IMessage> {
	public byte guiID;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageGUIRequest() {}
	public MessageGUIRequest(byte guiID) {
		this.guiID = guiID;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessageGUIRequest message, MessageContext ctx) {
		if(ctx.side != Side.SERVER) return null;
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		playerExt.requestGUI(message.guiID);
		return null;
	}
	
	
	// ==================================================
	//                    From Bytes
	// ==================================================
	/**
	 * Reads the message from bytes.
	 */
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer packet = new PacketBuffer(buf);
		this.guiID = packet.readByte();
	}
	
	
	// ==================================================
	//                     To Bytes
	// ==================================================
	/**
	 * Writes the message into bytes.
	 */
	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer packet = new PacketBuffer(buf);
		packet.writeByte(this.guiID);
	}
	
}
