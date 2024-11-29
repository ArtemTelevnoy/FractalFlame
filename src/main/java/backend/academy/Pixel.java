package backend.academy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public final class Pixel {
    private int counter = 0;
    private int r = 0;
    private int g = 0;
    private int b = 0;
}
