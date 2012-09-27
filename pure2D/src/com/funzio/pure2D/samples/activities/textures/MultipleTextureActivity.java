package com.funzio.pure2D.samples.activities.textures;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.R;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.samples.Bouncer;
import com.funzio.pure2D.samples.activities.StageActivity;

public class MultipleTextureActivity extends StageActivity {
    private List<Texture> mTextures = new ArrayList<Texture>();
    protected boolean mUseTexture = true;

    @Override
    protected int getLayout() {
        return R.layout.stage_textures;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                // load the textures
                loadTextures();

                // generate a lot of squares
                addObjects(OBJ_INIT_NUM, mUseTexture);
            }
        });
    }

    private void loadTextures() {
        final int[] ids = {
                R.drawable.cc_32, // cc
                R.drawable.mw_32, // mw
                R.drawable.ka_32, // ka
                R.drawable.cc_128, // cc
                R.drawable.mw_128, // mw
                R.drawable.ka_128, // ka
        };
        // final int[] ids = {
        // R.drawable.cc_175, // cc
        // R.drawable.mw_175, // mw
        // R.drawable.ka_175, // ka
        // };

        for (int id : ids) {
            // add texture to list
            mTextures.add(mScene.getTextureManager().createDrawableTexture(id, null));
        }
    }

    private void addObjects(final int num, final boolean useTexture) {
        // generate a lot of squares
        for (int i = 0; i < num; i++) {
            int random = mRandom.nextInt(mTextures.size());
            // create object
            Bouncer sq = new Bouncer();
            if (useTexture) {
                Texture texture = mTextures.get(random);
                sq.setTexture(texture);
                // add some alpha for fun
                if (random >= 3) {
                    sq.setAlpha(0.7f);
                }
            } else {
                sq.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), mRandom.nextFloat() + 0.5f));
                int size = mRandom.nextFloat() > 0.5 ? 32 : 128;
                sq.setSize(size, size);
            }

            // random positions
            sq.setPosition(mRandom.nextInt(mDisplaySize.x), mRandom.nextInt(mDisplaySize.y));

            // add to scene
            mScene.addChild(sq);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObjects(OBJ_STEP_NUM, mUseTexture);
                }
            });
        }

        return true;
    }

    public void onClickTextures(final View view) {
        if (view.getId() == R.id.cb_textures) {
            mUseTexture = ((CheckBox) findViewById(R.id.cb_textures)).isChecked();
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    mScene.removeAllChildren();
                    // generate a lot of squares
                    addObjects(OBJ_INIT_NUM, mUseTexture);
                }
            });
        }
    }
}
