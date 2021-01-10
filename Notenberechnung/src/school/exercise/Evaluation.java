package school.exercise;

public abstract class Evaluation {

    private double weighting;

    public Evaluation(double weighting) {
        setWeighting(weighting);
    }

    public double getWeighting() {
        return weighting;
    }

    public void setWeighting(double weighting) {
        if (weighting < 0) throw new IllegalArgumentException("The argument weighting cannot be less than 0. Given: " + weighting);
        this.weighting = weighting;
    }

    @Override
    public String toString() {
        return String.format("<%s> {weighting=%s}", this.getClass().toString(), getWeighting());
    }
}
