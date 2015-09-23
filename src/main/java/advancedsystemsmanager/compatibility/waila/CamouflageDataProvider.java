package advancedsystemsmanager.compatibility.waila;

import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CamouflageDataProvider implements IWailaDataProvider
{
    private List<IWailaDataProvider> cachedHeadProviders;
    private List<IWailaDataProvider> cachedBodyProviders;
    private List<IWailaDataProvider> cachedTailProviders;

    public void setCachedProviders()
    {
        cachedHeadProviders = new ArrayList<IWailaDataProvider>();
        for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getHeadProviders(BlockRegistry.cableCamouflage).values()){
            cachedHeadProviders.addAll(providersList);
        }
        cachedBodyProviders = new ArrayList<IWailaDataProvider>();
        for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getBodyProviders(BlockRegistry.cableCamouflage).values()){
            cachedBodyProviders.addAll(providersList);
        }
        cachedTailProviders = new ArrayList<IWailaDataProvider>();
        for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getTailProviders(BlockRegistry.cableCamouflage).values()){
            cachedTailProviders.addAll(providersList);
        }
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler iWailaConfigHandler)
    {
        if (cachedBodyProviders == null)
        {
            setCachedProviders();
        }
        TileEntity te = accessor.getTileEntity();
        if (te != null && !isShiftDown())
        {
            TileEntityCamouflage camouflage = BlockRegistry.cableCamouflage.getTileEntity(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
            if (camouflage != null && camouflage.hasSideBlock(accessor.getPosition().sideHit))
            {
                Block block = camouflage.getSideBlock(accessor.getPosition().sideHit);
                if (block != null)
                {
                    int meta = camouflage.getSideMetadata(accessor.getPosition().sideHit);
                    return new ItemStack(block, 1, block.damageDropped(meta));
                }
            }
        }
        return new ItemStack(accessor.getBlock(), 1, accessor.getBlock().damageDropped(accessor.getMetadata()));
    }

    @SideOnly(Side.CLIENT)
    private boolean isShiftDown()
    {
        return GuiScreen.isShiftKeyDown();
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler iWailaConfigHandler)
    {
        TileEntity te = accessor.getTileEntity();
        if (accessor instanceof DataAccessorCommon && te != null && !isShiftDown())
        {
            TileEntityCamouflage camouflage = BlockRegistry.cableCamouflage.getTileEntity(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
            if (camouflage != null && camouflage.hasSideBlock(accessor.getPosition().sideHit))
            {
                ((DataAccessorCommon) accessor).block = camouflage.getSideBlock(accessor.getPosition().sideHit);
                ((DataAccessorCommon) accessor).metadata = camouflage.getSideMetadata(accessor.getPosition().sideHit);
                for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getHeadProviders(accessor.getBlock()).values())
                {
                    for(IWailaDataProvider provider : providersList)
                    {
                        if (!cachedHeadProviders.contains(provider))
                        {
                            provider.getWailaBody(itemStack, list, accessor, iWailaConfigHandler);
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler iWailaConfigHandler)
    {
        TileEntity te = accessor.getTileEntity();
        if (accessor instanceof DataAccessorCommon && te != null && !isShiftDown())
        {
            TileEntityCamouflage camouflage = BlockRegistry.cableCamouflage.getTileEntity(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
            if (camouflage != null && camouflage.hasSideBlock(accessor.getPosition().sideHit))
            {
                ((DataAccessorCommon) accessor).block = camouflage.getSideBlock(accessor.getPosition().sideHit);
                ((DataAccessorCommon) accessor).metadata = camouflage.getSideMetadata(accessor.getPosition().sideHit);
                for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getBodyProviders(accessor.getBlock()).values())
                {
                    for(IWailaDataProvider provider : providersList)
                    {
                        if (!cachedBodyProviders.contains(provider))
                        {
                            provider.getWailaBody(itemStack, list, accessor, iWailaConfigHandler);
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler iWailaConfigHandler)
    {
        TileEntity te = accessor.getTileEntity();
        if (accessor instanceof DataAccessorCommon && te != null && !isShiftDown())
        {
            TileEntityCamouflage camouflage = BlockRegistry.cableCamouflage.getTileEntity(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
            if (camouflage != null && camouflage.hasSideBlock(accessor.getPosition().sideHit))
            {
                ((DataAccessorCommon) accessor).block = camouflage.getSideBlock(accessor.getPosition().sideHit);
                ((DataAccessorCommon) accessor).metadata = camouflage.getSideMetadata(accessor.getPosition().sideHit);
                for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getTailProviders(accessor.getBlock()).values())
                {
                    for(IWailaDataProvider provider : providersList)
                    {
                        if (!cachedTailProviders.contains(provider))
                        {
                            provider.getWailaTail(itemStack, list, accessor, iWailaConfigHandler);
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP entityPlayerMP, TileEntity tileEntity, NBTTagCompound nbtTagCompound, World world, int i, int i1, int i2)
    {
        return null;
    }
}
