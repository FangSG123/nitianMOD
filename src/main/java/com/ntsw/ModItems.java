package com.ntsw;

import com.ntsw.item.*;
import com.ntsw.network.ClientAccumulatedDamageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {


    // 注册物品

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);


    // 注册特殊食物
    public static final RegistryObject<Item> HuangTaoGuangTou = ITEMS.register("htgt",
            () -> new HuangTaoGuanTou(new Item.Properties()));

    public static final RegistryObject<Item> DABIAN = ITEMS.register("dabian" ,DaBianItem::new);
    public static final RegistryObject<Item> SAOBA = ITEMS.register("saoba",
            () -> new SaobaItem(new Item.Properties()));
    public static final RegistryObject<Item> ZHANSHISAOBA = ITEMS.register("zhanshisaoba",
            () -> new ZhanshiSaobaItem(new Item.Properties()));
    public static final RegistryObject<Item> TIESUO_LIANHUAN = ITEMS.register("tiesuolianhuan",
            () -> new TiesuoLianhuanItem(new Item.Properties().stacksTo(1).durability(1)));


    public static final RegistryObject<Item> PDD = ITEMS.register("pdd", PddItem::new);

    public static final RegistryObject<Item> FeiJiPiao = ITEMS.register("feijipiao",()
            -> new FeiJiPiao(new Item.Properties()));

    public static final RegistryObject<Item> ZUANSHI  = ITEMS.register("zuanshi", ZuanShiItem::new);
    public static final RegistryObject<Item> JIFEN = ITEMS.register("jifen", JifenItem::new);
    public static final RegistryObject<Item> SHAN = ITEMS.register("shan",ShanItem::new);
    public static final RegistryObject<Item> JIU = ITEMS.register("jiu",
            () -> new JiuItem(new Item.Properties()));
    public static final RegistryObject<Item> GUDINGDAO = ITEMS.register("gudingdao",
            () -> new GudingdaoItem(Tiers.IRON, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> HUOSHA = ITEMS.register("huosha",
            () -> new HuoShaItem(Tiers.IRON, 3, -2.4F, new Item.Properties().durability(250)));

    public static final RegistryObject<Item> BAGUAJING_HELMET = ITEMS.register("baguajing",
            () -> new BaguajingItem(
                    ArmorMaterials.LEATHER, // 使用皮革材质
                    ArmorItem.Type.HELMET,  // 使用 ArmorItem.Type.HELMET
                    new Item.Properties()
            ));

    public static final RegistryObject<Item> BUSHI_GEMEN = ITEMS.register("bushigemen",
            () -> new BushiGeMenItem(new Item.Properties().stacksTo(1).durability(500)));

    public static final RegistryObject<Item> MUST_DIE_TOTEM = ITEMS.register("must_die_totem",
            () -> new Item(new Item.Properties().stacksTo(1)));




    public static final RegistryObject<Item> ZIMIN_SPAWN_EGG = ITEMS.register("zimin_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntitys.ZiMin_Entity, 0xFFFFFF, 0x000000, // 自定义颜色，例如白色和黑色
                    new Item.Properties()));
    public static final RegistryObject<Item> NAILONG_SPAWN_EGG = ITEMS.register("nailong_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntitys.NAILONG_ENTITY, 0xFFFFFF, 0x000000, // 自定义颜色，例如白色和黑色
                    new Item.Properties()));
    public static final RegistryObject<Item> HEIMANBA_SPAWN_EGG = ITEMS.register("heimanba_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntitys.HeiManBa_Entity, 0xFFFFFF, 0x000000, // 自定义颜色，例如白色和黑色
                    new Item.Properties()));
    public static final RegistryObject<Item> LAOHEI_SPAWN_EGG = ITEMS.register("laohei_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntitys.HeiManBa_Entity, 0xFFFFFF, 0x000000, // 自定义颜色，例如白色和黑色
                    new Item.Properties()));
    public static final RegistryObject<Item> NONGCHANGZHU_SPAWN_EGG = ITEMS.register("nongchangzhu_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntitys.NONGCHANGZHU, 0xFFFFFF, 0x000000, // 自定义颜色，例如白色和黑色
                    new Item.Properties()));
    public static final RegistryObject<Item> TRUMP_SPAWN_EGG = ITEMS.register("trump_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntitys.CHUANGJIANGUO, 0xFFFFFF, 0x000000, // 自定义颜色，例如白色和黑色
                    new Item.Properties()));
    public static final RegistryObject<Item> MCTOW_SPAWN_EGG = ITEMS.register("mctow_spawn_egg",
            () -> new MCToW(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FEIJIBEI_SPAWN_EGG = ITEMS.register("feijibei_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntitys.FeiJiBei, 0xFFFFFF, 0x000000, // 自定义颜色，例如白色和黑色
                    new Item.Properties()));

    public static final RegistryObject<Item> SHIKUAI_ITEM = ITEMS.register("shikuai",
            () -> new ShikuaiItem(new Item.Properties()));
    public static final RegistryObject<Item> PDD_ZHIDUN = ITEMS.register("pddzhidun",
            () -> new PDDzhidun(new Item.Properties().stacksTo(1)));


    public static final RegistryObject<Item> KNOCKBACK_WEAPON = ITEMS.register("jiuguding",
            () -> new KnockbackWeapon());
    public static final RegistryObject<Item> PIFENG = ITEMS.register("pifeng",
            () -> new PifengItem(new Item.Properties()));
    public static final RegistryObject<Item> DAIKUANGTUTENG = ITEMS.register("daikuangtuteng",
            () -> new DaikuangtutengItem(new Item.Properties()));
    public static final RegistryObject<Item> DAIKUANGTUTENGBIG = ITEMS.register("daikuangtutengbig",
            () -> new DaikuangtutengItem(new Item.Properties()));
    public static final RegistryObject<Item> DAIKUANGTUTENGMAX = ITEMS.register("daikuangtutengmax",
            () -> new DaikuangtutengItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
