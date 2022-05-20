package mipt.project;

import org.jscience.mathematics.number.FloatingPoint;

public class MathUtils {
    public static FloatingPoint ln(FloatingPoint x) {
        FloatingPoint exp = FloatingPoint.ONE.plus(exp(x));
        FloatingPoint t = convertXToT(exp);
        return lnX(t);
    }

    private static FloatingPoint exp(FloatingPoint x) {
        FloatingPoint val = FloatingPoint.ONE
                .plus(x)
                .plus(x.pow(2).divide(FloatingPoint.valueOf(2)))
                .plus(x.pow(3).divide(FloatingPoint.valueOf(6)))
                .plus(x.pow(4).divide(FloatingPoint.valueOf(24)));
        if (val.compareTo(FloatingPoint.ZERO) != 1) {
            throw new IllegalArgumentException("EXP cannot be less than 0");
        }
        return val;
    }

    private static FloatingPoint convertXToT(FloatingPoint x) {
        FloatingPoint val = x.minus(FloatingPoint.ONE).divide(FloatingPoint.ONE.plus(x));
        if (val.compareTo(FloatingPoint.ZERO) != 1) {
            System.out.println("X = " + x + " val = " + val);
            throw new IllegalArgumentException("T cannot be less than 0");
        }
        return val;
    }

    private static FloatingPoint lnX(FloatingPoint x) {
        return FloatingPoint.valueOf(2)
                .times(
                        x.plus(x.pow(3).divide(FloatingPoint.valueOf(3)))
                                .plus(x.pow(5).divide(FloatingPoint.valueOf(5)))
                );
    }
}
