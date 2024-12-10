package com.ntsw.effect;

import com.ntsw.ModPotions;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class FaqingBrewingRecipe implements IBrewingRecipe {

    @Override
    public boolean isInput(ItemStack input) {
        // 检查输入是否为 Awkward Potion
        Potion potion = PotionUtils.getPotion(input);
        return input.getItem() == Items.POTION && potion == Potions.THICK;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        // 检查配料是否为 Blaze Rod
        return ingredient.getItem() == Items.BLAZE_ROD;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (isInput(input) && isIngredient(ingredient)) {
            // 创建 FAQING_POTION 的 ItemStack
            ItemStack output = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.FAQING_POTION.get());
            return output;
        }
        return ItemStack.EMPTY;
    }
}
