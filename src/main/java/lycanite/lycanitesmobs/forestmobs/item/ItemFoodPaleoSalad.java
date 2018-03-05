package lycanite.lycanitesmobs.forestmobs.item;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFoodPaleoSalad extends ItemCustomFood {
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemFoodPaleoSalad(String setItemName, GroupInfo group, String setTexturePath, int feed, float saturation, FOOD_CLASS foodClass) {
		super(setItemName, group, setTexturePath, feed, saturation, foodClass);
	}
	public ItemFoodPaleoSalad(String setItemName, GroupInfo group, int feed, float saturation, FOOD_CLASS foodClass) {
		super(setItemName, group, feed, saturation, foodClass);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, this.getEffectDuration(), 2));
        player.addPotionEffect(new PotionEffect(Potion.heal.id, 1, 3));
    }
}
