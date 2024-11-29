package backend.academy;

import java.util.List;

public record Params(int h, int w, int iterations, String format, List<String> transforms, double gamma, int thread) {
}
