package mod.util;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashSet;
import java.util.Set;

public class OreDictMatcher implements Predicate<IBlockState> {
	
	private final Set<Block> blocks = new HashSet<Block>();
	
	public OreDictMatcher(String name) {
		for (ItemStack stack : OreDictionary.getOres(name, false)) {
			blocks.add(Block.getBlockFromItem(stack.getItem()));
		}
	}
	
	@Override
	public boolean apply(IBlockState input) {
		return input != null && blocks.contains(input.getBlock());
	}
}
