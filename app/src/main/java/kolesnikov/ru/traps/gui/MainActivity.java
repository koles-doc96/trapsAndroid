package kolesnikov.ru.traps.gui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.R;
import kolesnikov.ru.traps.Server;
import kolesnikov.ru.traps.gui.adapters.RecycleViewTrapsAdapter;
import kolesnikov.ru.traps.gui.parsers.TrapsParser;
import kolesnikov.ru.traps.permission.PermissionChecker;
import ru.github.igla.ferriswheel.FerrisWheelView;

import static com.google.ads.AdRequest.LOGTAG;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_VIVBRATE = 2;
    private static final int REQUEST_ACCESS_NETWORK_STATE = 3;
    private static final int REQUEST_INTERNET = 2;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private View btnFindTrap;
    private RecyclerView rvTraps;
    private List<Trap> traps = new ArrayList<>();
    private ProgressDialog dialog;
    private Handler handler;
    private RecycleViewTrapsAdapter recycleViewTrapsAdapter;
    private Server server = new Server(this);
    private FerrisWheelView ferrisWheelView;
    private FloatingActionButton btnAddTrap;
    private TextView tvTitleLoad;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        btnFindTrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);
                overridePendingTransition(R.anim.move_left_activity_out, R.anim.move_rigth_activity_in);
            }
        });

        btnAddTrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TrapsAddActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.move_left_activity_out, R.anim.move_rigth_activity_in);
            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    hideDialog();
                    recycleViewTrapsAdapter = new RecycleViewTrapsAdapter(traps, MainActivity.this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    rvTraps.setLayoutManager(layoutManager);
                    rvTraps.setAdapter(recycleViewTrapsAdapter);
                }
            }
        };
        getPermissions();
        getDataServer();
    }


    private void getPermissions() {
        if (!PermissionChecker.isPermissionGranted(this, Manifest.permission.CAMERA)) {
            PermissionChecker.requestPermission(this, Manifest.permission.CAMERA, REQUEST_CAMERA);
        }
        if (!PermissionChecker.isPermissionGranted(this, Manifest.permission.VIBRATE)) {
            PermissionChecker.requestPermission(this, Manifest.permission.VIBRATE, REQUEST_VIVBRATE);
        }
        if (!PermissionChecker.isPermissionGranted(this, Manifest.permission.INTERNET)) {
            PermissionChecker.requestPermission(this, Manifest.permission.VIBRATE, REQUEST_INTERNET);
        }
        if (!PermissionChecker.isPermissionGranted(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
            PermissionChecker.requestPermission(this, Manifest.permission.VIBRATE, REQUEST_ACCESS_NETWORK_STATE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(btnFindTrap, "Разрешено", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                return;
            case REQUEST_VIVBRATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(btnFindTrap, "Разрешено", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            default:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(btnFindTrap, "Разрешено", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Log.d(LOGTAG, "COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG, "Have scan result in your app activity :" + result);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }

    private void hideDialog() {
        if (!this.isFinishing() && !this.isDestroyed() && dialog != null) {
            dialog.dismiss();
        }
        ferrisWheelView.setVisibility(View.GONE);
        ferrisWheelView.stopAnimation();
        btnAddTrap.setVisibility(View.VISIBLE);
        btnFindTrap.setVisibility(View.VISIBLE);
        tvTitleLoad.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setSystemUiVisibility(View.VISIBLE);
    }

    private void getDataServer() {
//        dialog = new ProgressDialog(this);
//        dialog.setMessage("Синхронизация данных...");
//        dialog.setIndeterminate(true);
//        dialog.setCancelable(false);
//        dialog.show();
        setLoading();

        new Thread(new Runnable() {
            public void run() {
                String line = server.getTraps();
                traps = TrapsParser.parseTraps(line);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                handler.sendEmptyMessage(0);

            }
        }).start();
    }

    private void setLoading() {
        ferrisWheelView.startAnimation();
        ferrisWheelView.setVisibility(View.VISIBLE);
        btnAddTrap.setVisibility(View.GONE);
        btnFindTrap.setVisibility(View.GONE);
        tvTitleLoad.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.GONE);
        toolbar.setSystemUiVisibility(View.GONE);
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ловушки");
        toolbar.setNavigationIcon(R.drawable.ic_trap);
        setSupportActionBar(toolbar);

        rvTraps = findViewById(R.id.rv_traps);
        ferrisWheelView = findViewById(R.id.ferrisWheelView);
        btnFindTrap = findViewById(R.id.bt_find_trap);
        btnAddTrap = findViewById(R.id.bt_add_trap);
        tvTitleLoad = findViewById(R.id.title_load);
    }

    private void initListeners(){

    }

}
