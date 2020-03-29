package kolesnikov.ru.traps.permission;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionChecker {
    public static void requestPermission(Activity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    public static boolean isPermissionGranted(Activity activity, String permission) {
        int permissionCheck = ActivityCompat.checkSelfPermission(activity, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }
}
