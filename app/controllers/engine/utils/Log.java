package controllers.engine.utils;

import play.Logger;

/**
 * Created by pavelkuzmin on 18/05/15.
 */
public class Log {

    public enum State {
        Time, Categories, API, Research, Favicon, Tokens
    }

    public static Long getTime() {
        return System.nanoTime() / 1000000;
    }

    //strange bug
    public static void time(String text, final long timeMillis) {

        final Long time = getTime() - timeMillis;

        out(State.Time, text + ", time: " + time);

    }

    public static void out(State state, String text) {

        text = state + ": " + text;

        if (state == State.Time) {
//            System.out.println(text);
            Logger.debug(text);
        }

//        System.out.println(text);
//        Logger.debug(text);

    }

    public void error(State state) {

        System.err.println();
    }
}
