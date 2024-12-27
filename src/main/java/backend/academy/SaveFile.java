package backend.academy;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SaveFile {
    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static void saveImg(final Pixel[][] pixels, final Params params) throws IOException {
        final BufferedImage image = new BufferedImage(params.w(), params.h(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < params.w(); x++) {
            for (int y = 0; y < params.h(); y++) {
                image.setRGB(x, y, new Color(pixels[x][y].r(), pixels[x][y].g(), pixels[x][y].b()).getRGB());
            }
        }

        ImageIO.write(image, params.format(), buildFilePath(params.format(), params.name()));
    }

    private static File buildFilePath(final String format, final String name) {
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
                + "\\src\\main\\resources\\" + name + "." + format);
    }
}
