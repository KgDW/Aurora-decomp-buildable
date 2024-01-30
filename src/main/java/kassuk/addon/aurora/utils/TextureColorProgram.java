package kassuk.addon.aurora.utils;

import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.render.VertexFormats;

public class TextureColorProgram extends GlProgram
{
    public TextureColorProgram() {
        super("position_tex_color2", VertexFormats.POSITION);
    }

    public void setParameters(final float x, final float y, final float width, final float height, final float radius, final Color color) {
        final int i = this.mc.options.getGuiScale().getValue();
    }

    @Override
    public void use() {
        super.use();
    }

    @Override
    protected void setup() {
    }
}
