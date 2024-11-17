package com.ntsw;


import com.ntsw.block.LaughObsidianBlock;
import net.minecraft.world.level.block.Block;
import com.ntsw.block.LaughPortalBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

    public static final RegistryObject<Block> LAUGH_OBSIDIAN = BLOCKS.register("laughobsidian",
            () -> new LaughObsidianBlock());
    public static final RegistryObject<Block> LAUGH_PORTAL = BLOCKS.register("laugh_portal",
            LaughPortalBlock::new);

    public static final RegistryObject<Item> LAUGH_OBSIDIAN_ITEM = ITEMS.register("laughobsidian",
            () -> new BlockItem(LAUGH_OBSIDIAN.get(), new Item.Properties()));
    public static final RegistryObject<Item> LAUGH_PORTAL_ITEM = ITEMS.register("laugh_portal",
            () -> new BlockItem(LAUGH_PORTAL.get(), new Item.Properties()));

}
