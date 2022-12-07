package org.cis1200.osus;

public abstract class HitObj extends GameObj implements Comparable {

    /**
     * Constructor
     *
     * @param px
     * @param py
     * @param width
     * @param height
     * @param beat
     */

    public abstract void animateIn();

    public abstract void hit();

    public abstract void miss();

    public abstract void animate100();

    public abstract void animate50();

    public abstract void animateMissOut();

    public abstract int getAnimateDuration();

    public abstract int getQuarterNote();

    public abstract boolean getHit();

    public abstract boolean getHittable();

    public abstract void setHittable(boolean hittable);

    public HitObj(int px, int py, int width, int height, int beat) {
        super(px, py, width, height);
    }
}
