package com.funzio.pure2D.lwf;

import android.util.Log;
import android.graphics.RectF;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.Playable;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.GLState;

public class LWFObject extends BaseDisplayObject implements Playable {
    public static boolean LOG_ENABLED = true;
    private static final String TAG = LWFObject.class.getSimpleName();

    public static final int SCALING_NONE = 0;
    public static final int SCALING_FIT_FOR_HEIGHT = 1;
    public static final int SCALING_FIT_FOR_WIDTH = 2;
    public static final int SCALING_SCALE_FOR_HEIGHT = 3;
    public static final int SCALING_SCALE_FOR_WIDTH = 4;
    public static final int SCALING_USE_DEFAULT = 5;

    private LWF mLWF;
    private LWF mLastAttachedLWF;
    private int mAttachId;
    private boolean mPlaying;
    private float mWidth;
    private float mHeight;
    private int mDefaultScaling = SCALING_NONE;

    public LWFObject() {
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mLWF == null)
            return false;
        mLWF.update(deltaTime);
        invalidate(InvalidateFlags.VISUAL);
        return super.update(deltaTime);
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        if (mLWF == null)
            return false;

        BlendFunc blendFunc = glState.getBlendFunc();

        mLWF.draw();

        glState.unbindTexture();
        glState.setBlendFunc(blendFunc);
        glState.setVertexArrayEnabled(false);
        glState.setTextureCoordArrayEnabled(false);
        glState.setColorArrayEnabled(false);
        glState.setTextureCoordBuffer(null);
        return true;
    }

    public void setStageSize(float width, float height, int defaultScaling) {
        mWidth = width;
        mHeight = height;
        mDefaultScaling = defaultScaling;
    }

    public void play() {
        if (mLWF == null)
            return;
        mLWF.setPlaying(true);
    }

    public void playAt(final int frame) {
        if (mLWF == null)
            return;
        mLWF.setPlaying(true);
    }

    public void stop() {
        if (mLWF == null)
            return;
        mLWF.setPlaying(false);
    }

    public void stopAt(final int frame) {
        if (mLWF == null)
            return;
        mLWF.setPlaying(false);
    }

    public int getLoop() {
        return LOOP_NONE;
    }

    public void setLoop(final int type) {
    }

    public int getCurrentFrame() {
        return 1;
    }

    public int getNumFrames() {
        return 1;
    }

    public boolean isPlaying() {
        if (mLWF == null)
            return false;
        return mLWF.isPlaying();
    }

    public RectF getFrameRect(final int frame) {
        RectF r = new RectF();
        return r;
    }

    public LWF attachLWF(LWFData data) {
        return attachLWF(data, SCALING_USE_DEFAULT);
    }

    public LWF attachLWF(LWFData data, int scaling) {
        String attachName = String.format("childLWF%d", mAttachId++);
        return attachLWF(data, "_root", attachName, scaling);
    }

    public LWF attachLWF(LWFData data, String target, String attachName) {
        return attachLWF(data, target, attachName, SCALING_USE_DEFAULT);
    }

    public LWF attachLWF(LWFData data, String target, String attachName, int scaling) {
        if (mLWF == null)  {
            mLWF = data.getLWFManager().createLWF();
            mPlaying = true;
        }

        LWF lwf = data.getLWFManager().createLWF(data);

        if (scaling == SCALING_USE_DEFAULT)
            scaling = mDefaultScaling;

        switch (scaling) {
        case SCALING_FIT_FOR_HEIGHT:
            mLWF.fitForHeight(mWidth, mHeight);
            break;

        case SCALING_FIT_FOR_WIDTH:
            mLWF.fitForWidth(mWidth, mHeight);
            break;

        case SCALING_SCALE_FOR_HEIGHT:
            mLWF.scaleForHeight(mWidth, mHeight);
            break;

        case SCALING_SCALE_FOR_WIDTH:
            mLWF.scaleForWidth(mWidth, mHeight);
            break;
        }

        mLWF.attachLWF(lwf, target, attachName);
        mLastAttachedLWF = lwf;

        return lwf;
    }

    public LWF getLWF() {
        return mLastAttachedLWF;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (mLWF != null) {
            if (LOG_ENABLED) {
                Log.e(TAG, "dispose()");
            }
            mLWF.dispose();
            mLWF = null;
            mLastAttachedLWF = null;
        }
    }
}
