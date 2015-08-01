package advancedsystemsmanager.compatibility.rf.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.Key;
import advancedsystemsmanager.compatibility.rf.RFCompat;
import advancedsystemsmanager.compatibility.rf.menus.MenuRF;
import advancedsystemsmanager.compatibility.rf.menus.MenuRFAmount;
import advancedsystemsmanager.compatibility.rf.menus.MenuRFTarget;
import advancedsystemsmanager.compatibility.thaumcraft.menus.MenuAspect;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.buffers.Buffer;
import advancedsystemsmanager.flow.execution.commands.CommandOutput;
import advancedsystemsmanager.flow.menus.Menu;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.util.SystemCoord;
import cofh.api.energy.IEnergyReceiver;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommandRFOutput extends CommandOutput<Integer>
{
    public CommandRFOutput()
    {
        super(RF_OUTPUT, Names.RF_OUTPUT, Buffer.RF);
    }

    @Override
    protected void outputFromBuffer(FlowComponent component, IBuffer<Integer> buffer)
    {
        MenuRFTarget target = (MenuRFTarget) component.getMenus().get(1);
        int maxAmount = ((MenuRFAmount) component.getMenus().get(2)).getAmount();
        maxAmount = maxAmount == 0 ? Integer.MAX_VALUE : maxAmount;
        for (SystemCoord block : getContainers(component.getManager(), (MenuRF)component.getMenus().get(0)))
        {
            int max = maxAmount;
            IEnergyReceiver receiver = (IEnergyReceiver)block.getTileEntity();
            for (int side = 0; side < 6; side++)
            {
                if (target.isActive(side) && receiver.canConnectEnergy(ForgeDirection.VALID_DIRECTIONS[side]))
                {
                    Iterator<Map.Entry<Key<Integer>, IBufferElement<Integer>>> itr = buffer.getOrderedIterator();
                    while (itr.hasNext() && max > 0)
                    {
                        IBufferElement<Integer> rfBufferElement = itr.next().getValue();
                        int amount = rfBufferElement.getSizeLeft();
                        amount = receiver.receiveEnergy(ForgeDirection.VALID_DIRECTIONS[side], amount, true);
                        rfBufferElement.reduceBufferAmount(amount);
                        if (rfBufferElement.getSizeLeft() == 0)
                            rfBufferElement.remove();
                    }
                }
            }
        }
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuRF(component, RFCompat.RF_RECEIVER));
        menus.add(new MenuRFTarget(component));
        menus.add(new MenuRFAmount(component));
    }
}
