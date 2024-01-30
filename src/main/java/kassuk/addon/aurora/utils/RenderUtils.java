package kassuk.addon.aurora.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import kassuk.addon.aurora.utils.GradientGlowProgram;
import kassuk.addon.aurora.utils.TextureColorProgram;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class RenderUtils {
    public static TextureColorProgram TEXTURE_COLOR_PROGRAM;
    public static GradientGlowProgram GRADIENT_GLOW_PROGRAM;
    private static final VertexConsumerProvider.Immediate vertex;

    public static void initShaders() {
        if (GRADIENT_GLOW_PROGRAM == null) {
            GRADIENT_GLOW_PROGRAM = new GradientGlowProgram();
        }
        if (TEXTURE_COLOR_PROGRAM == null) {
            TEXTURE_COLOR_PROGRAM = new TextureColorProgram();
        }
    }

    public static Color injectAlpha(Color color, int alpha) {
        return new Color(color.r, color.g, color.b, MathHelper.clamp(alpha, 0, 255));
    }

    public static void rounded(MatrixStack stack, float x, float y, float w, float h, float radius, int p, int color) {
        Matrix4f matrix4f = stack.peek().getPositionMatrix();
        float a = (float)ColorHelper.Argb.getAlpha(color) / 255.0f;
        float r = (float)ColorHelper.Argb.getRed(color) / 255.0f;
        float g = (float)ColorHelper.Argb.getGreen(color) / 255.0f;
        float b = (float)ColorHelper.Argb.getBlue(color) / 255.0f;
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        RenderUtils.corner(x + w, y, radius, 360, p, r, g, b, a, bufferBuilder, matrix4f);
        RenderUtils.corner(x, y, radius, 270, p, r, g, b, a, bufferBuilder, matrix4f);
        RenderUtils.corner(x, y + h, radius, 180, p, r, g, b, a, bufferBuilder, matrix4f);
        RenderUtils.corner(x + w, y + h, radius, 90, p, r, g, b, a, bufferBuilder, matrix4f);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void corner(float x, float y, float radius, int angle, float p, float r, float g, float b, float a, BufferBuilder bufferBuilder, Matrix4f matrix4f) {
        for (float i = (float)angle; i > (float)(angle - 90); i -= 90.0f / p) {
            bufferBuilder.vertex(matrix4f, (float)((double)x + Math.cos(Math.toRadians(i)) * (double)radius), (float)((double)y + Math.sin(Math.toRadians(i)) * (double)radius), 0.0f).color(r, g, b, a).next();
        }
    }

    public static void text(String text, MatrixStack stack, float x, float y, int color) {
        MeteorClient.mc.textRenderer.draw(text, x, y, color, false, stack.peek().getPositionMatrix(), vertex, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        vertex.draw();
    }

    public static void quad(MatrixStack stack, float x, float y, float w, float h, int color) {
        Matrix4f matrix4f = stack.peek().getPositionMatrix();
        float a = (float)ColorHelper.Argb.getAlpha(color) / 255.0f;
        float r = (float)ColorHelper.Argb.getRed(color) / 255.0f;
        float g = (float)ColorHelper.Argb.getGreen(color) / 255.0f;
        float b = (float)ColorHelper.Argb.getBlue(color) / 255.0f;
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, x + w, y, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix4f, x, y, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix4f, x, y + h, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix4f, x + w, y + h, 0.0f).color(r, g, b, a).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static Vec3d interpolatePos(float prevposX, float prevposY, float prevposZ, float posX, float posY, float posZ) {
        double x = (double)(prevposX + (posX - prevposX) * MeteorClient.mc.getTickDelta()) - MeteorClient.mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = (double)(prevposY + (posY - prevposY) * MeteorClient.mc.getTickDelta()) - MeteorClient.mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = (double)(prevposZ + (posZ - prevposZ) * MeteorClient.mc.getTickDelta()) - MeteorClient.mc.getEntityRenderDispatcher().camera.getPos().getZ();
        return new Vec3d(x, y, z);
    }

    static {
        vertex = VertexConsumerProvider.immediate(new BufferBuilder(2048));
    }
}

