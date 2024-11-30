package backend.academy;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import static backend.academy.Utils.RANDOM;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class FractalFlame {
    private static final int NUM_COUNT = 10000;
    private static final AffineTransform[] AFFINE = Utils.generateAffine(20);
    private final Params params;
    private final Pixel[][] pixels;

    public FractalFlame(Params params) {
        this.params = params;
        this.pixels = new Pixel[params.w()][params.h()];
    }

    public long create() throws IOException {
        final LocalDateTime before = LocalDateTime.now();
        Utils.saveImg(chaosGame(), params);
        final LocalDateTime after = LocalDateTime.now();
        return Duration.between(before, after).getSeconds();
    }

    private Pixel[][] chaosGame() {
        for (int i = 0; i < params.w(); i++) {
            for (int j = 0; j < params.h(); j++) {
                pixels[i][j] = new Pixel();
            }
        }

        final int xRes = params.w();
        final int yRes = params.h();
        final int rangeOfThread = NUM_COUNT / params.thread();

        final Thread[] threads = new Thread[params.thread()];
        for (int i = 0; i < params.thread(); i++) {
            final int j = i;
            threads[i] = new Thread(
                () -> fillPixels(j * rangeOfThread, Math.min((j + 1) * rangeOfThread, NUM_COUNT), xRes, yRes));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }

        return pixels;
    }

    @SuppressWarnings("MagicNumber")
    private void fillPixels(final int from, final int to, final int xRes, final int yRes) {
        for (int num = from; num < to; num++) {
            double newX = RANDOM.nextDouble(2) - 1;
            double newY = RANDOM.nextDouble(2) - 1;

            for (int step = -20; step < params.iterations(); step++) {
                final AffineTransform coeffs = AFFINE[RANDOM.nextInt(AFFINE.length)];
                final double x = coeffs.a() * newX + coeffs.b() * newY + coeffs.c();
                final double y = coeffs.d() * newX + coeffs.e() * newY + coeffs.f();

                final String transform = params.transforms().get(RANDOM.nextInt(params.transforms().size()));
                newX = Utils.applyTransform(x, y, true, coeffs.c(), transform);
                newY = Utils.applyTransform(x, y, false, coeffs.f(), transform);

                if (step >= 0) {
                    rotateSymAndSet(xRes, yRes, newX, newY, coeffs);
                }
            }
        }
    }

    private void rotateSymAndSet(
        final int xRes,
        final int yRes,
        final double newX,
        final double newY,
        final AffineTransform coeffs
    ) {
        double theta = 0;
        for (int s = 0; s < params.sym(); s++) {
            theta += 2 * Math.PI / params.sym();
            final double xRot = newX * cos(theta) - newY * sin(theta);
            final double yRot = newX * sin(theta) + newY * cos(theta);

            if (Math.abs(xRot) > 1 || Math.abs(yRot) > 1) {
                continue;
            }

            final int x1 = xRes - (int) ((1 - xRot) / 2 * xRes);
            final int y1 = yRes - (int) ((1 - yRot) / 2 * yRes);

            if (x1 >= xRes || y1 >= yRes) {
                continue;
            }

            synchronized (pixels[x1][y1]) {
                if (pixels[x1][y1].counter() == 0) {
                    pixels[x1][y1].r(coeffs.color().getRed());
                    pixels[x1][y1].g(coeffs.color().getGreen());
                    pixels[x1][y1].b(coeffs.color().getBlue());
                } else {
                    pixels[x1][y1].r((pixels[x1][y1].r() + coeffs.color().getRed()) / 2);
                    pixels[x1][y1].g((pixels[x1][y1].g() + coeffs.color().getGreen()) / 2);
                    pixels[x1][y1].b((pixels[x1][y1].b() + coeffs.color().getBlue()) / 2);
                }

                pixels[x1][y1].counter(pixels[x1][y1].counter() + 1);
            }
        }
    }
}
