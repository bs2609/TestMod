package mod.crafting;

import mod.block.ModBlocks;
import mod.item.ModItems;
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
		addPortalCompassRecipe();
	}

	private static void addPortalFrameRecipes() {
		
		GameRegistry.addRecipe(new ItemStack(ModBlocks.portalFrame, 4, ModBlocks.portalFrame.getMetaForType(PortalType.IN)),
				"ABA", "BCB", "ABA",
				'A', new ItemStack(Blocks.STONEBRICK, 1, BlockStoneBrick.CHISELED_META),
				'B', Items.REDSTONE,
				'C', Blocks.OBSIDIAN
		);

		GameRegistry.addRecipe(new ItemStack(ModBlocks.portalFrame, 4, ModBlocks.portalFrame.getMetaForType(PortalType.OUT)),
				"ABA", "BCB", "ABA",
				'A', new ItemStack(Blocks.STONEBRICK, 1, BlockStoneBrick.CHISELED_META),
				'B', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
				'C', Blocks.OBSIDIAN
		);
	}
	
	private static void addPortalBlockRecipe() {
		
		GameRegistry.addRecipe(new ItemStack(ModBlocks.portalBlock, 1),
				"ABA", "BCB", "ABA",
				'A', new ItemStack(ModBlocks.portalFrame, 1, ModBlocks.portalFrame.getMetaForType(PortalType.IN)),
				'B', Blocks.QUARTZ_BLOCK,
				'C', Items.ENDER_PEARL
		);
	}
	
	private static void addPortalCompassRecipe() {
		
		GameRegistry.addRecipe(new ItemStack(ModItems.portalCompass, 1),
				"ABC", "BDB", "CBA",
				'A', Items.REDSTONE,
				'B', Items.QUARTZ,
				'C', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
				'D', Items.COMPASS
		);
	}
}
