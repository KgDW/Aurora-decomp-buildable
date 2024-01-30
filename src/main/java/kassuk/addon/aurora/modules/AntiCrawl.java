package kassuk.addon.aurora.modules;

import kassuk.addon.aurora.BlackOut;
import kassuk.addon.aurora.BlackOutModule;

public class AntiCrawl extends BlackOutModule
{
    public AntiCrawl() {
        super(BlackOut.BLACKOUT, "Anti Crawl", "Doesn't crawl or sneak when in low space (should be used on 1.12.2).");
    }
}
