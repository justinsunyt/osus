package org.cis1200.osus;

public abstract class Note extends GameObj implements Comparable {

    /**
     * Constructor
     *
     * @param px
     * @param py
     * @param width
     * @param height
     * @param beat
     */

    final private int quarterNote;
    private boolean hit = false;
    private boolean miss = false;
    private int hitScore = 0;
    private int ifHitScore = 0; // 0 = unhittable, 50 = bad, 100 = good, 300 = perfect

    public Note(int posX, int posY, int width, int height, int quarterNote) {
        super(posX, posY,
                width, height);

        this.quarterNote = quarterNote;
    }

    public abstract void animateIn(long timeSinceLastTick);

    public abstract void animateHit();

    public abstract void animate100(long timeSinceLastTick);

    public abstract void animate50(long timeSinceLastTick);

    public abstract void animateMiss(long timeSinceLastTick);

    public abstract void animateOut(long timeSinceLastTick);

    public abstract long getAnimateDuration();

    public void hit() {
        if (!this.hit) {
            this.ifHitScore = 0;
            this.hit = true;
            Sound.playSound("files/sounds/hit.wav");
        }
    }

    public void miss() {
        if (!this.miss) {
            this.ifHitScore = 0;
            this.miss = true;
            Sound.playSound("files/sounds/miss.wav");
        }
    }

    public int getQuarterNote() {
        return this.quarterNote;
    }

    public boolean getHit() {
        return this.hit;
    }

    public boolean getMiss() {
        return this.miss;
    }

    public int getHitScore() {
        return this.hitScore;
    }

    public void setHitScore(int hitScore) {
        this.hitScore = hitScore;
    }

    public int getIfHitScore() {
        return this.ifHitScore;
    }

    public void setIfHitScore(int ifHitScore) {
        this.ifHitScore = ifHitScore;
    }

    @Override
    public int compareTo(Object o) {
        Note other = (Note) o;
        return this.getQuarterNote() - other.getQuarterNote();
    }
}
