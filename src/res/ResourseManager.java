package res;

import java.io.InputStream;

public class ResourseManager {
    public static InputStream getResourseByPath(String path) {
        return ResourseManager.class.getResourceAsStream(path);
    }
}
