package org.insta;

import android.graphics.*;
import android.opengl.*;
import jp.co.cyberagent.android.gpuimage.*;
import jp.co.cyberagent.android.gpuimage.util.*;
import java.nio.*;

public abstract class InstaFilter extends GPUImageFilter
{
    protected static final String VERTEX_SHADER = "attribute vec4 position;\n attribute vec4 inputTextureCoordinate;\n \n varying vec2 textureCoordinate;\n \n void main()\n {\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n }\n";
    protected int[] GL_TEXTURES;
    protected Bitmap[] bitmaps;
    protected int[] coordinateAttributes;
    protected ByteBuffer[] coordinatesBuffers;
    protected int[] inputTextureUniforms;
    protected int[] sourceTextures;
    protected int textureNum;

    public InstaFilter(final String s, final int n) {
        this("attribute vec4 position;\n attribute vec4 inputTextureCoordinate;\n \n varying vec2 textureCoordinate;\n \n void main()\n {\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n }\n", s, n);
    }

    public InstaFilter(final String s, final String s2, int i) {
        super(s, s2);
        this.GL_TEXTURES = new int[] { 33987, 33988, 33989, 33990, 33991, 33992 };
        this.textureNum = i;
        this.coordinateAttributes = new int[this.textureNum];
        this.inputTextureUniforms = new int[this.textureNum];
        this.sourceTextures = new int[this.textureNum];
        for (i = 0; i < this.textureNum; ++i) {
            this.sourceTextures[i] = -1;
        }
        this.coordinatesBuffers = new ByteBuffer[this.textureNum];
        this.bitmaps = new Bitmap[this.textureNum];
        this.setRotation(Rotation.NORMAL, false, false);
    }

    private void loadBitmap(final int n, final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        if (bitmap == null) {
            return;
        }
        this.runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (bitmap == null) {
                    return;
                }
                if (bitmap.isRecycled()) {
                    return;
                }
                if (InstaFilter.this.sourceTextures[n] == -1) {
                    GLES20.glActiveTexture(InstaFilter.this.GL_TEXTURES[n]);
                    InstaFilter.this.sourceTextures[n] = OpenGlUtils.loadTexture(bitmap, -1, false);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.textureNum <= 0) {
            return;
        }
        try {
            final int[] sourceTextures = this.sourceTextures;
            int i = 0;
            GLES20.glDeleteTextures(1, sourceTextures, 0);
            while (i < this.textureNum) {
                this.sourceTextures[i] = -1;
                if (this.bitmaps[i] != null && !this.bitmaps[i].isRecycled()) {
                    this.bitmaps[i].recycle();
                    this.bitmaps[i] = null;
                }
                ++i;
            }
        }
        catch (Exception ex) {}
    }

    @Override
    protected void onDrawArraysPre() {
        for (int i = 0; i < this.textureNum; ++i) {
            GLES20.glEnableVertexAttribArray(this.coordinateAttributes[i]);
            GLES20.glActiveTexture(this.GL_TEXTURES[i]);
            GLES20.glBindTexture(3553, this.sourceTextures[i]);
            GLES20.glUniform1i(this.inputTextureUniforms[i], i + 3);
            this.coordinatesBuffers[i].position(0);
            GLES20.glVertexAttribPointer(this.coordinateAttributes[i], 2, 5126, false, 0, (Buffer)this.coordinatesBuffers[i]);
        }
    }

    @Override
    public void onInit() {
        super.onInit();
        for (int i = 0; i < this.textureNum; ++i) {
            final int n = i + 2;
            this.coordinateAttributes[i] = GLES20.glGetAttribLocation(this.getProgram(), String.format("inputTextureCoordinate%d", n));
            this.inputTextureUniforms[i] = GLES20.glGetUniformLocation(this.getProgram(), String.format("inputImageTexture%d", n));
            GLES20.glEnableVertexAttribArray(this.coordinateAttributes[i]);
            if (this.bitmaps[i] != null && !this.bitmaps[i].isRecycled()) {
                this.loadBitmap(i, this.bitmaps[i]);
            }
        }
    }

    public void setBitmap(final int n, final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        if (bitmap == null) {
            return;
        }
        this.bitmaps[n] = bitmap;
    }

    public void setRotation(final Rotation rotation, final boolean b, final boolean b2) {
        final float[] rotation2 = TextureRotationUtil.getRotation(rotation, b, b2);
        final ByteBuffer order = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        final FloatBuffer floatBuffer = order.asFloatBuffer();
        floatBuffer.put(rotation2);
        floatBuffer.flip();
        for (int i = 0; i < this.textureNum; ++i) {
            this.coordinatesBuffers[i] = order;
        }
    }
}
