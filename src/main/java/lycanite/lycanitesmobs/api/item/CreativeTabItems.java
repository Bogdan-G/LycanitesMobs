package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabItems extends CreativeTabs {
	
	// ========== Constructor ==========
	public CreativeTabItems(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		if(ObjectManager.getItem("soulgazer") != null)
			return ObjectManager.getItem("soulgazer");
		else if(ObjectManager.getItem("HellfireCharge") != null)
			return ObjectManager.getItem("HellfireCharge");
		else if(ObjectManager.getItem("JoustMeat") != null)
			return ObjectManager.getItem("JoustMeat");
		else if(ObjectManager.getItem("PoisonGland") != null)
			return ObjectManager.getItem("PoisonGland");
		else
			return Items.bone;
	}
}