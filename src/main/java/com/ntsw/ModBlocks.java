package com.ntsw;

import com.ntsw.block.NTNBlock;
import com.ntsw.block.LaughObsidianBlock;
import com.ntsw.block.NonRespawnAnchorBlock;
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
    public static final RegistryObject<Block> NTN_BLOCK = BLOCKS.register("ntn_block", NTNBlock::new);

    public static final RegistryObject<Block> NON_RESPAWN_ANCHOR = BLOCKS.register(
            "non_respawn_anchor",
            () -> new NonRespawnAnchorBlock()
    );

    public static final RegistryObject<Item> LAUGH_OBSIDIAN_ITEM = ITEMS.register("laughobsidian",
            () -> new BlockItem(LAUGH_OBSIDIAN.get(), new Item.Properties()));
    public static final RegistryObject<Item> LAUGH_PORTAL_ITEM = ITEMS.register("laugh_portal",
            () -> new BlockItem(LAUGH_PORTAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> NTN_BLOCK_ITEM = ITEMS.register("ntn_block",
            () -> new BlockItem(ModBlocks.NTN_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> NON_RESPAWN_ANCHOR_ITEM = ITEMS.register("non_respawn_anchor",
            () -> new BlockItem(ModBlocks.NON_RESPAWN_ANCHOR.get(), new Item.Properties()));


}
