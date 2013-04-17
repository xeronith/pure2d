/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */
public class NovaVO {
    public int version;
    public int pool_size = 0;

    public List<EmitterVO> emitters;
    public List<AnimatorVO> animators;

    // for fast look up
    private Map<String, EmitterVO> mEmitterMap;
    private Map<String, AnimatorVO> mAnimatorMap;

    private Set<String> mUsedSprites;

    // @JsonCreator
    // public NovaVO( //
    // @JsonProperty("version")//
    // final int version, //
    //
    // @JsonProperty("emitters")//
    // final List<EmitterVO> emitters, //
    //
    // @JsonProperty("animators")//
    // final List<AnimatorVO> animators //
    // ) {
    // this.version = version;
    // this.emitters = emitters;
    // this.animators = animators;
    //
    // // make the map
    // mAnimatorMap = new HashMap<String, AnimatorVO>();
    // for (AnimatorVO vo : animators) {
    // mAnimatorMap.put(vo.name, vo);
    // }
    // }

    public NovaVO(final JSONObject json) throws JSONException {
        version = json.optInt("version");
        pool_size = json.optInt("pool_size");
        emitters = getEmitters(json.optJSONArray("emitters"));
        animators = getAnimators(json.optJSONArray("animators"));

        // make the maps
        if (emitters != null) {
            mEmitterMap = new HashMap<String, EmitterVO>();
            for (EmitterVO vo : emitters) {
                mEmitterMap.put(vo.name, vo);
            }
        }

        if (animators != null) {
            mAnimatorMap = new HashMap<String, AnimatorVO>();
            for (AnimatorVO vo : animators) {
                if (vo != null) {
                    mAnimatorMap.put(vo.name, vo);
                }
            }
        }
    }

    public NovaVO(final String json) throws JSONException {
        this(new JSONObject(json));
    }

    /**
     * Apply a screen's scale factor to some certain numbers such as x, y, dx, dy. This is used when you scale the texture.
     * 
     * @param scale
     * @see TextureOptions
     */
    public void applyScale(final float scale) {
        // scale emitters
        for (EmitterVO vo : emitters) {
            if (vo != null) {
                vo.applyScale(scale);
            }
        }

        // scale animators
        for (AnimatorVO vo : animators) {
            if (vo != null) {
                vo.applyScale(scale);
            }
        }
    }

    public EmitterVO getEmitterVO(final String name) {
        return mEmitterMap != null ? mEmitterMap.get(name) : null;
    }

    public AnimatorVO getAnimatorVO(final String name) {
        return mAnimatorMap != null ? mAnimatorMap.get(name) : null;
    }

    protected static List<EmitterVO> getEmitters(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final List<EmitterVO> result = new ArrayList<EmitterVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(new EmitterVO(array.getJSONObject(i)));
        }

        return result;
    }

    protected static List<AnimatorVO> getAnimators(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final List<AnimatorVO> result = new ArrayList<AnimatorVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(AnimatorVO.create(array.getJSONObject(i)));
        }

        return result;
    }

    // protected static List<Integer> getListInteger(final JSONArray array) throws JSONException {
    // if (array == null) {
    // return null;
    // }
    //
    // final List<Integer> result = new ArrayList<Integer>();
    // final int size = array.length();
    // for (int i = 0; i < size; i++) {
    // result.add(array.getInt(i));
    // }
    //
    // return result;
    // }

    protected static List<Integer> getListInt(final JSONObject json, final String field) throws JSONException {
        // field check
        if (!json.has(field)) {
            return null;
        }

        final List<Integer> result = new ArrayList<Integer>();
        try {
            final JSONArray array = json.getJSONArray(field);
            if (array != null) {
                final int size = array.length();
                for (int i = 0; i < size; i++) {
                    result.add(array.getInt(i));
                }
            }
        } catch (JSONException e) {
            // single value
            result.add(json.getInt(field));
        }

        return result;
    }

    // protected static List<Float> getListFloat(final JSONArray array) throws JSONException {
    // if (array == null) {
    // return null;
    // }
    //
    // final List<Float> result = new ArrayList<Float>();
    // final int size = array.length();
    // for (int i = 0; i < size; i++) {
    // result.add((float) array.getDouble(i));
    // }
    //
    // return result;
    // }

    protected static List<Float> getListFloat(final JSONObject json, final String field) throws JSONException {
        // field check
        if (!json.has(field)) {
            return null;
        }

        final List<Float> result = new ArrayList<Float>();
        try {
            final JSONArray array = json.getJSONArray(field);
            if (array != null) {
                final int size = array.length();
                for (int i = 0; i < size; i++) {
                    result.add((float) array.getDouble(i));
                }
            }
        } catch (JSONException e) {
            // single value
            result.add((float) json.getDouble(field));
        }

        return result;
    }

    // protected static List<String> getListString(final JSONArray array) throws JSONException {
    // if (array == null) {
    // return null;
    // }
    //
    // final List<String> result = new ArrayList<String>();
    // final int size = array.length();
    // for (int i = 0; i < size; i++) {
    // result.add(array.getString(i));
    // }
    //
    // return result;
    // }

    protected static List<String> getListString(final JSONObject json, final String field) throws JSONException {
        // field check
        if (!json.has(field)) {
            return null;
        }

        final List<String> result = new ArrayList<String>();
        try {
            final JSONArray array = json.getJSONArray(field);
            if (array != null) {
                final int size = array.length();
                for (int i = 0; i < size; i++) {
                    result.add(array.getString(i));
                }
            }
        } catch (JSONException e) {
            // single value
            result.add(json.optString(field));
        }

        return result;
    }

    /**
     * @return the set of Sprites being used
     */
    public Set<String> getUsedSprites() {
        if (mUsedSprites == null) {
            mUsedSprites = new HashSet<String>();

            // collect from the emitters
            for (EmitterVO emitterVO : emitters) {
                mUsedSprites.addAll(emitterVO.getUsedSprites());
            }
        }

        return mUsedSprites;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Version: " + version + "\n" //
                + "Emitters: " + (emitters == null ? 0 : emitters.size()) + "\n" //
                + "Animators: " + (animators == null ? 0 : animators.size());
    }

}
