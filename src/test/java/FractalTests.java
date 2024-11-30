import backend.academy.FractalFlame;
import backend.academy.Main;
import backend.academy.Params;
import backend.academy.Utils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.security.SecureRandom;
import java.util.List;
import static backend.academy.Transforms.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.lang.Math.*;

public class FractalTests {

    @Test
    public void validAffine() {
        assertTrue(Utils.checkAffine(0.5, 0.49, 0.5, 0.49));
        assertFalse(Utils.checkAffine(1, 0.49, 0.5, 0.49));
    }

    @Test
    public void goodParams() {
        assertDoesNotThrow(() -> Main.main(new String[]{"--linear", "--gamma", "2", "--sym", "2", "--help"}));
        assertDoesNotThrow(() -> Main.main(new String[]{"--linear", "--gamma", "0.5", "--sinusoidal", "--help"}));
        assertDoesNotThrow(() -> Main.main(new String[]{"--linear", "--sym", "5", "--sinusoidal", "--i", "1000"}));
        assertDoesNotThrow(() -> Main.main(new String[]{"--sinusoidal", "--i", "1000", "--h", "1080", "--w", "1920"}));
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

    @Test
    public void badParams() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Main.main(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Main.main(new String[]{null}));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Main.main(new String[]{"--h", "2", "--gamma", "2"}));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Main.main(new String[]{"--swirl", "--unknown"}));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Main.main(new String[]{"--swirl", "--h", "2", "--h", "3"}));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Main.main(new String[]{"--swirl", "--h"}));
    }

    @SneakyThrows
    @Test
    public void ParallelVsSingleThread() {
        final List<String> transforms = List.of("--swirl", "--popcorn");
        final Params params1 = new Params(100, 100, 1000, "png", transforms, 1, 1, 1);
        final long time1 = new FractalFlame(params1).create();

        for (int i = 1; i < 6; i++) {
            final Params params2 = new Params(100, 100, 1000, "png", transforms, 1, i * 5, 1);
            final long time2 = new FractalFlame(params2).create();
            Assertions.assertTrue(time2 <= time1);
        }
    }
}
