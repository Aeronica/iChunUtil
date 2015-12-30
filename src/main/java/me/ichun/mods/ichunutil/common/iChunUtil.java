package me.ichun.mods.ichunutil.common;


import me.ichun.mods.ichunutil.client.core.event.EventHandlerClient;
import me.ichun.mods.ichunutil.common.core.Logger;
import me.ichun.mods.ichunutil.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.ichunutil.common.core.event.EventHandlerServer;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.util.List;

@Mod(modid = iChunUtil.MOD_NAME, name = iChunUtil.MOD_NAME,
        version = iChunUtil.VERSION,
        guiFactory = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:Forge@[" + iChunUtil.REQ_FORGE_MAJOR + "." + iChunUtil.REQ_FORGE_MINOR + "." + iChunUtil.REQ_FORGE_REVISION + "." + iChunUtil.REQ_FORGE_BUILD + ",99999." + (iChunUtil.REQ_FORGE_MINOR + 1) + ".0.0)"
)
//hashmap.put(Type.SKIN, new MinecraftProfileTexture(String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(p_152790_1_.getName()) }), null));
public class iChunUtil
{
    //Stuff to bump every update
    public static final String VERSION_OF_MC = "1.8.9";
    public static final int VERSION_MAJOR = 6;
    public static final String VERSION = VERSION_MAJOR + ".0.0";

    public static final String MOD_NAME = "iChunUtil";

    public static final int REQ_FORGE_MAJOR = 11;
    public static final int REQ_FORGE_MINOR = ForgeVersion.minorVersion;
    public static final int REQ_FORGE_REVISION = 0;
    public static final int REQ_FORGE_BUILD = 1654;

    public static final Logger LOGGER = Logger.createLogger(MOD_NAME);

    @Mod.Instance(MOD_NAME)
    public static iChunUtil instance;

    @SidedProxy(clientSide = "me.ichun.mods.ichunutil.client.core.ProxyClient", serverSide = "me.ichun.mods.ichunutil.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    //Mod stuffs
    public static Config config;

    public static PacketChannel channel;

    public static EventHandlerServer eventHandlerServer;
    public static EventHandlerClient eventHandlerClient;

    public static Block blockCompactPorkchop;
    public static List<ItemStack> oreDictBlockCompactRawPorkchop;

    private static boolean hasPostInit;

    public static boolean userIsPatron;

    public class Config extends ConfigBase
    {
        @ConfigProp(category = "clientOnly", side = Side.CLIENT, changeable = false)
        @IntBool
        public int enableStencils = 1;

        //Modules
        @ConfigProp(category = "block", useSession = true, module = "compressedPorkchop")
        @IntBool
        public int enableCompactPorkchop = 1;

        @ConfigProp(side = Side.CLIENT, module = "ding")
        @IntBool
        public int dingEnabled = 1;

        @ConfigProp(side = Side.CLIENT, module = "ding")
        public String dingSoundName = "random.orb";

        @ConfigProp(side = Side.CLIENT, module = "ding")
        @IntMinMax(min = -5000, max = 5000)
        public int dingSoundPitch = 100;

        @ConfigProp(module = "eula")
        public String eulaAcknowledged = "";

        //End Modules

        public Config(File file)
        {
            super(file);
        }

        @Override
        public String getModId()
        {
            return iChunUtil.MOD_NAME.toLowerCase();
        }

        @Override
        public String getModName()
        {
            return iChunUtil.MOD_NAME;
        }

        @Override
        public void onReceiveSession()
        {
            List<ItemStack> compactPorkchops = oreDictBlockCompactRawPorkchop;
            if(compactPorkchops.size() == 1 && compactPorkchops.get(0).getItem() != null && Block.getBlockFromItem(compactPorkchops.get(0).getItem()) == (blockCompactPorkchop)) //Only handle the recipe if it's the only oredict entry for the block.
            {
                List recipes = CraftingManager.getInstance().getRecipeList();
                for(int i = recipes.size() - 1; i >= 0; i--)
                {
                    if(recipes.get(i) instanceof ShapedRecipes)
                    {
                        ShapedRecipes recipe = (ShapedRecipes)recipes.get(i);
                        if(recipe.getRecipeOutput().isItemEqual(new ItemStack(blockCompactPorkchop)))
                        {
                            recipes.remove(i);
                        }
                    }
                }

                if(enableCompactPorkchop == 1)
                {
                    GameRegistry.addRecipe(new ItemStack(blockCompactPorkchop), "PPP", "PPP", "PPP", 'P', Items.porkchop);
                    GameRegistry.addShapelessRecipe(new ItemStack(Items.porkchop, 9), blockCompactPorkchop);
                }
            }
        }
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        ObfHelper.detectObfuscation();

        config = (Config)ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event)
    {
        hasPostInit = true;

        proxy.postInit();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
    }

    public boolean hasPostInit()
    {
        return hasPostInit;
    }
}
