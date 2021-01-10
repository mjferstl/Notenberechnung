package school.exercise;

import school.exercise.Evaluation;

public class NormalExerciseEvaluation extends Evaluation {

    private double BE;

    public NormalExerciseEvaluation(double weighting, double be) {
        super(weighting);
        setBE(be);
    }

    public double getBE() {
        return BE;
    }

    public void setBE(double BE) {
        if (BE < 0) throw new IllegalArgumentException("The argument BE cannot be less than 0. Given: " + BE);
        this.BE = BE;
    }

    @Override
    public String toString() {
        return String.format("<%s> {weighting=%s, BE=%s}", this.getClass().toString(), getWeighting(), getBE());
    }
}
