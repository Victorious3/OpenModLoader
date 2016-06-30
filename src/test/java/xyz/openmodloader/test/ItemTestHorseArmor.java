package xyz.openmodloader.test;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.openmodloader.item.HorseArmor;
import xyz.openmodloader.registry.GameRegistry;

public class ItemTestHorseArmor extends Item implements HorseArmor {

    public static final HorseArmorType TYPE = new HorseArmorType(5, "omltest", "omltest");
    
    public ItemTestHorseArmor () {      
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.setUnlocalizedName("oml.horsearmor");
    }
    
    @Override
    public HorseArmorType getArmorType(ItemStack stack) {
        return TYPE;
    }

    @Override
    public String getArmorTexture(EntityLivingBase wearer, ItemStack stack) {
        return "omltest:textures/entity/horse/armor_test.png";
    }
}
