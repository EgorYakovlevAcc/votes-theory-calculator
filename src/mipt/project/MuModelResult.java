package mipt.project;

import org.jscience.mathematics.number.FloatingPoint;

public class MuModelResult {
    private FloatingPoint muValue;
    private FloatingPoint tValue;
    private FloatingPoint maxSpk;
    private FloatingPoint aCoef;

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
}
