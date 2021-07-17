package Hardware;

import com.example.chargergui.BuildConfig;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ShellUtils;

public class Utils {
    static {
        // Set settings before the main shell can be created
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        );
    }

    public static boolean rootAccess() {
        return Shell.rootAccess();
    }

    public static void runCommand(String command) {
        Shell.su(command).exec();
    }

    public static void chmod(String permission, String path) {
        runCommand("chmod " + permission + " " + path);
    }

}