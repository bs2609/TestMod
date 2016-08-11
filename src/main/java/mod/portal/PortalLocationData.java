package mod.portal;

import mod.util.BlockArea;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.HashSet;
import java.util.Set;

public class PortalLocationData extends WorldSavedData {

	public static final String ID = "portalLocations";

	final Set<BlockArea> candidatePortals = new HashSet<BlockArea>();
	final Set<BlockArea> activePortals = new HashSet<BlockArea>();

	private PortalLocationData() {
		super(ID);
	}

	public PortalLocationData(String name) {
		super(name);
	}

	public static PortalLocationData get(World world) {

		MapStorage storage = world.getPerWorldStorage();
		PortalLocationData data = (PortalLocationData) storage.getOrLoadData(PortalLocationData.class, ID);

		if (data == null) {
			data = new PortalLocationData();
			storage.setData(ID, data);
		}

		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		NBTTagList candidate = nbt.getTagList("candidate", nbt.getId());
		for (int i = 0; i < candidate.tagCount(); i++) {
			candidatePortals.add(BlockArea.fromNBT(candidate.getCompoundTagAt(i)));
		}

		NBTTagList active = nbt.getTagList("active", nbt.getId());
		for (int i = 0; i < active.tagCount(); i++) {
			activePortals.add(BlockArea.fromNBT(active.getCompoundTagAt(i)));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		NBTTagList candidate = new NBTTagList();
		for (BlockArea area : candidatePortals) {
			candidate.appendTag(area.serializeNBT());
		}
		nbt.setTag("candidate", candidate);

		NBTTagList active = new NBTTagList();
		for (BlockArea area : activePortals) {
			active.appendTag(area.serializeNBT());
		}
		nbt.setTag("active", active);

		return nbt;
	}
}
