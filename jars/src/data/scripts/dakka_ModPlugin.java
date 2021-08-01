package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import data.scripts.plugins.dakka_ProjectileGlowPlugin;

public class dakka_ModPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() throws Exception {
        dakka_ProjectileGlowPlugin.getGlowData();
    }
}
