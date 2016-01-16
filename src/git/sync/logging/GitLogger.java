package git.sync.logging;

import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by Unknown on 16/01/2016.
 */
public final class GitLogger {
    private GitLogger() {

    }

    public static final Logger getGitLogger(Class<?> klazz) {
        Logger logger = Logger.getLogger(klazz.getName());
        return logger;
    }

    public static final Logger getGitLogger(Class<?> klazz, Handler handler) {
        Logger logger = getGitLogger(klazz);
        logger.addHandler(handler);
        return logger;
    }
}
