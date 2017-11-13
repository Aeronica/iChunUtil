package me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.chunk;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ListedRenderChunkWorldPortal extends ListedRenderChunk implements IRenderChunkWorldPortal
{
    public ArrayList<BlockPos> poses;
    public ArrayList<EnumFacing> faces;

    public ListedRenderChunkWorldPortal(World worldIn, RenderGlobal renderGlobalIn, int indexIn)
    {
        super(worldIn, renderGlobalIn, indexIn);
        this.poses = new ArrayList<>();
        this.poses.add(BlockPos.ORIGIN);
        this.faces = new ArrayList<>();
        this.faces.add(EnumFacing.UP);
    }

    @Override
    public void setCurrentPositionsAndFaces(ArrayList<BlockPos> poses, ArrayList<EnumFacing> faces)
    {
        this.poses = poses;
        this.faces = faces;
    }

    @Override
    public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        CompiledChunk compiledchunk = new CompiledChunk();
        int i = 1;
        BlockPos blockpos = this.position;
        BlockPos blockpos1 = blockpos.add(15, 15, 15);
        generator.getLock().lock();

        try
        {
            if(generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
            {
                return;
            }

            generator.setCompiledChunk(compiledchunk);
        }
        finally
        {
            generator.getLock().unlock();
        }

        VisGraph lvt_9_1_ = new VisGraph();
        HashSet lvt_10_1_ = Sets.newHashSet();

        if(!this.worldView.isEmpty())
        {
            ++renderChunksUpdated;
            boolean[] aboolean = new boolean[BlockRenderLayer.values().length];
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos1))
            {
                boolean noRender = true;
                for(int i1 = 0; i1 < faces.size(); i1++)
                {
                    BlockPos pos = poses.get(i1);
                    EnumFacing face = faces.get(i1);
                    if(!(face.getFrontOffsetX() < 0 && blockpos$mutableblockpos.getX() > pos.getX() || face.getFrontOffsetX() > 0 && blockpos$mutableblockpos.getX() < pos.getX() || face.getFrontOffsetY() < 0 && blockpos$mutableblockpos.getY() > pos.getY() || face.getFrontOffsetY() > 0 && blockpos$mutableblockpos.getY() < pos.getY() || face.getFrontOffsetZ() < 0 && blockpos$mutableblockpos.getZ() > pos.getZ() || face.getFrontOffsetZ() > 0 && blockpos$mutableblockpos.getZ() < pos.getZ())) // this means noRender = true
                    {
                        noRender = false;
                        break;
                    }
                }

                IBlockState iblockstate = this.worldView.getBlockState(blockpos$mutableblockpos);
                Block block = iblockstate.getBlock();

                if(iblockstate.isOpaqueCube())
                {
                    lvt_9_1_.setOpaqueCube(blockpos$mutableblockpos);
                }

                if(!noRender && block.hasTileEntity(iblockstate))
                {
                    TileEntity tileentity = this.worldView.getTileEntity(blockpos$mutableblockpos, Chunk.EnumCreateEntityType.CHECK);

                    if (tileentity != null)
                    {
                        TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileentity);

                        if (tileentityspecialrenderer != null)
                        {
                            compiledchunk.addTileEntity(tileentity);

                            if (tileentityspecialrenderer.isGlobalRenderer(tileentity))
                            {
                                lvt_10_1_.add(tileentity);
                            }
                        }
                    }
                }

                for(BlockRenderLayer blockrenderlayer1 : BlockRenderLayer.values())
                {
                    if(!block.canRenderInLayer(iblockstate, blockrenderlayer1))
                    {
                        continue;
                    }
                    net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockrenderlayer1);
                    int j = blockrenderlayer1.ordinal();

                    if(block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
                    {
                        net.minecraft.client.renderer.BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);

                        if(!compiledchunk.isLayerStarted(blockrenderlayer1))
                        {
                            compiledchunk.setLayerStarted(blockrenderlayer1);
                            this.preRenderBlocks(bufferbuilder, blockpos);
                        }

                        if(!noRender && blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, this.worldView, bufferbuilder))
                        {
                            aboolean[j] |= true;
                        }
                    }
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
            }

            for(BlockRenderLayer blockrenderlayer : BlockRenderLayer.values())
            {
                if(aboolean[blockrenderlayer.ordinal()])
                {
                    compiledchunk.setLayerUsed(blockrenderlayer);
                }

                if(compiledchunk.isLayerStarted(blockrenderlayer))
                {
                    this.postRenderBlocks(blockrenderlayer, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer), compiledchunk);
                }
            }
        }

        compiledchunk.setVisibility(lvt_9_1_.computeVisibility());
        this.lockCompileTask.lock();

        try
        {
            Set<TileEntity> set = Sets.newHashSet(lvt_10_1_);
            Set<TileEntity> set1 = Sets.newHashSet(this.setTileEntities);
            set.removeAll(this.setTileEntities);
            set1.removeAll(lvt_10_1_);
            this.setTileEntities.clear();
            this.setTileEntities.addAll(lvt_10_1_);
            this.renderGlobal.updateTileEntities(set1, set);
        }
        finally
        {
            this.lockCompileTask.unlock();
        }
    }
}
