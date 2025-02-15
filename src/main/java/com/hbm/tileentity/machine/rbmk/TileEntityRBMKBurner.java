package com.hbm.tileentity.machine.rbmk;

import java.util.List;
import java.util.Random;

import api.hbm.fluid.IFluidStandardReceiver;
import com.hbm.interfaces.IFluidAcceptor;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.fluid.trait.FT_Flammable;
import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingType;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Gaseous;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Gaseous_ART;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.lib.Library;
import com.hbm.tileentity.machine.TileEntityHeatBoiler;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKConsole.ColumnType;
import com.hbm.util.ParticleUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class TileEntityRBMKBurner extends TileEntityRBMKBase implements IFluidAcceptor, IFluidStandardReceiver {
	
	public FluidTank tank;
	private int lastHot;
	
	public TileEntityRBMKBurner() {
		super();
		
		this.tank = new FluidTank(Fluids.GASOLINE, 8000);
	}
	
	@Override
	public void updateEntity() {
		int maxBurn = 10;
		if(!worldObj.isRemote) {
			
			if(this.worldObj.getTotalWorldTime() % 20 == 0)
				this.trySubscribe(tank.getTankType(), worldObj, xCoord, yCoord - 1, zCoord, Library.NEG_Y);
			maxBurn += maxBurn;

			if((int)(this.heat) > 19) {
				FT_Flammable trait = tank.getTankType().getTrait(FT_Flammable.class);
				if(tank.getTankType().hasTrait(FT_Flammable.class)){
						//int HeatProvided = Math.min(maxBurn, tank.getFill());
						//int heatProvided = (int)(this.heat + 1D);
						 int heating = Math.min(maxBurn, tank.getFill());
						{
						tank.setFill(tank.getFill() - heating );
						//Math.min(this.heat, maxBurn);
						long powerProd = tank.getTankType().getTrait(FT_Flammable.class).getHeatEnergy() * heating  / 1_000000; // divided by 1000 per mB
						this.heat += powerProd;
					}			
						this.lastHot = heating;	
				}
				
				if(lastHot > 0) {
					List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord + 4, zCoord, xCoord + 1, yCoord + 8, zCoord + 1));
					
					for(Entity e : entities) {
						e.setFire(5);
						e.attackEntityFrom(DamageSource.inFire, 10);
					}
				}
			} else {
				this.lastHot = 0;
			}
			
		} else {
			
			if(this.lastHot > 100) {
				for(int i = 0; i < 2; i++) {
					worldObj.spawnParticle("flame", xCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, yCoord + 4.5, zCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, 0, 0.2, 0);
					worldObj.spawnParticle("smoke", xCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, yCoord + 4.5, zCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, 0, 0.2, 0);
					ParticleUtil.spawnGasFlame(worldObj, xCoord + worldObj.rand.nextDouble(), yCoord + 4.5 + worldObj.rand.nextDouble(), zCoord + worldObj.rand.nextDouble(), worldObj.rand.nextGaussian() * 0.2, 0.1, worldObj.rand.nextGaussian() * 0.2);
				}
				
				if(worldObj.rand.nextInt(20) == 0)
					worldObj.spawnParticle("lava", xCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, yCoord + 4.5, zCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, 0, 0.0, 0);
			} else if(this.lastHot > 50) {
				for(int i = 0; i < 2; i++) {
					worldObj.spawnParticle("cloud", xCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, yCoord + 4.5, zCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, worldObj.rand.nextGaussian() * 0.05, 0.2, worldObj.rand.nextGaussian() * 0.05);
					worldObj.spawnParticle("flame", xCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, yCoord + 4.5, zCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, 0, 0.2, 0);
					worldObj.spawnParticle("smoke", xCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, yCoord + 4.5, zCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, 0, 0.2, 0);
					ParticleUtil.spawnGasFlame(worldObj, xCoord + worldObj.rand.nextDouble(), yCoord + 4.5 + worldObj.rand.nextDouble(), zCoord + worldObj.rand.nextDouble(), worldObj.rand.nextGaussian() * 0.2, 0.1, worldObj.rand.nextGaussian() * 0.2);
				}
			} else if(this.lastHot > 0) {
				
				if(worldObj.getTotalWorldTime() % 2 == 0)
					worldObj.spawnParticle("flame", xCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, yCoord + 4.5, zCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, 0, 0.2, 0);
					worldObj.spawnParticle("smoke", xCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, yCoord + 4.5, zCoord + 0.25 + worldObj.rand.nextDouble() * 0.5, 0, 0.2, 0);
					ParticleUtil.spawnGasFlame(worldObj, xCoord + worldObj.rand.nextDouble(), yCoord + 4.5 + worldObj.rand.nextDouble(), zCoord + worldObj.rand.nextDouble(), worldObj.rand.nextGaussian() * 0.2, 0.1, worldObj.rand.nextGaussian() * 0.2);
				
			}
			
		}
		
		super.updateEntity();
		
	}
	
	private int[] findCore(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		tank.readFromNBT(nbt, "fuel");
		this.lastHot = nbt.getInteger("burned");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		tank.writeToNBT(nbt, "fuel");
		nbt.setInteger("burned", this.lastHot);
	}

	@Override
	public ColumnType getConsoleType() {
		return ColumnType.BURNER;
	}

	@Override
	public void setFillForSync(int fill, int index) {
		tank.setFill(fill);
	}

	@Override
	public void setFluidFill(int fill, FluidType type) {
		if(type == tank.getTankType())
			tank.setFill(fill);
	}

	@Override
	public void setTypeForSync(FluidType type, int index) {
		tank.setTankType(type);
	}

	@Override
	public int getFluidFill(FluidType type) {
		return type == tank.getTankType() ? tank.getFill() : 0;
	}

	@Override
	public int getMaxFluidFill(FluidType type) {
		return type == tank.getTankType() ? tank.getMaxFill() : 0;
	}

	@Override
	public FluidTank[] getAllTanks() {
		return new FluidTank[] {tank};
	}

	@Override
	public FluidTank[] getReceivingTanks() {
		return new FluidTank[] {tank};
	}

}
