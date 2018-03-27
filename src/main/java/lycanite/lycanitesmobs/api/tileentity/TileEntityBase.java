package lycanite.lycanitesmobs.api.tileentity;

import lycanite.lycanitesmobs.api.entity.EntityPortal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;

public class TileEntityBase extends TileEntity {
    public EntityPortal summoningPortal;
    protected AxisAlignedBB aabb;

    @Override
    public void validate() {
    	aabb = AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord - 1, zCoord - 1, xCoord + 2, yCoord + 1, zCoord + 2);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
    	return aabb;
    }

    // ========================================
    //                  Remove
    // ========================================
    /** Can be called by a block when broken to alert this TileEntity that it is being removed. **/
    public void onRemove() {}


    // ========================================
    //                  Update
    // ========================================
    /** The main update called every tick. **/
    @Override
    public void updateEntity() {}


    // ========================================
    //              Client Events
    // ========================================
    public boolean receiveClientEvent(int eventID, int eventArg) {
        return false;
    }


    // ========================================
    //             Network Packets
    // ========================================
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {}


    // ========================================
    //                 NBT Data
    // ========================================
    /** Reads from saved NBT data. **/
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
    }

    /** Writes to NBT data. **/
    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
    }


    // ========================================
    //                Open GUI
    // ========================================
    /** Called by the GUI Handler when opening a GUI. **/
    public Object getGUI(EntityPlayer player) {
        return null;
    }

    @Override 
    public void markDirty() {/* Do not do the super Function */} 
}
