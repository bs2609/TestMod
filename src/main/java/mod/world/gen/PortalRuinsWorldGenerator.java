package mod.world.gen;

import mod.util.BlockArea;
import mod.util.BlockStructure;
import mod.util.BlockUtils;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class PortalRuinsWorldGenerator extends WorldGenerator {
	
	private final IBlockState state0 = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
	private final IBlockState state1 = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);
	private final IBlockState state2 = Blocks.AIR.getDefaultState();
	
	private final BlockStructure portalStructure = new BlockStructure(state0, state1, state2);
	
	@Override
	public boolean generate(World world, Random random, BlockPos pos) {
		
		if (!pos.equals(world.getTopSolidOrLiquidBlock(pos))) return false;
		
		int w = 3, h = 4;
		BlockPos posX = pos.add(w, h, 0), posZ = pos.add(0, h, w);
		
		int yx = Math.abs(world.getTopSolidOrLiquidBlock(posX).getY() - pos.getY()) + 1;
		int yz = Math.abs(world.getTopSolidOrLiquidBlock(posZ).getY() - pos.getY()) + 1;
		
		BlockArea area = new BlockArea(pos, random.nextInt(yx + yz) < yz ? posX : posZ);
		BlockUtils.placeStructure(world, area, portalStructure);
		
		return true;
	}
}
