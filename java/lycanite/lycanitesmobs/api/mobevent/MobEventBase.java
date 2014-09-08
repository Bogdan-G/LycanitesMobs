package lycanite.lycanitesmobs.api.mobevent;

import java.util.ArrayList;
import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.gui.GuiOverlay;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.spawning.CustomSpawner;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MobEventBase {
	
	// Properties:
	public String name = "mobevent";
	public int weight = 8;
    public List<SpawnTypeBase> spawners = new ArrayList<SpawnTypeBase>();
    public GroupInfo group;

    // Properties:
    public int duration = 30 * 20;
    public int activeTicks = 0;
    public World world;
    
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBase(String name, GroupInfo group) {
		this.name = name;
		this.group = group;
	}
    
	
    // ==================================================
    //                       Names
    // ==================================================
    /** Returns the translated name of this event. **/
	public String getTitle() {
		return StatCollector.translateToLocal("mobevent." + this.name + ".name");
	}

    /** Returns a translated string to overlay the event image, this returns an empty string for english as the image itself has the title in english. **/
    public String getDisplayTitle() {
        String title = this.getTitle().replaceAll(" ", "").toLowerCase();
        return title.equalsIgnoreCase(this.name) ? "" : title;
    }
	
	
    // ==================================================
    //                      Enabled
    // ==================================================
	public boolean isEnabled() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
		return config.getBool("Events Enabled", this.name, true);
	}


    // ==================================================
    //                    Event Stats
    // ==================================================
    /* Returns the rate that event mobs are spawned at in ticks. This changes based on the difficulty of the provided world. */
    public int getRate(World world) {
        int base = MobEventManager.instance.baseRate;
        if(world.difficultySetting.getDifficultyId() <= 1)
            return Math.round(base * 1.5F);
        else if(world.difficultySetting.getDifficultyId() == 2)
            return base;
        else
            return Math.round(base * 0.5F);
    }

    /* Returns the distance from the player that event mobs are spawned from. This changes based on the difficulty of the provided world. */
    public int getRange(World world) {
        int base = MobEventManager.instance.baseRange;
        if(world.difficultySetting.getDifficultyId() <= 1)
            return Math.round(base * 1.5F);
        else if(world.difficultySetting.getDifficultyId() == 2)
            return base;
        else
            return Math.round(base * 0.5F);
    }


    // ==================================================
    //                     Spawners
    // ==================================================
    public MobEventBase addSpawner(SpawnTypeBase spawner) {
        if(!this.spawners.contains(spawner)) {
            this.spawners.add(spawner);
            spawner.setMobEvent(this);
        }
        return this;
    }
	
	
    // ==================================================
    //                       Start
    // ==================================================
	public void onStart(World world) {
		LycanitesMobs.printDebug("", "Event Started - Remote: " + world.isRemote); //XXX
		this.activeTicks = 0;
		this.world = world;
		
		// Server Side:
		if(!world.isRemote) {
	        for(SpawnTypeBase spawner : this.spawners) {
	            if(spawner != null && !CustomSpawner.instance.updateSpawnTypes.contains(spawner))
	                CustomSpawner.instance.updateSpawnTypes.add(spawner);
	        }
		}
		
		// Client Side:
        else {
    		String eventMessage = StatCollector.translateToLocal("event.started");
			eventMessage = eventMessage.replace("%event%", this.getTitle());
			LycanitesMobs.proxy.getClientPlayer().addChatMessage(new ChatComponentText(eventMessage));
			
        	if(AssetManager.getSound("mobevent_" + this.name.toLowerCase()) == null)
        			AssetManager.addSound("mobevent_" + this.name.toLowerCase(), this.group, "mobevent." + this.name.toLowerCase());
        	world.playSoundAtEntity(LycanitesMobs.proxy.getClientPlayer(), AssetManager.getSound("mobevent_" + this.name.toLowerCase()), 1.5F, 1.0F);
        }
	}
	
	
    // ==================================================
    //                      Finish
    // ==================================================
	public void onFinish() {
		if(this.world.isRemote) {
	        String eventMessage = StatCollector.translateToLocal("event.finished");
			eventMessage = eventMessage.replace("%event%", this.getTitle());
			LycanitesMobs.proxy.getClientPlayer().addChatMessage(new ChatComponentText(eventMessage));
		}
		
		LycanitesMobs.printDebug("", "Event Finished - Remote: " + world.isRemote); //XXX
	}
	
	
    // ==================================================
    //                      Update
    // ==================================================
	public void onServerUpdate() {
		if(this.world == null) {
			LycanitesMobs.printWarning("", "Mob Event is trying to update without a world object!");
			return;
		}
		if(this.world.isRemote)
			return;
		
        this.activeTicks++;

        if(this.activeTicks > this.duration) {
        	for(SpawnTypeBase spawner : this.spawners) {
                if(spawner != null && CustomSpawner.instance.updateSpawnTypes.contains(spawner))
                    CustomSpawner.instance.updateSpawnTypes.remove(spawner);
            }
            MobEventManager.instance.stopMobEvent();
        }
	}
	
	
    // ==================================================
    //                  Client Update
    // ==================================================
	public void onClientUpdate() {
        this.activeTicks++;
	}
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
	public void onSpawn(EntityLiving entity) {
		// TODO Override this class and add lots of FIRE! Yeahhh!
	}
	
	
    // ==================================================
    //                       GUI
    // ==================================================
    @SideOnly(Side.CLIENT)
	public void onGUIUpdate(GuiOverlay gui, int sWidth, int sHeight) {
    	if(this.world == null) return;
    	if(!this.world.isRemote) return;
    	
		int introTime = 7 * 20;
        if(this.activeTicks > introTime) return;
        int startTime = 2 * 20;
        int stopTime = 1 * 20;
        float animation = 1.0F;

        if(this.activeTicks < startTime)
            animation = (float)this.activeTicks / (float)startTime;
        else if(this.activeTicks > introTime - stopTime)
            animation = 1.0F - ((float)(introTime - this.activeTicks) / (float)stopTime);

        int width = 256;
        int height = 256;
        int x = (sWidth / 2) - (width / 2);
        int y = (sHeight / 2) - (height / 2);
        int u = width;
        int v = height;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, animation);
        gui.mc.getTextureManager().bindTexture(this.getTexture());
        gui.drawTexturedModalRect(x, y, u, v, width, height);
	}

    @SideOnly(Side.CLIENT)
    public ResourceLocation getTexture() {
        if(AssetManager.getTexture("guimobevent" + this.name) == null)
            AssetManager.addTexture("guimobevent" + this.name, this.group, "textures/mobevents/" + this.name.toLowerCase() + ".png");
        return AssetManager.getTexture("guimobevent" + this.name);
    }
}
