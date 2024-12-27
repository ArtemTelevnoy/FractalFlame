package backend.academy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"MultipleStringLiterals", "RecordComponentNumber"})
public record Params(
    int h,
    int w,
    int iterations,
    String format,
    List<String> transforms,
    double gamma,
    int thread,
    int sym,
    String name
) {
    private static final int DEFAULT_HEIGHT = 200;
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_ITERATIONS = 1000;
    private static final int DEFAULT_THREAD_COUNT = 1;
    private static final String DEFAULT_FORMAT = "png";
    private static final String DEFAULT_NAME = "points_image";
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

    private static void checkParams(final String[] args) {
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

    @SuppressWarnings({"CyclomaticComplexity", "ModifiedControlVariable"})
    public static Params getParams(final String[] args) {
        final String[] filterArgs = Arrays.stream(args).filter(o -> !"--help".equals(o)).toArray(String[]::new);
        checkParams(filterArgs);

        int h = DEFAULT_HEIGHT;
        int w = DEFAULT_WIDTH;
        int iterations = DEFAULT_ITERATIONS;
        int thread = DEFAULT_THREAD_COUNT;
        String format = DEFAULT_FORMAT;
        double gamma = DEFAULT_GAMMA;
        int sym = DEFAULT_SYM;
        String name = DEFAULT_NAME;
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
                    case "--name":
                        name = filterArgs[++i];
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

        return new Params(h, w, iterations, format, transforms.stream().toList(), gamma, thread, sym, name);
    }
}
