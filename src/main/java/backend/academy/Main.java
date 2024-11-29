package backend.academy;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    private static final PrintStream OUT = System.out;
    private static final String[] HINT = new String[] {
        "--help for this hint",
        "--h \"height\" for choosing height",
        "--w \"width\" for choosing width",
        "--i \"iterations\" for choosing count of iterations",
        "--format \"format\" for choosing format of picture",
        "--gamma \"gamma\" for choosing gamma coefficient",
        "--threads \"threads\" for choosing count of thread",
        "--linear | --sinusoidal | --spherical | --horseshoe | --popcorn some of this flags for choosing transforms"
    };

    public static void main(String[] args) throws IOException {
        OUT.println("run with --help for showing hint");
        if (args == null || Arrays.stream(args).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("must be not null array of not null params");
        } else if (Arrays.stream(args).anyMatch("--hint"::equalsIgnoreCase)) {
            Arrays.stream(HINT).forEach(OUT::println);
        }

        final Params params = Utils.getParams(args);
        OUT.printf("Working time for %d threads: %d", params.thread(), new FractalFlame(params).create());
    }
}
