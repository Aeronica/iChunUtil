package me.ichun.mods.ichunutil.common.packet.mod;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class PacketRequestBlockEntityData extends AbstractPacket
{
    public int id;

    public PacketRequestBlockEntityData() {}

    public PacketRequestBlockEntityData(EntityBlock block)
    {
        id = block.getEntityId();
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(id);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        id = buffer.readInt();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        Entity ent = player.getEntityWorld().getEntityByID(id);
        if(ent instanceof EntityBlock)
        {
            NBTTagCompound tag = new NBTTagCompound();
            ent.writeToNBT(tag);
            iChunUtil.channel.sendTo(new PacketBlockEntityData(id, tag), player);
        }
    }

    @Override
    public Side receivingSide()
    {
        return Side.SERVER;
    }
}
