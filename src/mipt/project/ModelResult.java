package mipt.project;

import org.jscience.mathematics.number.FloatingPoint;

import java.util.Map;

public class ModelResult {
    private Map<FloatingPoint, FloatingPoint> firstGroupResult;
    private Map<FloatingPoint, FloatingPoint> secondGroupResult;
    private Map<FloatingPoint, FloatingPoint> allGroupsResult;
    private FloatingPoint aCoef;
    private Boolean isModifiedModel;
    private FloatingPoint mean;

    public Map<FloatingPoint, FloatingPoint> getAllGroupsResult() {
        return allGroupsResult;
    }

    public void setAllGroupsResult(Map<FloatingPoint, FloatingPoint> allGroupsResult) {
        this.allGroupsResult = allGroupsResult;
    }

    public Map<FloatingPoint, FloatingPoint> getFirstGroupResult() {
        return firstGroupResult;
    }

    public void setFirstGroupResult(Map<FloatingPoint, FloatingPoint> firstGroupResult) {
        this.firstGroupResult = firstGroupResult;
    }

    public Map<FloatingPoint, FloatingPoint> getSecondGroupResult() {
        return secondGroupResult;
    }

    public void setSecondGroupResult(Map<FloatingPoint, FloatingPoint> secondGroupResult) {
        this.secondGroupResult = secondGroupResult;
    }

    public FloatingPoint getaCoef() {
        return aCoef;
    }

    public void setaCoef(FloatingPoint aCoef) {
        this.aCoef = aCoef;
    }

    public Boolean getModifiedModel() {
        return isModifiedModel;
    }

    public void setModifiedModel(Boolean modifiedModel) {
        isModifiedModel = modifiedModel;
    }

    public FloatingPoint getMean() {
        return mean;
    }

    public void setMean(FloatingPoint mean) {
        this.mean = mean;
    }
}
