package school;

public class TextProductionEvaluation extends Evaluation {

    private double pointsLanguage;
    private double pointsContent;

    public TextProductionEvaluation(double weighting, double pointsContent, double pointsLanguage) {
        super(weighting);
        setPointsContent(pointsContent);
        setPointsLanguage(pointsLanguage);
    }

    public double getPointsContent() {
        return pointsContent;
    }

    public void setPointsContent(double pointsContent) {
        if (pointsContent < 0) throw new IllegalArgumentException("The argument pointsContent cannot be less than 0. Given: " + pointsContent);
        this.pointsContent = pointsContent;
    }

    public double getPointsLanguage() {
        return pointsLanguage;
    }

    public void setPointsLanguage(double pointsLanguage) {
        if (pointsLanguage < 0) throw new IllegalArgumentException("The argument pointsLanguage cannot be less than 0. Given: " + pointsLanguage);
        this.pointsLanguage = pointsLanguage;
    }

    @Override
    public String toString() {
        return String.format("<%s> {weighting=%s, content=%s, language=%s}", this.getClass().toString(), getWeighting(), getPointsContent(), getPointsLanguage());
    }
}
