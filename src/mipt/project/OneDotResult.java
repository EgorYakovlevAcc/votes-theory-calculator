package mipt.project;

import org.jscience.mathematics.number.FloatingPoint;

public class OneDotResult {
    private FloatingPoint tValue;
    private FloatingPoint resultForFirst;
    private FloatingPoint resultForSecond;
    private FloatingPoint commonResult;

    public FloatingPoint getCommonResult() {
        return commonResult;
    }

    public void setCommonResult(FloatingPoint commonResult) {
        this.commonResult = commonResult;
    }

    public FloatingPoint gettValue() {
        return tValue;
    }

    public void settValue(FloatingPoint tValue) {
        this.tValue = tValue;
    }

    public FloatingPoint getResultForFirst() {
        return resultForFirst;
    }

    public void setResultForFirst(FloatingPoint resultForFirst) {
        this.resultForFirst = resultForFirst;
    }

    public FloatingPoint getResultForSecond() {
        return resultForSecond;
    }

    public void setResultForSecond(FloatingPoint resultForSecond) {
        this.resultForSecond = resultForSecond;
    }
}
