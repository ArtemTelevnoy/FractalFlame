package backend.academy;

import java.awt.Color;
import static backend.academy.SaveFile.RANDOM;
import static java.lang.Math.pow;

public record AffineTransform(Color color, double a, double b, double c, double d, double e, double f) {
    public static boolean checkAffine(
        final double a,
        final double b,
        final double d,
        final double e
    ) {
        final double a2AddD2 = pow(a, 2) + pow(d, 2);
        final double b2AddE2 = pow(b, 2) + pow(e, 2);
        return a2AddD2 < 1 && b2AddE2 < 1 && a2AddD2 + b2AddE2 < 1 + pow(a * e - b * d, 2);
    }

    @SuppressWarnings("MagicNumber")
    public static AffineTransform[] generateAffine(final int count) {
        final AffineTransform[] affine = new AffineTransform[count];

        double a;
        double b;
        double d;
        double e;
        for (int i = 0; i < count; i++) {
            do {
                a = RANDOM.nextDouble(2) - 1;
                b = RANDOM.nextDouble(2) - 1;
                d = RANDOM.nextDouble(2) - 1;
                e = RANDOM.nextDouble(2) - 1;
            } while (!AffineTransform.checkAffine(a, b, d, e));
            final double c = RANDOM.nextDouble(2) - 1;
            final double f = RANDOM.nextDouble(2) - 1;
            affine[i] =
                new AffineTransform(new Color(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256)),
                    a, b, c, d, e, f);
        }

        return affine;
    }
}
