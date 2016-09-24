package mod.item;

import mod.portal.PortalUtils;
import mod.util.BlockArea;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PortalCompass extends BasicItem {
	
	public static final String NAME = "portal_compass";
	
	PortalCompass() {
		super(NAME, CreativeTabs.TOOLS);
		addPropertyOverride(new ResourceLocation("angle"), new PortalFinder());
	}
	
	private class PortalFinder implements IItemPropertyGetter {
		@Override
		@SideOnly(Side.CLIENT)
		public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
			if (worldIn == null || entityIn == null) return 0.0f;
			BlockArea portal = PortalUtils.getClosestPortal(worldIn, entityIn);
			if (portal == null) return 0.0f;
			Vec3d src = entityIn.getPositionVector(), dst = portal.getCentre();
			double dx = dst.xCoord - src.xCoord, dz = dst.zCoord - src.zCoord;
			double a = entityIn.rotationYaw / 180.0 + 0.5, b = Math.atan2(dz, dx) / Math.PI;
			return (float) ((a + b + 1.0) % 2.0 * 0.5);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}
}
