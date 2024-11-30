package backend.academy;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import lombok.experimental.UtilityClass;
import static java.lang.Math.pow;

@SuppressWarnings({"MultipleStringLiterals", "ModifiedControlVariable"})
@UtilityClass
public class Utils {
    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final int DEFAULT_HEIGHT = 200;
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_ITERATIONS = 1000;
    private static final int DEFAULT_THREAD_COUNT = 1;
    private static final String DEFAULT_FORMAT = "png";
    private static final double DEFAULT_GAMMA = 1;
    private static final int DEFAULT_SYM = 1;
    private static final Set<String> VALID_FORMATS = Set.of(
        DEFAULT_FORMAT,
        "jpeg",
        "bmp"
    );
    private static final Set<String> VALID_TRANSFORMS = Set.of(
        "--linear",
        "--sinusoidal",
        "--spherical",
        "--swirl",
        "--horseshoe",
        "--popcorn"
    );

    public double applyTransform(
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

    public void saveImg(final Pixel[][] pixels, final Params params) throws IOException {
        final BufferedImage image = new BufferedImage(params.w(), params.h(), BufferedImage.TYPE_INT_RGB);
        logAndGammaCorrection(pixels, params);

        for (int x = 0; x < params.w(); x++) {
            for (int y = 0; y < params.h(); y++) {
                image.setRGB(x, y, new Color(pixels[x][y].r(), pixels[x][y].g(), pixels[x][y].b()).getRGB());
            }
        }

        ImageIO.write(image, params.format(), buildFilePath(params.format()));
    }

    private File buildFilePath(final String format) {
        var classResource = Main.class.getClassLoader().getResource(Main.class.getName().replace('.', '/') + ".class");
        if (classResource == null) {
            throw new RuntimeException("Class not found");
        }
        File classFile;
        try {
            classFile = new File(classResource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return new File(
            classFile.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().toString()
                + "\\src\\main\\resources" + "\\points_image." + format);
    }

    private void logAndGammaCorrection(final Pixel[][] pixels, final Params params) {
        double maxCount = 0;
        for (int x = 0; x < params.w(); x++) {
            for (int y = 0; y < params.h(); y++) {
                maxCount = Math.max(maxCount, Math.log10(pixels[x][y].counter()));
            }
        }

        for (int x = 0; x < params.w(); x++) {
            for (int y = 0; y < params.h(); y++) {
                if (pixels[x][y].counter() == 0) {
                    continue;
                }

                final double c = Math.log10(pixels[x][y].counter()) / maxCount;
                pixels[x][y].r((int) (pixels[x][y].r() * pow(c, 1 / params.gamma())));
                pixels[x][y].g((int) (pixels[x][y].g() * pow(c, 1 / params.gamma())));
                pixels[x][y].b((int) (pixels[x][y].b() * pow(c, 1 / params.gamma())));
            }
        }
    }

    private void checkParams(final String[] args) {
        if (Arrays.stream(args).filter("--h"::equals).count() > 1
            || Arrays.stream(args).filter("--w"::equals).count() > 1
            || Arrays.stream(args).filter("--i"::equals).count() > 1
            || Arrays.stream(args).filter("--format"::equals).count() > 1
            || Arrays.stream(args).filter("--gamma"::equals).count() > 1
            || Arrays.stream(args).filter("--threads"::equals).count() > 1
            || Arrays.stream(args).filter("--sym"::equals).count() > 1) {
            throw new IllegalArgumentException("Some flags were repeated more than one time");
        }
    }

    @SuppressWarnings("CyclomaticComplexity")
    public Params getParams(final String[] args) {
        final String[] filterArgs = Arrays.stream(args).filter(o -> !"--help".equals(o)).toArray(String[]::new);
        checkParams(filterArgs);

        int h = DEFAULT_HEIGHT;
        int w = DEFAULT_WIDTH;
        int iterations = DEFAULT_ITERATIONS;
        int thread = DEFAULT_THREAD_COUNT;
        String format = DEFAULT_FORMAT;
        double gamma = DEFAULT_GAMMA;
        int sym = DEFAULT_SYM;
        final Set<String> transforms = new HashSet<>();

        try {
            for (int i = 0; i < filterArgs.length; i++) {
                switch (filterArgs[i]) {
                    case "--h":
                        h = Integer.parseInt(filterArgs[++i]);
                        break;
                    case "--w":
                        w = Integer.parseInt(filterArgs[++i]);
                        break;
                    case "--i":
                        iterations = Integer.parseInt(filterArgs[++i]);
                        break;
                    case "--format":
                        if (!VALID_FORMATS.contains(filterArgs[++i])) {
                            throw new IllegalArgumentException("Invalid picture format");
                        }

                        format = filterArgs[++i];
                        break;
                    case "--gamma":
                        gamma = Double.parseDouble(filterArgs[++i]);
                        break;
                    case "--threads":
                        thread = Integer.parseInt(filterArgs[++i]);
                        break;
                    case "--sym":
                        sym = Integer.parseInt(filterArgs[++i]);
                        break;
                    default:
                        if (!VALID_TRANSFORMS.contains(filterArgs[i])) {
                            throw new IllegalArgumentException("Invalid type of argument: " + filterArgs[i]);
                        }

                        transforms.add(filterArgs[i]);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Some flag without value", e);
        }

        if (iterations <= 0 || h <= 0 || w <= 0 || thread <= 0 || sym <= 0 || gamma <= 0) {
            throw new IllegalArgumentException("Num params must be positive");
        } else if (transforms.isEmpty()) {
            throw new IllegalArgumentException("Zero transforms");
        }

        return new Params(h, w, iterations, format, transforms.stream().toList(), gamma, thread, sym);
    }

    public boolean checkAffine(
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
    public AffineTransform[] generateAffine(final int count) {
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
            } while (!checkAffine(a, b, d, e));
            final double c = RANDOM.nextDouble(2) - 1;
            final double f = RANDOM.nextDouble(2) - 1;
            affine[i] =
                new AffineTransform(new Color(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256)),
                    a, b, c, d, e, f);
        }

        return affine;
    }
}
