package com.ntsw;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEnchantments {

    // 使用 DeferredRegister 来注册附魔
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Main.MODID);

    // 注册 "火焰附加" 和 "击退" 附魔
    public static final RegistryObject<Enchantment> FIRE_ENCHANT = ENCHANTMENTS.register("fire_enchant", com.ntsw.enchantment.
FireEnchant::new);
    public static final RegistryObject<Enchantment> KNOCKBACK_ENCHANT = ENCHANTMENTS.register("knockback_enchant", com.ntsw.enchantment.
KnockbackEnchant::new);
    public static final RegistryObject<Enchantment> LOYALTY_ENCHANT = ENCHANTMENTS.register("loyalty_enchant", com.ntsw.enchantment.
LoyaltyEnchant::new);
    public static final RegistryObject<Enchantment> EFFICIENCY_ENCHANT = ENCHANTMENTS.register("efficiency_enchant", com.ntsw.enchantment.
EfficiencyEnchant::new);
    public static final RegistryObject<Enchantment> BLESSING_OF_VANISHING = ENCHANTMENTS.register("blessing_of_vanishing", com.ntsw.enchantment.
BlessingOfVanishingEnchant::new);
    public static final RegistryObject<Enchantment> LIGHTNING_ENCHANT = ENCHANTMENTS.register("lightning_enchant", com.ntsw.enchantment.
LightningEnchant::new);
    public static final RegistryObject<Enchantment> FINITE_ENCHANT = ENCHANTMENTS.register("finite_enchant", com.ntsw.enchantment.
FiniteEnchant::new);
    public static final RegistryObject<Enchantment> IMPACT_ENCHANT = ENCHANTMENTS.register("impact_enchant", com.ntsw.enchantment.
ImpactEnchant::new);
    public static final RegistryObject<Enchantment> DUNCUO_ENCHANT = ENCHANTMENTS.register("duncuo_enchant", com.ntsw.enchantment.
DunCuoEnchant::new);
    public static final RegistryObject<Enchantment> SHALUGUANGHUAN = ENCHANTMENTS.register("shaluguanghuan", com.ntsw.enchantment.
ShaluguanghuanEnchantment::new);
    public static final RegistryObject<Enchantment> JIYANXINGZHE = ENCHANTMENTS.register("jiyanxingzhe", com.ntsw.enchantment.
JiYanXingZheEnchantment::new);
    public static final RegistryObject<Enchantment> ZHISHENGJI = ENCHANTMENTS.register("zhishengji", com.ntsw.enchantment.
ZhiShengJiEnchantment::new);
    public static final RegistryObject<Enchantment> WAJUEJI = ENCHANTMENTS.register("wajueji", com.ntsw.enchantment.
WaJueJiEnchantment::new);
    public static final RegistryObject<Enchantment> SHUPIZHIREN = ENCHANTMENTS.register("shupizhiren", com.ntsw.enchantment.
ShuPiZhiRenEnchantment::new);
    public static final RegistryObject<Enchantment> TOUSHI = ENCHANTMENTS.register("toushi", com.ntsw.enchantment.
TouShiEnchant::new);
    public static final RegistryObject<Enchantment> SUO = ENCHANTMENTS.register("suo", com.ntsw.enchantment.
SuoEnchant::new);
    public static final RegistryObject<Enchantment> MEIHUO = ENCHANTMENTS.register("meihuo", com.ntsw.enchantment.
MeihuoEnchantment::new);
    public static final RegistryObject<Enchantment> DAOTOUJIUSHUI = ENCHANTMENTS.register("daotoujiushui", com.ntsw.enchantment.
DaoTouJiuShuiEnchant::new);
    public static final RegistryObject<Enchantment> QICHUANGQI = ENCHANTMENTS.register("qichuangqi", com.ntsw.enchantment.
QichuangqiEnchant::new);
    public static final RegistryObject<Enchantment> ZUANSHICHANZI = ENCHANTMENTS.register("zuanshichanzi",  com.ntsw.enchantment.
ZuanshiChanziEnchantment::new);
    public static final RegistryObject<Enchantment> WODISHENGAO = ENCHANTMENTS.register("wodishengao", com.ntsw.enchantment.
WodishengaoEnchantment::new);
    public static final RegistryObject<Enchantment> CHUANGGELIPEI = ENCHANTMENTS.register("chuanggelipei", com.ntsw.enchantment.
ChuanggelipeiEnchantment::new);
    public static final RegistryObject<Enchantment> LUODISHUI = ENCHANTMENTS.register("luodishui", com.ntsw.enchantment.
LuodishuiEnchantment::new);
    public static final RegistryObject<Enchantment> FUQIANGDUO = ENCHANTMENTS.register("fuqiangduo", com.ntsw.enchantment.
NegativeLootingEnchantment::new);
    public static final RegistryObject<Enchantment> FUSHIYUN = ENCHANTMENTS.register("fushiyun", com.ntsw.enchantment.
FuShiYunEnchantment::new);
    public static final RegistryObject<Enchantment> FUHUOYANFUJIA = ENCHANTMENTS.register("fuhuoyanfujia", com.ntsw.enchantment.
FuHuoYanFuJiaEnchantment::new);
    public static final RegistryObject<Enchantment> FUJINGYANXIUBU = ENCHANTMENTS.register("fujingyanxiubu", com.ntsw.enchantment.
FuJingYanXiuBuEnchantment::new);
    public static final RegistryObject<Enchantment> FUJITUI = ENCHANTMENTS.register("fujitui", com.ntsw.enchantment.
FuJiTuiEnchantment::new);
    public static final RegistryObject<Enchantment> FUFENGLI = ENCHANTMENTS.register("fufengli", com.ntsw.enchantment.
FuFengLiEnchantment::new);
    public static final RegistryObject<Enchantment> JIANRENPIANZUO = ENCHANTMENTS.register("jianrenpianzuo", com.ntsw.enchantment.
JianRenPianZuoEnchantment::new);
    public static final RegistryObject<Enchantment> GOUJITIAOQIANG = ENCHANTMENTS.register("goujitiaoqiang", com.ntsw.enchantment.
GoujitiaoqiangEnchantment::new);



    // 注册方法，在 Main 类的构造函数中调用
    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
