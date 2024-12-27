package backend.academy;

import lombok.experimental.UtilityClass;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

@UtilityClass
public class Transforms {
    public static double applyTransform(
        final double x,
        final double y,
        final boolean isX,
        final double coeff,
        final String transform
    ) {
        return switch (transform) {
            case "--linear" -> isX ? x : y;
            case "--sinusoidal" -> Transforms.sinusoidal(isX ? x : y);
            case "--spherical" -> Transforms.spherical(x, y, isX);
            case "--swirl" -> Transforms.swirl(x, y, isX);
            case "--horseshoe" -> Transforms.horseshoe(x, y, isX);
            case "--popcorn" -> Transforms.popcorn(x, y, isX, coeff);
            default -> throw new IllegalArgumentException("Unknown transform");
        };
    }

    public static double sinusoidal(final double cord) {
        return sin(cord);
    }

    public static double spherical(final double x, final double y, final boolean isX) {
        return (isX ? x : y) / pow(radius(x, y), 2);
    }

    public static double swirl(final double x, final double y, final boolean isX) {
        final double r2 = pow(radius(x, y), 2);
        return x * (isX ? sin(r2) : cos(r2)) - y * (isX ? cos(r2) : sin(r2));
    }

    public static double horseshoe(final double x, final double y, final boolean isX) {
        final double r = radius(x, y);
        return (isX ? (x - y) * (x + y) : 2 * x * y) / r;
    }

    @SuppressWarnings("MagicNumber")
    public static double popcorn(
        final double x,
        final double y,
        final boolean isX,
        final double coeff
    ) {
        return (isX ? x : y) + coeff * sin(tan(3 * (isX ? y : x)));
    }

    private static double radius(final double x, final double y) {
        return Math.sqrt(x * x + y * y);
    }
}
