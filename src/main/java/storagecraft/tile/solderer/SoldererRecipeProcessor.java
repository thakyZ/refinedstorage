package storagecraft.tile.solderer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraftItems;
import storagecraft.item.ItemProcessor;

public class SoldererRecipeProcessor implements ISoldererRecipe
{
	private int type;

	public SoldererRecipeProcessor(int type)
	{
		this.type = type;
	}

	@Override
	public ItemStack getRow(int row)
	{
		if (row == 0)
		{
			switch (type)
			{
				case ItemProcessor.TYPE_BASIC:
					return new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_BASIC);
				case ItemProcessor.TYPE_IMPROVED:
					return new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_IMPROVED);
				case ItemProcessor.TYPE_ADVANCED:
					return new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_ADVANCED);
			}
		}
		else if (row == 1)
		{
			return new ItemStack(Items.redstone);
		}
		else if (row == 2)
		{
			return new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_SILICON);
		}

		return null;
	}

	@Override
	public ItemStack getResult()
	{
		return new ItemStack(StorageCraftItems.PROCESSOR, 1, type);
	}

	@Override
	public int getDuration()
	{
		return 200;
	}
}
