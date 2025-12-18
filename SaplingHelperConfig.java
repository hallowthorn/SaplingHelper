package com.saplinghelper;

import net.runelite.client.config.*;

@ConfigGroup("saplinghelper")
public interface SaplingHelperConfig extends Config
{
    @ConfigItem(
            keyName = "enableChain",
            name = "One-click seed → pot → water",
            description = "Plants and waters saplings with one click"
    )
    default boolean enableChain()
    {
        return true;
    }
}
