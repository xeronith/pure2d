/**
 * 
 */
package com.funzio.pure2D.containers;

import android.graphics.PointF;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.ui.UIConfig;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long
 */
public abstract class LinearGroup extends DisplayGroup {

    protected static final String ATT_ALIGN = "align";
    protected static final String ATT_REPEATING = "repeating";
    protected static final String ATT_BOUNDS_CHECK_ENABLED = "boundsCheckEnabled";
    protected static final String ATT_AUTO_SLEEP_CHILDREN = "autoSleepChildren";

    protected float mGap = 0;
    protected float mOffsetX = 0;
    protected float mOffsetY = 0;

    protected int mAlignment = Alignment.NONE;
    protected PointF mScrollPosition = new PointF();
    protected boolean mRepeating = false;
    protected boolean mBoundsCheckEnabled = true;
    protected boolean mAutoSleepChildren = false;

    private boolean mChildrenPositionInvalidated = false;

    @Override
    public void updateChildren(final int deltaTime) {
        super.updateChildren(deltaTime);

        // adjust the positions when necessary
        if (mChildrenPositionInvalidated) {
            positionChildren();
            mChildrenPositionInvalidated = false;

            // only apply constraints when size or parent changed
            if (mUIConstraint != null && (mInvalidateFlags & (SIZE | PARENT)) != 0) {
                mUIConstraint.apply(this, mParent);
            }
        }

    }

    protected void invalidateChildrenPosition() {
        mChildrenPositionInvalidated = true;
        invalidate(InvalidateFlags.CHILDREN);
    }

    /**
     * @return the gap
     */
    public float getGap() {
        return mGap;
    }

    /**
     * @param gap the gap to set
     */
    public void setGap(final float gap) {
        mGap = gap;

        // reposition the children
        invalidateChildrenPosition();
    }

    public void setOffset(final float offsetX, final float offsetY) {
        mOffsetX = offsetX;
        mOffsetY = offsetY;

        // reposition the children
        invalidateChildrenPosition();
    }

    public PointF getScrollPosition() {
        return mScrollPosition;
    }

    public void scrollTo(final float x, final float y) {
        mScrollPosition.x = x;
        mScrollPosition.y = y;

        // reposition the children
        invalidateChildrenPosition();
    }

    public void scrollTo(final DisplayObject child) {
        PointF pos = child.getPosition();
        mScrollPosition.x = pos.x;
        mScrollPosition.y = pos.y;

        // reposition the children
        invalidateChildrenPosition();
    }

    public void scrollBy(final float dx, final float dy) {
        mScrollPosition.x += dx;
        mScrollPosition.y += dy;

        // reposition the children
        invalidateChildrenPosition();
    }

    /**
     * @return the Repeating
     */
    public boolean isRepeating() {
        return mRepeating;
    }

    /**
     * @param Repeating the Repeating to set
     */
    public void setRepeating(final boolean Repeating) {
        mRepeating = Repeating;
        invalidateChildrenPosition();
    }

    @Override
    protected void onAddedChild(final DisplayObject child) {
        super.onAddedChild(child);

        invalidateChildrenPosition();
    }

    @Override
    protected void onRemovedChild(final DisplayObject child) {
        super.onRemovedChild(child);

        invalidateChildrenPosition();
    }

    /**
     * @return the checking bounds
     */
    public boolean isBoundsCheckEnabled() {
        return mBoundsCheckEnabled;
    }

    /**
     * Toggle checking bounds and draw children
     * 
     * @param checking the bounds to set
     */
    public void setBoundsCheckEnabled(final boolean checking) {
        mBoundsCheckEnabled = checking;

        invalidate(InvalidateFlags.CHILDREN);
    }

    public boolean isAutoSleepChildren() {
        return mAutoSleepChildren;
    }

    public void setAutoSleepChildren(final boolean autoSleepChildren) {
        mAutoSleepChildren = autoSleepChildren;
    }

    public int getAlignment() {
        return mAlignment;
    }

    public void setAlignment(final int alignment) {
        mAlignment = alignment;

        invalidateChildrenPosition();
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String align = xmlParser.getAttributeValue(null, ATT_ALIGN);
        if (align != null) {
            setAlignment(UIConfig.getAlignment(align));
        }

        final String repeating = xmlParser.getAttributeValue(null, ATT_REPEATING);
        if (repeating != null) {
            setRepeating(Boolean.valueOf(repeating));
        }

        final String boundsCheck = xmlParser.getAttributeValue(null, ATT_BOUNDS_CHECK_ENABLED);
        if (boundsCheck != null) {
            setBoundsCheckEnabled(Boolean.valueOf(boundsCheck));
        }

        final String autoSleepChildren = xmlParser.getAttributeValue(null, ATT_AUTO_SLEEP_CHILDREN);
        if (autoSleepChildren != null) {
            setAutoSleepChildren(Boolean.valueOf(autoSleepChildren));
        }
    }

    protected abstract void positionChildren();

    public abstract PointF getContentSize();

    public abstract PointF getScrollMax();

}
