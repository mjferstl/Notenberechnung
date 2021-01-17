package school.exercise;

import org.eclipse.jdt.annotation.NonNull;

public abstract class Exercise {

    private String name;
    private Evaluation evaluation;
    
    public final static String WEIGHTING = "Gewichtung";

    public Exercise(String name) {
        setName(name);
    }

    public Exercise(String name, Evaluation evaluation) {
        this(name);
        setEvaluation(evaluation);
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract ExerciseType getExerciseType();

    public void setEvaluation(@NonNull Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public Evaluation getEvaluation() {
        return this.evaluation;
    }

    public abstract String getConfigString();
    public abstract String getKey();

    @Override
    public String toString() {
        return String.format("<%s> {name: %s, evaluation: [%s]}", this.getClass().toString(), getName(), getEvaluation());
    }
}
