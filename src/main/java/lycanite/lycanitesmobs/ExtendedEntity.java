package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.network.MessageEntityPickedUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedEntity implements IExtendedEntityProperties {
	public static String EXT_PROP_NAME = "LycanitesMobsEntity";
    public static String[] FORCE_REMOVE_ENTITY_IDS;
    public static int FORCE_REMOVE_ENTITY_TICKS = 40;
	
	public Entity entity;
	
	// States:
	public Entity pickedUpByEntity;
	private int pickedUpByEntityID;

    /** The last coordinates the entity was at where it wasn't inside an opaque block. (Helps prevent suffocation). **/
    double[] lastSafePos;
    private boolean playerAllowFlyingSnapshot;
    private boolean playerIsFlyingSnapshot;
	
	public EntityFear fearEntity;

    // Force Remove:
    boolean forceRemoveChecked = false;
    boolean forceRemove = false;
    int forceRemoveTicks = FORCE_REMOVE_ENTITY_TICKS;
	
	// ==================================================
    //                   Get for Entity
    // ==================================================
	public static ExtendedEntity getForEntity(Entity entity) {
		if(entity == null) {
			//LycanitesMobs.printWarning("", "Tried to access an ExtendedEntity from a null Entity.");
			return null;
		}
		IExtendedEntityProperties entityIExt = entity.getExtendedProperties(EXT_PROP_NAME);
		ExtendedEntity entityExt;
		if(entityIExt != null)
			entityExt = (ExtendedEntity)entityIExt;
		else
			entityExt = new ExtendedEntity(entity);
		
		return entityExt;
	}
	
	
	// ==================================================
    //                    Constructor
    // ==================================================
	public ExtendedEntity(Entity entity) {
		entity.registerExtendedProperties(ExtendedEntity.EXT_PROP_NAME, this);
	}
	
	
	// ==================================================
    //                       Init
    // ==================================================
	@Override
	public void init(Entity entity, World world) {
		this.entity = entity;
	}
	
	
	// ==================================================
    //                      Update
    // ==================================================
	public void onUpdate() {
        if(this.entity == null)
            return;

        // Force Remove Entity:
        if(this.entity.worldObj != null && !this.entity.worldObj.isRemote && FORCE_REMOVE_ENTITY_IDS != null && FORCE_REMOVE_ENTITY_IDS.length > 0 && !this.forceRemoveChecked) {
            LycanitesMobs.printDebug("ForceRemoveEntity", "Forced entity removal, checking: " + this.entity.getCommandSenderName());
            for(String forceRemoveID : FORCE_REMOVE_ENTITY_IDS) {
                if(forceRemoveID.equalsIgnoreCase(this.entity.getCommandSenderName())) {
                    this.forceRemove = true;
                    break;
                }
            }
            this.forceRemoveChecked = true;
        }
        if(this.forceRemove && this.forceRemoveTicks-- <= 0)
            this.entity.setDead();

        // Safe Position:
        if(this.lastSafePos == null) {
            this.lastSafePos = new double[]{this.entity.posX, this.entity.posY, this.entity.posZ};
        }
        if(this.entity.noClip || !this.entity.isEntityInsideOpaqueBlock()) {
            this.lastSafePos[0] = this.entity.posX;
            this.lastSafePos[1] = this.entity.posY;
            this.lastSafePos[2] = this.entity.posZ;
        }

		// Picked Up By Entity Check:
		if(this.pickedUpByEntity != null) {
			if(!this.pickedUpByEntity.isEntityAlive())
				this.setPickedUpByEntity(null);
			else if(this.pickedUpByEntity instanceof EntityLivingBase) {
				if(((EntityLivingBase)this.pickedUpByEntity).getHealth() <= 0)
					this.setPickedUpByEntity(null);
			}
            else if(this.entity instanceof EntityLivingBase && ObjectManager.getPotionEffect("weight") != null) {
                if(((EntityLivingBase)(this.entity)).isPotionActive(ObjectManager.getPotionEffect("weight")))
                    this.setPickedUpByEntity(null);
            }
			else if(this.entity.getDistanceSqToEntity(this.pickedUpByEntity) > 32D) {
				this.setPickedUpByEntity(null);
			}
		}

        // Picked Up By Entity Movement:
		if(this.pickedUpByEntity != null) {
			double[] pickupOffset = new double[]{0, 0, 0};
			if(this.pickedUpByEntity instanceof EntityCreatureBase)
				pickupOffset = ((EntityCreatureBase)this.pickedUpByEntity).getPickupOffset(this.entity);
			double yPos = this.pickedUpByEntity.posY;
			if(this.entity.worldObj.isRemote && entity instanceof EntityPlayer) {
				yPos = this.pickedUpByEntity.boundingBox.minY + entity.height;
			}
			this.entity.setPosition(this.pickedUpByEntity.posX + pickupOffset[0], yPos + pickupOffset[1], this.pickedUpByEntity.posZ + pickupOffset[2]);
			this.entity.motionX = this.pickedUpByEntity.motionX;
			this.entity.motionY = this.pickedUpByEntity.motionY;
			this.entity.motionZ = this.pickedUpByEntity.motionZ;
			this.entity.fallDistance = 0;
			if(!this.entity.worldObj.isRemote && this.entity instanceof EntityPlayer) {
				((EntityPlayer)this.entity).capabilities.allowFlying = true;
			}
			if(!this.entity.isEntityAlive())
				this.setPickedUpByEntity(null);
			if(this.entity instanceof EntityLivingBase) {
				if(((EntityLivingBase)this.entity).getHealth() <= 0)
					this.setPickedUpByEntity(null);
			}
    	}
		else if(this.pickedUpByEntityID != (this.pickedUpByEntity != null ? this.pickedUpByEntity.getEntityId() : 0)) {
			/*if(!this.entity.worldObj.isRemote && this.entity instanceof EntityPlayer) {
				((EntityPlayer)this.entity).capabilities.allowFlying = this.playerAllowFlyingSnapshot;
                ((EntityPlayer)this.entity).capabilities.isFlying = this.playerIsFlyingSnapshot;
			}*/
            this.pickedUpByEntityID = (this.pickedUpByEntity != null ? this.pickedUpByEntity.getEntityId() : 0);
		}
		
		// Fear Entity:
		if(this.fearEntity != null && !this.fearEntity.isEntityAlive())
			this.fearEntity = null;
	}
	
	
	// ==================================================
    //                       Death
    // ==================================================
	public void onDeath() {
		this.setPickedUpByEntity(null);
	}
	
	
	// ==================================================
    //                 Picked Up By Entity
    // ==================================================
	public void setPickedUpByEntity(Entity pickedUpByEntity) {
        if(this.pickedUpByEntity == pickedUpByEntity || this.entity == null)
            return;

		if(this.entity.ridingEntity != null)
			this.entity.mountEntity(null);
		if(this.entity.riddenByEntity != null)
			this.entity.riddenByEntity.mountEntity(null);
		this.pickedUpByEntity = pickedUpByEntity;

        // Server Side:
		if(!this.entity.worldObj.isRemote) {

            // Player Flying:
			if(this.entity instanceof EntityPlayer) {
				if(pickedUpByEntity != null) {
                    this.playerAllowFlyingSnapshot = ((EntityPlayer) this.entity).capabilities.allowFlying;
                    this.playerIsFlyingSnapshot = ((EntityPlayer)this.entity).capabilities.isFlying;
                }
				else {
                    ((EntityPlayer)this.entity).capabilities.allowFlying = this.playerAllowFlyingSnapshot;
                    ((EntityPlayer)this.entity).capabilities.isFlying = this.playerIsFlyingSnapshot;
                }
			}

            // Safe Position:
            if(pickedUpByEntity == null) {
                if(this.lastSafePos != null && this.lastSafePos.length >= 3)
                    this.entity.setPosition(this.lastSafePos[0], this.lastSafePos[1], this.lastSafePos[2]);
                this.entity.motionX = 0;
                this.entity.motionY = 0;
                this.entity.motionZ = 0;
                this.entity.fallDistance = 0;
            }

			MessageEntityPickedUp message = new MessageEntityPickedUp(this.entity, pickedUpByEntity);
			LycanitesMobs.packetHandler.sendToDimension(message, this.entity.dimension);
		}
	}
	
	public boolean isFeared() {
		return this.pickedUpByEntity instanceof EntityFear;
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
	@Override
    public void loadNBTData(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("ActiveEffects", 9))  {
            NBTTagList nbttaglist = nbtTagCompound.getTagList("ActiveEffects", 9);

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);
                byte potionID = nbttagcompound1.getByte("Id");
                if(potionID >= Potion.potionTypes.length || Potion.potionTypes[potionID] == null) {
                	nbttaglist.removeTag(i);
    				LycanitesMobs.printWarning("EffectsSetup", "Found a null potion effect in entity NBTTag, this effect has been removed.");
                }
            }
        }
    }
    
    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
	@Override
    public void saveNBTData(NBTTagCompound nbtTagCompound) {
		
    }
}
