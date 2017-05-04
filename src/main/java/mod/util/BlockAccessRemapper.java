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

public class BlockAccessRemapper implements IBlockAccess {
	
	protected final IBlockAccess source;
	
	protected BlockAccessRemapper(IBlockAccess source) {
		this.source = source;
	}
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return source.getTileEntity(pos);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return source.getCombinedLight(pos, lightValue);
	}
	
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return source.getBlockState(pos);
	}
	
	@Override
	public boolean isAirBlock(BlockPos pos) {
		return source.isAirBlock(pos);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Biome getBiome(BlockPos pos) {
		return source.getBiome(pos);
	}
	
	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return source.getStrongPower(pos, direction);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public WorldType getWorldType() {
		return source.getWorldType();
	}
	
	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		return source.isSideSolid(pos, side, _default);
	}
}
