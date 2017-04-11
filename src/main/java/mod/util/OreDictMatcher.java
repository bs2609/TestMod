package mod.util;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class OreDictMatcher implements Predicate<IBlockState> {
	
	private final List<ItemStack> stacks;
	
	public OreDictMatcher(String name) {
		stacks = OreDictionary.getOres(name, false);
	}
	
	@Override
	public boolean apply(IBlockState input) {
		Block block = input.getBlock();
		for (ItemStack stack : stacks) {
			if (block != Block.getBlockFromItem(stack.getItem())) continue;
			int meta = stack.getMetadata();
			if (meta == OreDictionary.WILDCARD_VALUE || meta == block.damageDropped(input)) return true;
		}
		return false;
	}
}
