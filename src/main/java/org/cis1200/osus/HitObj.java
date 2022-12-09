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

    public abstract void animateIn(long getTimeSinceLastTick);

    public abstract void hit();

    public abstract void miss(long getTimeSinceLastTick);

    public abstract void animate100(long getTimeSinceLastTick);

    public abstract void animate50(long getTimeSinceLastTick);

    public abstract void animateOut(long getTimeSinceLastTIck);

    public abstract long getAnimateDuration(long getTimeSinceLastTick);

    public abstract int getQuarterNote();

    public abstract boolean getHit();

    public abstract boolean getMissed();

    public abstract int getHitScore();

    public abstract void setHitScore(int hitScore);

    public abstract int getIfHitScore();

    public abstract void setIfHitScore(int ifHitScore);

    public HitObj(int px, int py, int width, int height, int beat) {
        super(px, py, width, height);
    }
}
