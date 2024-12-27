import backend.academy.AffineTransform;
import backend.academy.FractalFlame;
import backend.academy.Main;
import backend.academy.Params;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.security.SecureRandom;
import java.util.List;
import static backend.academy.Transforms.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.lang.Math.*;

public class FractalTests {

    @Test
    public void validAffine() {
        assertTrue(AffineTransform.checkAffine(0.5, 0.49, 0.5, 0.49));
        assertFalse(AffineTransform.checkAffine(1, 0.49, 0.5, 0.49));
    }

    @ParameterizedTest
    @ValueSource(strings = {"--linear --gamma 2 --sym 2 --help",
        "--linear --gamma 0.5 --sinusoidal --help",
        "--linear --sym 5 --sinusoidal --i 1000",
        "--sinusoidal --i 1000 --h 1080 --w 1920"})
    public void goodParams(final String arr) {
        assertDoesNotThrow(() -> Params.getParams(arr.split(" ")));
    }

    @Test
    public void transformsCheck() {
        final SecureRandom random = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            final double x = random.nextDouble();
            final double y = random.nextDouble();
            final double r = radius(x, y);

            assertEquals(sin(x), sinusoidal(x));

            assertEquals(x / (r * r), spherical(x, y, true));
            assertEquals(y / (r * r), spherical(x, y, false));

            assertEquals(x * sin(r * r) - y * cos(r * r), swirl(x, y, true));
            assertEquals(x * cos(r * r) - y * sin(r * r), swirl(x, y, false));

            assertEquals((x - y) * (x + y) / r, horseshoe(x, y, true));
            assertEquals(2 * x * y / r, horseshoe(x, y, false));

            final double coeff = random.nextDouble();
            assertEquals(x + coeff * sin(tan(3 * y)), popcorn(x, y, true, coeff));
            assertEquals(y + coeff * sin(tan(3 * x)), popcorn(x, y, false, coeff));
        }
    }

    private static double radius(final double x, final double y) {
        return Math.sqrt(x * x + y * y);
    }

    @ParameterizedTest
    @ValueSource(strings = {"--h 2 --gamma 2", "--swirl --unknown", "--swirl --h 2 --h 3", "--swirl --h"})
    public void badParams(final String arr) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Main.main(arr.split(" ")));
    }

    @SneakyThrows
    @Test
    public void ParallelVsSingleThread() {
        final List<String> transforms = List.of("--swirl", "--popcorn");
        final Params params1 = new Params(100, 100, 1000, "png", transforms, 1, 1, 1, "points");
        final long time1 = new FractalFlame(params1).create();

        for (int i = 1; i < 6; i++) {
            final Params params2 = new Params(100, 100, 1000, "png", transforms, 1, i * 5, 1, "points");
            final long time2 = new FractalFlame(params2).create();
            Assertions.assertTrue(time2 <= time1);
        }
    }
}
