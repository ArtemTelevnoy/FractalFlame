package backend.academy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Point {
    private double x;
    private double y;
}
