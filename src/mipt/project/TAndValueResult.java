package mipt.project;

import org.jscience.mathematics.number.FloatingPoint;

import java.util.Map;

public class TAndValueResult {
    private FloatingPoint tValue;
    private FloatingPoint spk;

    public TAndValueResult(Map.Entry<FloatingPoint, FloatingPoint> tValueAndSpk) {
        this.tValue = tValueAndSpk.getKey();
        this.spk = tValueAndSpk.getValue();
    }

    public FloatingPoint gettValue() {
        return tValue;
    }

    public void settValue(FloatingPoint tValue) {
        this.tValue = tValue;
    }

    public FloatingPoint getSpk() {
        return spk;
    }

    public void setSpk(FloatingPoint spk) {
        this.spk = spk;
    }
}
