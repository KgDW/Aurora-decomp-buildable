package kassuk.addon.aurora;

import com.mojang.logging.LogUtils;
import kassuk.addon.aurora.commands.BlackoutGit;
import kassuk.addon.aurora.commands.Coords;
import kassuk.addon.aurora.globalsettings.*;
import kassuk.addon.aurora.hud.*;
import kassuk.addon.aurora.modules.*;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class BlackOut extends MeteorAddon
{
    public static final Logger LOG;
    public static final Category BLACKOUT;
    public static final Category SETTINGS;
    public static final HudGroup HUD_BLACKOUT;
    public static final String BLACKOUT_NAME = "Aurora";
    public static final String BLACKOUT_VERSION = "1.0.1";
    public static final String COLOR = "Color is the visual perception of different wavelengths of light as hue, saturation, and brightness";

    public void onInitialize() {
        BlackOut.LOG.info("Initializing Blackout");
        this.initializeModules(Modules.get());
        this.initializeSettings(Modules.get());
        this.initializeCommands();
        this.initializeHud(Hud.get());
    }

    private void initializeModules(final Modules modules) {
        modules.add(new AutoAnchor());
        modules.add(new AnteroTaateli());
        modules.add(new AntiAim());
        modules.add(new AntiCrawl());
        modules.add(new AutoCraftingTable());
        modules.add(new AutoCrystalPlus());
        modules.add(new AutoEz());
        modules.add(new Automation());
        modules.add(new AutoMend());
        modules.add(new AutoMine());
        modules.add(new AutoMoan());
        modules.add(new AutoPearl());
        this.initializeAutoPVP(modules);
        modules.add(new AutoTrapPlus());
        modules.add(new BedAuraPlus());
        modules.add(new Blocker());
        modules.add(new BurrowPlus());
        modules.add(new CustomFOV());
        modules.add(new ElytraFlyPlus());
        modules.add(new FastXP());
        modules.add(new FeetESP());
        modules.add(new FlightPlus());
        modules.add(new Fog());
        modules.add(new ForceSneak());
        modules.add(new HoleFillPlus());
        modules.add(new HoleFillRewrite());
        modules.add(new HoleSnap());
        modules.add(new JesusPlus());
        modules.add(new KillAuraPlus());
        modules.add(new LightsOut());
        modules.add(new MineESP());
        modules.add(new OffHandPlus());
        modules.add(new PacketFly());
        modules.add(new PistonCrystal());
        modules.add(new PistonPush());
        modules.add(new PortalGodMode());
        modules.add(new RPC());
        modules.add(new ScaffoldPlus());
        modules.add(new SelfTrapPlus());
        modules.add(new SoundModifier());
        modules.add(new SpeedPlus());
        modules.add(new SprintPlus());
        modules.add(new StepPlus());
        modules.add(new StrictNoSlow());
        modules.add(new Suicide());
        modules.add(new SurroundPlus());
        modules.add(new SwingModifier());
        modules.add(new TickShift());
        modules.add(new WeakAlert());
        modules.add(new BurrowMove());
        modules.add(new PacketEat());
        modules.add(new SkinBlinker());
        modules.add(new BurrowPlus2());
        modules.add(new AntiPiston());
        modules.add(new SmartWeb());
        modules.add(new SmartWebPlus());
    }

    private void initializeSettings(final Modules modules) {
        modules.add(new FacingSettings());
        modules.add(new RangeSettings());
        modules.add(new RaytraceSettings());
        modules.add(new RotationSettings());
        modules.add(new ServerSettings());
        modules.add(new SwingSettings());
    }

    private void initializeCommands() {
        Commands.add(new BlackoutGit());
        Commands.add(new Coords());
    }

    private void initializeHud(final Hud hud) {
        hud.register(ArmorHudPlus.INFO);
        hud.register(BlackoutArray.INFO);
        hud.register(GearHud.INFO);
        hud.register(HudWaterMark.INFO);
        hud.register(Keys.INFO);
        hud.register(TargetHud.INFO);
        hud.register(Welcomer.INFO);
        hud.register(OnTope.INFO);
        hud.register(CatGirl.INFO);
    }

    private void initializeAutoPVP(final Modules modules) {
        try {
            Class.forName("baritone.api.BaritoneAPI");
            modules.add(new AutoPvp());
        }
        catch (final ClassNotFoundException ex) {}
    }

    public void onRegisterCategories() {
        Modules.registerCategory(BlackOut.BLACKOUT);
        Modules.registerCategory(BlackOut.SETTINGS);
    }

    public String getPackage() {
        return "kassuk.addon.aurora";
    }

    public static boolean getBoolean() { //Shit hwid verify LMAO
        final String hwid = getValue();
        try {
            final URL url = new URL(new String(Base64.getDecoder().decode("aHR0cHM6Ly9wYXN0ZWJpbi5jb20vcmF3L211UmdkeWhZ"))); //https://pastebin.com/raw/muRgdyhY
            final URLConnection conn = url.openConnection();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(hwid)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getValue() {
        return DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("user.name") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITEW6432"));
    }

    static {
        LOG = LogUtils.getLogger();
        BLACKOUT = new Category("Aurora", Items.END_CRYSTAL.getDefaultStack());
        SETTINGS = new Category("Settings", Items.OBSIDIAN.getDefaultStack());
        HUD_BLACKOUT = new HudGroup("Aurora");
    }
}
