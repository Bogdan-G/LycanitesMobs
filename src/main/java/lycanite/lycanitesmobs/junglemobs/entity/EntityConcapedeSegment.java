package lycanite.lycanitesmobs.junglemobs.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityConcapedeSegment extends EntityCreatureAgeable implements IAnimals, IGroupAnimal {
    
	// Parent UUID:
	/** Used to identify the parent segment when loading this saved entity, set to null when found or lost for good. **/
	UUID parentUUID = null;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConcapedeSegment(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 0;
        this.experience = 3;
        this.hasAttackSound = true;
        this.hasStepSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0D;
        
        this.setWidth = 0.5F;
        this.setHeight = 0.9F;
        this.isHostileByDefault = false;
        this.setupMob();
    	
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(5, new EntityAIFollowParent(this).setSpeed(1.1D).setStrayDistance(0).setLostDistance(0).setAdultFollowing(true).setFollowBehind(0.25D));
        this.tasks.addTask(6, new EntityAIWander(this).setPauseRate(30));
        //this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        //this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpClasses(EntityConcapedeHead.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("ConcapedeMeatRaw")), 1).setMinAmount(1).setMaxAmount(2).setBurningDrop(new ItemStack(ObjectManager.getItem("ConcapedeMeatCooked"))));
        this.drops.add(new DropRate(new ItemStack(Items.string), 0.25F).setMinAmount(1).setMaxAmount(2));
	}

    // ==================================================
    //                      Spawning
    // ==================================================
    // ========== Natural Spawn Check ==========
    /** Second stage checks for spawning, this check is ignored if there is a valid monster spawner nearby. **/
    @Override
    public boolean naturalSpawnCheck(World world, int i, int j, int k) {
    	if(this.getNearbyEntities(EntityConcapedeHead.class, SpawnInfo.spawnLimitRange).size() <= 0)
    		return false;
    	return super.naturalSpawnCheck(world, i, j, k);
    }
    
    // ========== Get Random Subspecies ==========
    @Override
    public void getRandomSubspecies() {
    	if(this.subspecies == null && !this.hasParent()) {
    		this.subspecies = this.mobInfo.getRandomSubspecies(this);
    		if(this.subspecies != null)
    			LycanitesMobs.printDebug("Subspecies", "Setting " + this.getSpeciesName() + " to " + this.subspecies.getTitle());
    		else
    			LycanitesMobs.printDebug("Subspecies", "Setting " + this.getSpeciesName() + " to base species.");
    	}
    	
    	if(this.hasParent() && this.getParentTarget() instanceof EntityCreatureBase) {
    		this.setSubspecies(((EntityCreatureBase)this.getParentTarget()).getSubspeciesIndex(), true);
    	}
    }
    
    // ========== Despawning ==========
    /** Returns whether this mob should despawn overtime or not. Config defined forced despawns override everything except tamed creatures and tagged creatures. **/
    @Override
    protected boolean canDespawn() {
    	if(!super.canDespawn())
    		return false;
    	return !this.hasParent();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        // Try to Load Parent from UUID:
        if(!this.worldObj.isRemote && !this.hasParent() && this.parentUUID != null) {
	        double range = 64D;
	        List connections = this.worldObj.getEntitiesWithinAABB(EntityCreatureAgeable.class, this.boundingBox.expand(range, range, range));
	        Iterator possibleConnections = connections.iterator();
	        while(possibleConnections.hasNext()) {
	        	EntityCreatureAgeable possibleConnection = (EntityCreatureAgeable)possibleConnections.next();
	            if(possibleConnection != this && possibleConnection.getUniqueID().equals(this.parentUUID)) {
	            	this.setParentTarget(possibleConnection);
	            	break;
	            }
	        }
	        this.parentUUID = null;
        }
        
        super.onLivingUpdate();
        
        // Concapede Connections:
        if(!this.worldObj.isRemote) {
        	// Check if back segment is alive:
        	if(this.hasMaster()) {
        		if(!this.getMasterTarget().isEntityAlive())
        			this.setMasterTarget(null);
        	}

        	// Check if front segment is alive:
        	if(this.hasParent()) {
        		if(!this.getParentTarget().isEntityAlive())
        			this.setParentTarget(null);
        	}

        	// Force position to front with offset:
        	if(this.hasParent()) {
        		this.faceEntity(this.getParentTarget(), 360, 360);
        		
        		double segmentDistance = 0.5D;
        		double[] coords;
        		if(this.getParentTarget() instanceof EntityCreatureBase)
        			coords = ((EntityCreatureBase)this.getParentTarget()).getFacingPosition(-0.25D);
        		else {
        			coords = new double[3];
        			coords[0] = this.getParentTarget().posX;
        			coords[1] = this.getParentTarget().posY;
        			coords[2] = this.getParentTarget().posZ;
        		}
        		if(this.posX - coords[0] > segmentDistance)
        			this.posX = coords[0] + segmentDistance;
        		else if(this.posX - coords[0] < -segmentDistance)
        			this.posX = coords[0] - segmentDistance;
        		
        		if(this.posY - coords[1] > segmentDistance)
        			this.posY = coords[1] + segmentDistance;
        		else if(this.posY - coords[1] < -(segmentDistance / 2))
        			this.posY = coords[1];
        		
        		if(this.posZ - coords[2] > segmentDistance)
        			this.posZ = coords[2] + segmentDistance;
        		else if(this.posZ - coords[2] < -segmentDistance)
        			this.posZ = coords[2] - segmentDistance;
        	}
        }
        
        // Growth Into Head:
        if(!this.worldObj.isRemote && this.getGrowingAge() <= 0)
        	this.setGrowingAge(-this.growthTime);
    }
	
	
	// ==================================================
  	//                        Age
  	// ==================================================
	@Override
	public void setGrowingAge(int age) {
		if(this.hasParent())
			age = -this.growthTime;
        super.setGrowingAge(age);
		if(age == 0 && !this.worldObj.isRemote) {
			EntityConcapedeHead concapedeHead = new EntityConcapedeHead(this.worldObj);
			concapedeHead.copyLocationAndAnglesFrom(this);
			concapedeHead.firstSpawn = false;
			concapedeHead.setGrowingAge(-this.growthTime / 4);
			this.worldObj.spawnEntityInWorld(concapedeHead);
			if(this.hasMaster() && this.getMasterTarget() instanceof EntityConcapedeSegment)
				((EntityConcapedeSegment)this.getMasterTarget()).setParentTarget(concapedeHead);
			this.worldObj.removeEntity(this);
		}
    }

	
    // ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		if(this.worldObj.getBlock(par1, par2 - 1, par3) != Blocks.air) {
			Block block = this.worldObj.getBlock(par1, par2 - 1, par3);
			if(block.getMaterial() == Material.grass)
				return 10F;
			if(block.getMaterial() == Material.ground)
				return 7F;
		}
        return super.getBlockPathWeight(par1, par2, par3);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canLeash(EntityPlayer player) {
	    return !this.hasParent();
    }
    
    // ========== Falling Speed Modifier ==========
    @Override
    public double getFallingMod() {
    	if(this.worldObj.isRemote)
    		return 0.0D;
    	if(this.hasParent() && this.getParentTarget().posY > this.posY)
    		return 0.0D;
    	return super.getFallingMod();
    }
    
    
    // ==================================================
   	//                     Targets
   	// ==================================================
	@Override
	public void setParentTarget(EntityLivingBase setTarget) {
		if(setTarget instanceof EntityConcapedeSegment || setTarget instanceof EntityConcapedeHead)
			((EntityCreatureBase)setTarget).setMasterTarget(this);
		super.setParentTarget(setTarget);
	}
    
	
	// ==================================================
   	//                     Interact
   	// ==================================================
    // ========== Render Subspecies Name Tag ==========
    /** Gets whether this mob should always display its nametag if it's a subspecies. **/
	@Override
    public boolean renderSubspeciesNameTag() {
    	return !this.hasParent();
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
	@Override
    public boolean isDamageTypeApplicable(String type) {
    	if(type.equals("inWall")) return false;
    	return super.isDamageTypeApplicable(type);
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.poison.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.moveSlowdown.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    @Override
    public float getFallResistance() {
    	return 100;
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partener) {
		return null;
	}
    
    // ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("Vegetables", testStack);
    }
	
	// ========== Breed ==========
	public boolean breed() {
		if(!this.canBreed())
			return false;
        this.setGrowingAge(0);
        return true;
	}
	
	@Override
	public boolean canBreed() {
        return !this.hasParent();
    }
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Used when loading this mob from a saved chunk. **/
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
    	if(nbtTagCompound.hasKey("ParentUUIDMost") && nbtTagCompound.hasKey("ParentUUIDLeast")) {
            this.parentUUID = new UUID(nbtTagCompound.getLong("ParentUUIDMost"), nbtTagCompound.getLong("ParentUUIDLeast"));
        }
        super.readEntityFromNBT(nbtTagCompound);
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
    	if(this.hasParent()) {
    		nbtTagCompound.setLong("ParentUUIDMost", this.getParentTarget().getUniqueID().getMostSignificantBits());
    		nbtTagCompound.setLong("ParentUUIDLeast", this.getParentTarget().getUniqueID().getLeastSignificantBits());
    	}
        super.writeEntityToNBT(nbtTagCompound);
    }
}
