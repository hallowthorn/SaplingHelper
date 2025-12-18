package com.saplinghelper;

import com.google.inject.Provides;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
        name = "Sapling Helper"
)
public class SaplingHelperPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private SaplingHelperConfig config;

    @Provides
    SaplingHelperConfig provideConfig(ConfigManager manager)
    {
        return manager.getConfig(SaplingHelperConfig.class);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!config.enableChain())
            return;

        if (!event.getMenuOption().equals("Use"))
            return;

        if (!isTreeSeed(event.getItemId()))
            return;

        event.consume();

        clientThread.invokeLater(this::plantAndWater);
    }

    private void plantAndWater()
    {
        Item seed = findSeed();
        Item pot = findItem(ItemID.PLANT_POT);
        Item can = findWateringCan();

        if (seed == null || pot == null || can == null)
            return;

        use(seed, pot);

        clientThread.invokeLater(() ->
        {
            Item planted = findItem(ItemID.PLANT_POT_WITH_SEED);
            if (planted == null)
                return;

            use(can, planted);
        });
    }

    private void use(Item from, Item to)
    {
        client.invokeMenuAction(
                "Use",
                "",
                to.getId(),
                MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
                to.getSlot(),
                WidgetInfo.INVENTORY.getId()
        );
    }

    private Item findSeed()
    {
        ItemContainer inv = client.getItemContainer(InventoryID.INVENTORY);
        if (inv == null)
            return null;

        for (Item item : inv.getItems())
            if (isTreeSeed(item.getId()))
                return item;

        return null;
    }

    private Item findItem(int id)
    {
        ItemContainer inv = client.getItemContainer(InventoryID.INVENTORY);
        if (inv == null)
            return null;

        for (Item item : inv.getItems())
            if (item.getId() == id)
                return item;

        return null;
    }

    private Item findWateringCan()
    {
        ItemContainer inv = client.getItemContainer(InventoryID.INVENTORY);
        if (inv == null)
            return null;

        for (Item item : inv.getItems())
            if (item.getId() >= ItemID.WATERING_CAN
                    && item.getId() <= ItemID.WATERING_CAN8)
                return item;

        return null;
    }

    private boolean isTreeSeed(int id)
    {
        return id == ItemID.ACORN
                || id == ItemID.WILLOW_SEED
                || id == ItemID.MAPLE_SEED
                || id == ItemID.YEW_SEED
                || id == ItemID.MAGIC_SEED
                || id == ItemID.APPLE_TREE_SEED
                || id == ItemID.ORANGE_TREE_SEED;
    }
}
