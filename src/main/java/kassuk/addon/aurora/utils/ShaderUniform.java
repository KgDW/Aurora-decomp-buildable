package kassuk.addon.aurora.utils;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector3f;
import net.minecraft.client.gl.Uniform;
import java.util.List;

public class ShaderUniform
{
    private final List<Uniform> uniforms;
    
    public ShaderUniform(final List<Uniform> uniforms) {
        this.uniforms = uniforms;
    }
    
    public void set(final float value1) {
        this.uniforms.forEach(u -> u.set(value1));
    }
    
    public void set(final float value1, final float value2) {
        this.uniforms.forEach(u -> u.set(value1, value2));
    }
    
    public void set(final float value1, final float value2, final float value3) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3));
    }
    
    public void setAndFlip(final float value1, final float value2, final float value3, final float value4) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4));
    }
    
    public void setForDataType(final float value1, final float value2, final float value3, final float value4) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4));
    }
    
    public void setForDataType(final int value1, final int value2, final int value3, final int value4) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4));
    }
    
    public void set(final int value) {
        this.uniforms.forEach(u -> u.set(value));
    }
    
    public void set(final int value1, final int value2) {
        this.uniforms.forEach(u -> u.set(value1, value2));
    }
    
    public void set(final int value1, final int value2, final int value3) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3));
    }
    
    public void set(final int value1, final int value2, final int value3, final int value4) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4));
    }
    
    public void set(final float[] values) {
        this.uniforms.forEach(u -> u.set(values));
    }
    
    public void set(final Vector3f vector) {
        this.uniforms.forEach(u -> u.set(vector));
    }
    
    public void set(final Vector4f vec) {
        this.uniforms.forEach(u -> u.set(vec));
    }
    
    public void set(final float value1, final float value2, final float value3, final float value4) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4));
    }
    
    public void set(final float value1, final float value2, final float value3, final float value4, final float value5, final float value6) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4, value5, value6));
    }
    
    public void set(final float value1, final float value2, final float value3, final float value4, final float value5, final float value6, final float value7, final float value8) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4, value5, value6, value7, value8));
    }
    
    public void set(final float value1, final float value2, final float value3, final float value4, final float value5, final float value6, final float value7, final float value8, final float value9) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4, value5, value6, value7, value8, value9));
    }
    
    public void set(final float value1, final float value2, final float value3, final float value4, final float value5, final float value6, final float value7, final float value8, final float value9, final float value10, final float value11, final float value12) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12));
    }
    
    public void set(final float value1, final float value2, final float value3, final float value4, final float value5, final float value6, final float value7, final float value8, final float value9, final float value10, final float value11, final float value12, final float value13, final float value14, final float value15, final float value16) {
        this.uniforms.forEach(u -> u.set(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16));
    }
    
    public void set(final Matrix4f matrix4f) {
        this.uniforms.forEach(u -> u.set(matrix4f));
    }
    
    public void set(final Matrix3f matrix3f) {
        this.uniforms.forEach(u -> u.set(matrix3f));
    }
}
