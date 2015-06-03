package us.ichun.mods.ichunutil.common.grab;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.RandomStringUtils;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.grab.handlers.GrabbedFallingBlockHandler;
import us.ichun.mods.ichunutil.common.grab.handlers.GrabbedFireballHandler;

import java.util.ArrayList;
import java.util.EnumMap;

public class GrabHandler
{
    public String identifier;
    public EntityLivingBase grabber;
    public Entity grabbed;
    public int grabberId;
    public int grabbedId;
    public float grabDistance;
    public float yawTweak;
    public float pitchTweak;
    public boolean forceTerminate;

    public GrabHandler(EntityLivingBase grabber, Entity grabbed, float distance) //3.5F is a nice value for grab distance
    {
        this.identifier = RandomStringUtils.randomAscii(20);
        this.grabber = grabber;
        this.grabbed = grabbed;
        this.grabberId = grabber.getEntityId();
        this.grabbedId = grabbed.getEntityId();
        this.grabDistance = distance;
    }

    public GrabHandler(String identifier, int grabberId, int grabbedId, float dist)
    {
        this.identifier = identifier;
        this.grabberId = grabberId;
        this.grabbedId = grabbedId;
        this.grabDistance = dist;
    }

    public void update()
    {
        Vec3 pos = EntityHelperBase.getEntityPositionEyes(grabber, 1.0F);
        grabber.rotationYawHead += yawTweak;
        grabber.rotationPitch += pitchTweak;
        Vec3 look = grabber.getLookVec(); //this is getLook(1.0F);
        grabber.rotationYawHead -= yawTweak;
        grabber.rotationPitch -= pitchTweak;
        Vec3 grabPos = new Vec3(pos.xCoord + (look.xCoord * grabDistance), pos.yCoord + (look.yCoord * grabDistance), pos.zCoord + (look.zCoord * grabDistance));

        float distTolerance = grabToleranceTillTeleport();
        if(distTolerance > 0.0F && grabbed.getDistance(grabPos.xCoord, grabPos.yCoord, grabPos.zCoord) > distTolerance) //if grabber is too far from
        {
            grabbed.lastTickPosX = grabbed.prevPosX = grabber.posX;
            grabbed.lastTickPosY = grabbed.prevPosY = grabber.posY;
            grabbed.lastTickPosZ = grabbed.prevPosZ = grabber.posZ;
            grabbed.setPosition(grabbed.posX, grabbed.posY, grabbed.posZ);
        }
        EntityHelperBase.setVelocity(grabbed, (grabPos.xCoord - grabbed.posX), (grabPos.yCoord - ((grabbed.getEntityBoundingBox().minY + grabbed.getEntityBoundingBox().maxY) / 2D)), (grabPos.zCoord - grabbed.posZ));
        grabbed.fallDistance = -(float)(grabbed.motionY * grabbed.motionY);
        grabbed.onGround = false;
        grabbed.isAirBorne = true;
        grabbed.timeUntilPortal = 5;
        for(GrabbedEntityHandler handler : entityHandlers)
        {
            if(handler.eligible(grabbed))
            {
                handler.handle(this);
            }
        }
    }

    public void transfer(GrabHandler transfer)
    {
        transfer.grabDistance = this.grabDistance;
    }

    public float grabToleranceTillTeleport() //0F to disable the teleport
    {
        return 5.0F;
    }

    public boolean shouldTerminate()
    {
        return forceTerminate || grabber != null && grabbed != null && (grabbed.isDead || !grabber.isEntityAlive() || grabbed == grabber.ridingEntity || grabbed.dimension != grabber.dimension || grabbed instanceof EntityEnderman && grabbed.getDistanceToEntity(grabber) > grabDistance + 5D); //if the enderman is >5D of grab distance, let go of it.
    }

    public void terminate()
    {
    }

    //grabbed entities and their handlers

    public static EnumMap<Side, ArrayList<GrabHandler>> grabbedEntities = new EnumMap<Side, ArrayList<GrabHandler>>(Side.class){{
        put(Side.SERVER, new ArrayList<GrabHandler>());
        put(Side.CLIENT, new ArrayList<GrabHandler>());
    }};

    public static void tick(Side side)
    {
        ArrayList<GrabHandler> ents = grabbedEntities.get(side);
        for(int i = ents.size() - 1; i >= 0; i--)
        {
            GrabHandler handler = ents.get(i);
            if(handler.shouldTerminate())
            {
                handler.terminate();
                ents.remove(i);
            }
            else if(handler.grabber != null && handler.grabbed != null)
            {
                handler.update();
            }
        }
    }

    public static void grab(GrabHandler base, Side side)
    {
        ArrayList<GrabHandler> ents = grabbedEntities.get(side);
        ents.add(base);
    }

    public static void release(EntityLivingBase grabber, Side side)
    {
        ArrayList<GrabHandler> ents = grabbedEntities.get(side);
        for(int i = ents.size() - 1; i >= 0; i--)
        {
            GrabHandler handler = ents.get(i);
            if(handler.grabber == grabber)
            {
                handler.terminate();
                ents.remove(i);
            }
        }
    }

    public static ArrayList<GrabHandler> getHandlers(EntityLivingBase grabber, Side side)
    {
        ArrayList<GrabHandler> handlers = new ArrayList<GrabHandler>();

        ArrayList<GrabHandler> ents = grabbedEntities.get(side);
        for(int i = ents.size() - 1; i >= 0; i--)
        {
            GrabHandler handler = ents.get(i);
            if(handler.grabber == grabber)
            {
                handlers.add(handler);
            }
        }
        return handlers;
    }

    public static boolean hasHandlerType(EntityLivingBase grabber, Side side, Class<? extends GrabHandler> clz)
    {
        for(GrabHandler handler : getHandlers(grabber, side))
        {
            if(clz.isInstance(handler))
            {
                return true;
            }
        }
        return false;
    }

    //entity handlers section

    private static ArrayList<GrabbedEntityHandler> entityHandlers = new ArrayList<GrabbedEntityHandler>() {{
        add(new GrabbedFireballHandler());
        add(new GrabbedFallingBlockHandler());
    }};

    public static interface GrabbedEntityHandler
    {
        public boolean eligible(Entity grabbed);
        public void handle(GrabHandler grabHandler);
    }

    public static void registerEntityHandler(GrabbedEntityHandler handler)
    {
        entityHandlers.add(handler);
    }
}
