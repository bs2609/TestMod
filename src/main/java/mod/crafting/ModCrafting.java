package mod.crafting;

import mod.block.ModBlocks;
import mod.portal.PortalType;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModCrafting {

	public static void init() {
		addPortalFrameRecipes();
		addPortalBlockRecipe();
	}

	private static void addPortalFrameRecipes() {
		
		GameRegistry.addRecipe(new ItemStack(ModBlocks.portalFrameBlock, 4, ModBlocks.portalFrameBlock.getMetaForType(PortalType.IN)),
				"ABA", "BCB", "ABA",
				'A', new ItemStack(Blocks.STONEBRICK, 1, BlockStoneBrick.CHISELED_META),
				'B', Items.REDSTONE,
				'C', Blocks.OBSIDIAN
		);

		GameRegistry.addRecipe(new ItemStack(ModBlocks.portalFrameBlock, 4, ModBlocks.portalFrameBlock.getMetaForType(PortalType.OUT)),
				"ABA", "BCB", "ABA",
				'A', new ItemStack(Blocks.STONEBRICK, 1, BlockStoneBrick.CHISELED_META),
				'B', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
				'C', Blocks.OBSIDIAN
		);
	}
	
	private static void addPortalBlockRecipe() {
		
		GameRegistry.addRecipe(new ItemStack(ModBlocks.portalBlock, 1),
				"ABA", "BCB", "ABA",
				'A', new ItemStack(ModBlocks.portalFrameBlock, 1, ModBlocks.portalFrameBlock.getMetaForType(PortalType.IN)),
				'B', Blocks.QUARTZ_BLOCK,
				'C', Items.ENDER_PEARL
		);
	}
}
