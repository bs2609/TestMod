package mod.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockAccessRemapper implements IBlockAccess {
	
	protected final IBlockAccess source;
	protected final IBlockAccess other;
	
	protected BlockAccessRemapper(IBlockAccess source, IBlockAccess other) {
		this.source = source;
		this.other = other;
	}
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return shouldRemap(pos) ? other.getTileEntity(remap(pos)) : source.getTileEntity(pos);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return shouldRemap(pos) ? other.getCombinedLight(remap(pos), lightValue) : source.getCombinedLight(pos, lightValue);
	}
	
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return shouldRemap(pos) ? other.getBlockState(remap(pos)) : source.getBlockState(pos);
	}
	
	@Override
	public boolean isAirBlock(BlockPos pos) {
		return shouldRemap(pos) ? other.isAirBlock(remap(pos)) : source.isAirBlock(pos);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Biome getBiome(BlockPos pos) {
		return shouldRemap(pos) ? other.getBiome(remap(pos)) : source.getBiome(pos);
	}
	
	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return shouldRemap(pos) ? other.getStrongPower(remap(pos), remap(direction)) : source.getStrongPower(pos, direction);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public WorldType getWorldType() {
		return source.getWorldType();
	}
	
	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		return shouldRemap(pos) ? other.isSideSolid(remap(pos), remap(side), _default) : source.isSideSolid(pos, side, _default);
	}
	
	protected abstract boolean shouldRemap(BlockPos pos);
	
	protected abstract BlockPos remap(BlockPos pos);
	
	protected abstract EnumFacing remap(EnumFacing facing);
}
