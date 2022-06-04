package mipt.project;

import org.jscience.mathematics.number.FloatingPoint;

public class MuModelResult {
    private FloatingPoint muValue;
    private FloatingPoint tValue;
    private FloatingPoint maxSpk;
    private FloatingPoint aCoef;
    private ModelResult modelResult;

    public MuModelResult(FloatingPoint muValue) {
        this.muValue = muValue;
    }

    public FloatingPoint getaCoef() {
        return aCoef;
    }

    public void setaCoef(FloatingPoint aCoef) {
        this.aCoef = aCoef;
    }

    public FloatingPoint getMuValue() {
        return muValue;
    }

    public void setMuValue(FloatingPoint muValue) {
        this.muValue = muValue;
    }

    public FloatingPoint gettValue() {
        return tValue;
    }

    public void settValue(FloatingPoint tValue) {
        this.tValue = tValue;
    }

    public FloatingPoint getMaxSpk() {
        return maxSpk;
    }

    public void setMaxSpk(FloatingPoint maxSpk) {
        this.maxSpk = maxSpk;
    }

    public ModelResult getModelResult() {
        return modelResult;
    }

    public void setModelResult(ModelResult modelResult) {
        this.modelResult = modelResult;
    }
}
