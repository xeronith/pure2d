/**
 * 
 */
package com.funzio.pure2D.shapes;

import android.graphics.RectF;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long
 */
public class Sprite9 extends Rectangular {
    protected static final int NUM_PATCHES = 9;

    private RectF m9Patches;
    private QuadBuffer[] mQuadBuffers;
    private TextureCoordBuffer[] mCoordBuffers;

    private boolean m9PatchEnabled = true;
    private boolean mHasPatches = false;

    public void set9Patches(final float left, final float right, final float top, final float bottom) {
        if (m9Patches == null) {
            m9Patches = new RectF(left, top, right, bottom);
        } else {
            m9Patches.set(left, top, right, bottom);
        }

        mHasPatches = left > 0 || right > 0 || top > 0 || bottom > 0;

        invalidate(InvalidateFlags.TEXTURE_COORDS);
    }

    public RectF get9Patches() {
        return m9Patches;
    }

    public boolean is9PatchEnabled() {
        return m9PatchEnabled;
    }

    public void set9PatchEnabled(final boolean value) {
        m9PatchEnabled = value;

        invalidate(InvalidateFlags.TEXTURE_COORDS);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#setTexture(com.funzio.pure2D.gl.gl10.textures.Texture)
     */
    @Override
    public void setTexture(final Texture texture) {
        super.setTexture(texture);

        // auto set size if it's not set
        if (texture != null && mSize.x <= 1 && mSize.y <= 1) {
            setSize(texture.getSize());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#validateTextureCoordBuffer()
     */
    @Override
    protected void validateTextureCoordBuffer() {
        if (mTexture == null || !m9PatchEnabled || !mHasPatches) {
            super.validateTextureCoordBuffer();
            return;
        }

        // init the arrays
        if (mQuadBuffers == null) {
            mQuadBuffers = new QuadBuffer[NUM_PATCHES];
            mCoordBuffers = new TextureCoordBuffer[NUM_PATCHES];

            for (int i = 0; i < NUM_PATCHES; i++) {
                mQuadBuffers[i] = new QuadBuffer();
                mCoordBuffers[i] = new TextureCoordBuffer();
            }
        }

        // some constants
        final float textureW = mTexture.getSize().x;
        final float textureH = mTexture.getSize().y;
        final float tsx = mTexture.mCoordScaleX;
        final float tsy = mTexture.mCoordScaleY;
        float left = m9Patches.left;
        float right = m9Patches.right;
        float top = m9Patches.top;
        float bottom = m9Patches.bottom;
        float middleW = mSize.x - left - right;
        float middleH = mSize.y - top - bottom;
        // if width is too small
        if (middleW < 0) {
            left = right = 0;
            middleW = mSize.x;
        }
        // if height is too small
        if (middleH < 0) {
            top = bottom = 0;
            middleH = mSize.y;
        }

        // vertices
        final float[] widths = {
                left, middleW, right
        };
        final float[] heights = {
                bottom, middleH, top
        };
        // swap for AXIS_TOP_LEFT
        if (mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
            heights[0] = top;
            heights[2] = bottom;
        }

        // texture coordinates
        final float[] scaleX = {
                left / textureW, (textureW - left - right) / textureW, right / textureW
        };
        final float[] scaleY = {
                bottom / textureH, (textureH - top - bottom) / textureH, top / textureH
        };
        // swap for AXIS_TOP_LEFT
        if (mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
            final float temp = scaleY[0];
            scaleY[0] = scaleY[2];
            scaleY[2] = temp;
        }

        float vx = 0, vy = 0; // vertex start x,y
        float tx = 0, ty = 1, tyInverted = 0; // texture coord start x,y
        int index = 0;
        for (int row = 0; row < 3; row++) {
            float vh = heights[row];
            float th = scaleY[row];
            vx = 0;
            tx = 0;

            for (int col = 0; col < 3; col++) {
                float vw = widths[col];
                float tw = scaleX[col];

                // set the quad values
                mQuadBuffers[index].setRect(vx, vy, vw, vh);

                // set the coordinates
                if (mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
                    mCoordBuffers[index].setRectFlipVertical(tx * tsx, tyInverted * tsy, tw * tsx, th * tsy);
                } else {
                    mCoordBuffers[index].setRectFlipVertical(tx * tsx, ty * tsy, tw * tsx, -th * tsy);
                }

                vx += vw;
                tx += tw;
                index++;
            }

            vy += vh;
            ty -= th;
            tyInverted += th;
        }

        // clear flag: texture coords
        validate(InvalidateFlags.TEXTURE_COORDS);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Rectangular#setSize(float, float)
     */
    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // also invalidate coordinates
        invalidate(InvalidateFlags.TEXTURE_COORDS);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#drawChildren(com.funzio.pure2D.gl.gl10.GLState)
     */
    @Override
    protected boolean drawChildren(final GLState glState) {
        // texture check
        if (mTexture == null || !m9PatchEnabled || !mHasPatches) {
            return super.drawChildren(glState);
        }

        // bind the texture
        mTexture.bind();

        // check and draw the quads
        QuadBuffer quad;
        for (int i = 0; i < NUM_PATCHES; i++) {
            quad = mQuadBuffers[i];
            // only draw when the quad is set
            if (quad.hasSize()) {
                // now draw, woo hoo!
                mCoordBuffers[i].apply(glState);
                quad.draw(glState);
            }
        }

        return true;
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        if (xmlParser.getAttributeValue(null, "patches") != null) {
            final String[] patches = xmlParser.getAttributeValue(null, "patches").split(",");
            final float left = patches.length >= 1 ? Float.valueOf(patches[0].trim()) : 0;
            final float right = patches.length >= 2 ? Float.valueOf(patches[1].trim()) : 0;
            final float top = patches.length >= 3 ? Float.valueOf(patches[2].trim()) : 0;
            final float bottom = patches.length >= 4 ? Float.valueOf(patches[3].trim()) : 0;
            set9Patches(left, right, top, bottom);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();

        m9Patches = null;
        mQuadBuffers = null;
        mCoordBuffers = null;
    }
}
