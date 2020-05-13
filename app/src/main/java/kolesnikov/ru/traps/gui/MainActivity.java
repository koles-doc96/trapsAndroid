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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import kolesnikov.ru.traps.Objects.Keys;
import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.R;
import kolesnikov.ru.traps.Utils.Utils;
import kolesnikov.ru.traps.servers.Server;
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
    private android.app.AlertDialog dialog;
    private Handler handler;
    private RecycleViewTrapsAdapter recycleViewTrapsAdapter;
    private Server server = new Server(this);
    private FerrisWheelView ferrisWheelView;
    private FloatingActionButton btnAddTrap;
    private TextView tvTitleLoad;
    private Toolbar toolbar;
    private Trap trap = null;
    private FloatingActionButton btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                hideDialog();
                if (msg.what == 0) {
                    recycleViewTrapsAdapter = new RecycleViewTrapsAdapter(traps, MainActivity.this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    rvTraps.setLayoutManager(layoutManager);
                    rvTraps.setAdapter(recycleViewTrapsAdapter);
                } else if (msg.what == 1) {
                    Intent intent = new Intent(MainActivity.this, TrapActivity.class);
                    intent.putExtra("id", trap.getId());
                    intent.putExtra("date", trap.getDateInspection());
                    intent.putExtra("traceBittes", trap.isTraceBittes());
                    intent.putExtra("adhesivePlateReplacement", trap.isAdhesivePlateReplacement());
                    intent.putExtra("numberPests", trap.getNumberPests());
                    intent.putExtra("isTrapDamage", trap.isTrapDamage());
                    intent.putExtra("isTrapReplacement", trap.isTrapReplacement());
                    intent.putExtra("isTrapReplacementDo", trap.isTrapReplacementDo());
                    intent.putExtra("customNumber", trap.getCustomNumber());
                    intent.putExtra("comment", trap.getComment());
                    intent.putExtra("commentPhoto", trap.getCommentPhoto());
                    intent.putExtra("nameTrap", trap.getNameTrap());
//                intent.putExtra("photo", trap.getPhoto());
                    Utils.photo = trap.getPhoto();
                    intent.putExtra("barCode", trap.getBarCode());
                    startActivity(intent);
                    overridePendingTransition(R.anim.move_left_activity_out, R.anim.move_rigth_activity_in);
                } else if (msg.what == 2) {
                    showOkDialog2(MainActivity.this, "Не удалось подключиться к серверу, попробуйте позднее");
                } else if (msg.what == 3) {
                    showOkDialog2(MainActivity.this, "Не удалось найти ловушку");
                }
            }
        };
        getPermissions();
        showKeyDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                alertDialog.setTitle("Ошибка сканирования");
                alertDialog.setMessage("QR коде считать не удалось");
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
            final String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG, "Have scan result in your app activity :" + result);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    trap = server.findTrap(result);
                    if (trap != null) {
                        handler.sendEmptyMessage(1);
                    } else {
                        try {
                            trap = findTrap(URLEncoder.encode(result, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (trap != null) {
                            handler.sendEmptyMessage(1);
                        } else {
                            System.out.println(result);
                            handler.sendEmptyMessage(3);
                        }
                    }
                }
            }).start();

        }
    }

    private Trap findTrap(String bacode) {
        Trap trap = null;
        for (Trap each : traps) {
            if (each.getBarCode().equals(bacode)) {
                return each;
            }
        }
        return trap;
    }

    private void hideDialog() {
        if (!this.isFinishing() && !this.isDestroyed() && dialog != null) {
            dialog.dismiss();
        }
        ferrisWheelView.setVisibility(View.GONE);
        ferrisWheelView.stopAnimation();
        btnAddTrap.setVisibility(View.VISIBLE);
        btnFindTrap.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.VISIBLE);
        rvTraps.setVisibility(View.VISIBLE);
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
                traps = server.getTraps();
                if (traps != null) {
                    handler.sendEmptyMessage(0);
                } else {
                    handler.sendEmptyMessage(2);
                }

            }
        }).start();
    }

    private void setLoading() {
        ferrisWheelView.startAnimation();
        ferrisWheelView.setVisibility(View.VISIBLE);
        btnAddTrap.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.GONE);
        rvTraps.setVisibility(View.GONE);
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
        btnUpdate = findViewById(R.id.bt_update);
        tvTitleLoad = findViewById(R.id.title_load);
        initListeners();
    }

    private void initListeners() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataServer();
            }
        });
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
    }

    public void showOkDialog2(final Activity activity, final String errorText) {
        LayoutInflater layoutinflater = LayoutInflater.from(activity);
        View view = layoutinflater.inflate(R.layout.ok_dialog_app, null);
        android.app.AlertDialog.Builder errorDialog = new android.app.AlertDialog.Builder(activity);
        errorDialog.setView(view);
        Button btnOk = view.findViewById(R.id.btn_error_dialog_ok);
        TextView message = view.findViewById(R.id.message);
        message.setText(errorText);
        final android.app.AlertDialog dialog = errorDialog.create();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                    dialog.dismiss();
                }
            }
        });
    }

    public void showKeyDialog(final Activity activity) {
        LayoutInflater layoutinflater = LayoutInflater.from(activity);
        View view = layoutinflater.inflate(R.layout.set_keys_dialog, null);
        android.app.AlertDialog.Builder errorDialog = new android.app.AlertDialog.Builder(activity);
        errorDialog.setView(view);
        final Button btnOk = view.findViewById(R.id.btn_error_dialog_ok);
        final ProgressBar progress = view.findViewById(R.id.progress);
        final EditText key = view.findViewById(R.id.ed_key);
        final android.app.AlertDialog dialog = errorDialog.create();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
            dialog.show();
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                    progress.setVisibility(View.VISIBLE);
                    btnOk.setVisibility(View.GONE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String keys = server.findkKey(key.getText().toString());
                            if (keys != null) {
                                dialog.dismiss();
                            } else {
                                progress.setVisibility(View.GONE);
                                btnOk.setVisibility(View.VISIBLE);
                                Snackbar.make(btnFindTrap, "Введите правильный лицензионный ключ", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                    });

                }
            }
        });
    }

}
