package com.ysbing.yadb;

import android.os.Looper;

import com.ysbing.yadb.input.Keyboard;
import com.ysbing.yadb.input.Touch;
import com.ysbing.yadb.layout.Layout;
import com.ysbing.yadb.screenshot.Screenshot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static final String PACKAGE_NAME = "com.android.shell";

    public static int USER_ID = getActiveUserId();

    private static final String ARG_KEY_BOARD = "-keyboard";
    private static final String ARG_TOUCH = "-touch";
    private static final String ARG_LAYOUT = "-layout";
    private static final String ARG_SCREENSHOT = "-screenshot";
    private static final String ARG_READ_CLIPBOARD = "-readClipboard";

    public static void main(String[] args) {
        try {
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> System.out.println(t.getName() + ",UncaughtException:" + getStackTraceAsString(e)));
            Looper.prepareMainLooper();
            if (check(args[0])) {
                switch (args[0]) {
                    case ARG_KEY_BOARD:
                        Keyboard.run(args[1]);
                        break;
                    case ARG_TOUCH:
                        if (args.length == 4) {
                            Touch.run(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Long.parseLong(args[3]));
                        } else {
                            Touch.run(Float.parseFloat(args[1]), Float.parseFloat(args[2]), -1L);
                        }
                        break;
                    case ARG_LAYOUT:
                        if (args.length == 2) {
                            Layout.run(args[1]);
                        } else {
                            Layout.run(null);
                        }
                        break;
                    case ARG_SCREENSHOT:
                        if (args.length == 2) {
                            Screenshot.run(args[1]);
                        } else {
                            Screenshot.run(null);
                        }
                        break;
                    case ARG_READ_CLIPBOARD:
                        Keyboard.readClipboard();
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("Invalid argument");
            }
        } catch (Throwable e) {
            System.out.println("MainException:" + getStackTraceAsString(e));
        }
    }

    private static int getActiveUserId() {
        Process process = null;
        BufferedReader reader = null;
        try {
            // Attempt to run the shell command
            process = Runtime.getRuntime().exec("am get-current-user");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            int exitCode = process.waitFor();
            if (exitCode == 0 && line != null && !line.trim().isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (Exception e) {
            // If something goes wrong, just print the error and return 0
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return 0;  // Default if we can't detect the user
    }

    private static boolean check(String arg) {
        return arg.equals(ARG_KEY_BOARD) || arg.equals(ARG_TOUCH) || arg.equals(ARG_LAYOUT) || arg.equals(ARG_SCREENSHOT) || arg.equals(ARG_READ_CLIPBOARD);
    }

    private static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
}
