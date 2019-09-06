package com.maxwenger.aquastratum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;

@Mod(modid = AquaStratum.MODID, name = AquaStratum.NAME, version = AquaStratum.VERSION)
public class AquaStratum
{
    public static final String MODID = "aquastratum";
    public static final String NAME = "AquaStratum";
    public static final String VERSION = "1.0";

    private Minecraft mc;
    private EventSubscriptions eventSubscriptions;
    private DiverProfile diverProfile;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        eventSubscriptions = new EventSubscriptions();
        logger.info("Completed pre-init.");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        mc = Minecraft.getMinecraft();
        diverProfile = new DiverProfile(mc);
        InitEventSubscriptions();
        MinecraftForge.EVENT_BUS.register(eventSubscriptions);
        logger.info("Completed init.");
    }

    private void InitEventSubscriptions() {
        eventSubscriptions.setMC(mc);
        eventSubscriptions.setDiverProfile(diverProfile);
        eventSubscriptions.setPerdix(new Perdix());
    }
}
