package kolesnikov.ru.traps.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.R;
import kolesnikov.ru.traps.Utils.BitmapUtils;
import kolesnikov.ru.traps.Utils.DateUtils;
import kolesnikov.ru.traps.Utils.ToastUtils;
import kolesnikov.ru.traps.Utils.Utils;
import kolesnikov.ru.traps.permission.PermissionChecker;
import kolesnikov.ru.traps.servers.Server;

import static com.blikoon.qrcodescanner.utils.QrUtils.calculateInSampleSize;
import static com.google.ads.AdRequest.LOGTAG;
import static kolesnikov.ru.traps.Utils.BitmapUtils.saveBitmap;
import static kolesnikov.ru.traps.gui.TrapsAddActivity.REQUEST_CODE_QR_SCAN;
import static kolesnikov.ru.traps.gui.TrapsAddActivity.REQUEST_IMAGE_CAPTURE;
import static kolesnikov.ru.traps.gui.TrapsAddActivity.REQUEST_STORAGE;

public class TrapActivity extends AppCompatActivity {
    private Trap trap = new Trap();
    private ImageView ivPhoto;
    private ImageView ivQrCode;
    private ImageView ivAddPhoto;
    private Button btnSave;
    private SwitchCompat swTraceBittes;
    private SwitchCompat swAdhesivePlateReplacement;
    private SwitchCompat swIsTrapDamage;
    private SwitchCompat swIsTrapReplacement;
    private SwitchCompat swIsTrapReplacementDo;
    private EditText edNumberPests;
    private TextView tvDate;
    private TextView tvNumbers;
    private boolean isEdit = false;
    private Server server;
    private String mCurrentPhotoPath = "";
    private Handler handler;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trap);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ловушка");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.move_left_activity_out, R.anim.move_rigth_activity_in);
            }
        });
        getExtras();
        init();
        setImage();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (!isFinishing() && !isDestroyed() && dialog != null) {
                    dialog.dismiss();
                }
                if (msg.what == 1) {
                    showOkDialog2(TrapActivity.this, "Успешно сохранено");
                } else if (msg.what == 2) {

                    Snackbar.make(ivAddPhoto, "Ошибка, попробуйте позднее", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        };
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            trap.setId(extras.getString("id"));
            trap.setBarCode(extras.getString("barCode"));
            trap.setDateInspection(extras.getString("date"));
            trap.setTraceBittes(extras.getBoolean("traceBittes"));
            trap.setAdhesivePlateReplacement(extras.getBoolean("adhesivePlateReplacement"));
            trap.setNumberPests(extras.getInt("numberPests"));
            trap.setTrapDamage(extras.getBoolean("isTrapDamage"));
            trap.setTrapReplacement(extras.getBoolean("isTrapReplacement"));
            trap.setTrapReplacementDo(extras.getBoolean("isTrapReplacementDo"));
            trap.setPhoto(Utils.photo);
        }
    }

    private void init() {
        ivPhoto = findViewById(R.id.iv_photo);
        ivAddPhoto = findViewById(R.id.iv_add_photo);
        btnSave = findViewById(R.id.btn_save);
        swTraceBittes = (SwitchCompat) findViewById(R.id.sw_traceBittes);
        swAdhesivePlateReplacement = (SwitchCompat) findViewById(R.id.sw_adhesivePlateReplacement);
        swIsTrapDamage = (SwitchCompat) findViewById(R.id.sw_isTrapDamage);
        swIsTrapReplacement = (SwitchCompat) findViewById(R.id.sw_isTrapReplacement);
        swIsTrapReplacementDo = (SwitchCompat) findViewById(R.id.sw_isTrapReplacementDo);
        edNumberPests = findViewById(R.id.ed_number_pests);
        tvDate = findViewById(R.id.tv_date);
        tvNumbers = findViewById(R.id.tv_numbers);
        server = new Server(this);

        swTraceBittes.setChecked(trap.isTraceBittes());
        swAdhesivePlateReplacement.setChecked(trap.isAdhesivePlateReplacement());
        swIsTrapDamage.setChecked(trap.isTrapDamage());
        swIsTrapReplacement.setChecked(trap.isTrapReplacement());
        swIsTrapReplacementDo.setChecked(trap.isTrapReplacementDo());
        edNumberPests.setText(String.valueOf(trap.getNumberPests()));
        tvNumbers.setText(String.valueOf(trap.getId()));
        Date date = DateUtils.stringToDate(trap.getDateInspection());
        tvDate.setText(DateUtils.simpleDateFormat.format(date));

        initListeners();

    }

    private void initListeners() {
        swTraceBittes.setOnCheckedChangeListener(new OnchangeSw());
        swAdhesivePlateReplacement.setOnCheckedChangeListener(new OnchangeSw());
        swIsTrapReplacement.setOnCheckedChangeListener(new OnchangeSw());
        swIsTrapReplacementDo.setOnCheckedChangeListener(new OnchangeSw());
        swIsTrapDamage.setOnCheckedChangeListener(new OnchangeSw());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProcessingDialog(TrapActivity.this, "Сохранение...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String line = server.editTrap(trap.getId(), String.valueOf(swTraceBittes.isChecked()), String.valueOf(swAdhesivePlateReplacement.isChecked()),
                                edNumberPests.getText().toString(), String.valueOf(swIsTrapDamage.isChecked()),
                                String.valueOf(swIsTrapReplacement.isChecked()), String.valueOf(swIsTrapReplacementDo.isChecked()), trap.getPhoto());
                        if (line.equals("1")) {
                            handler.sendEmptyMessage(1);
                        }else {
                            handler.sendEmptyMessage(2);
                        }
                    }
                }).start();
            }
        });

        edNumberPests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEdit = true;
                showButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionChecker.isPermissionGranted(TrapActivity.this, Manifest.permission.CAMERA)) {
                    PermissionChecker.requestPermission(TrapActivity.this, Manifest.permission.CAMERA, REQUEST_IMAGE_CAPTURE);
                } else {
                    if (!PermissionChecker.isPermissionGranted(TrapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        PermissionChecker.requestPermission(TrapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE);
                    } else {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            java.io.File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                Snackbar.make(ivPhoto, "Не удалось получить доступ к памяти на устройстве", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            if (photoFile != null) {
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }

                        }
                    }
                }
            }
        });
    }

    private void showToastHere(String title) {
        if (!isFinishing() && !isDestroyed()) {
            ToastUtils.showToast(TrapActivity.this, title);
        }
    }

    public void showProcessingDialog(final Activity activity, final String errorText) {
        LayoutInflater layoutinflater = LayoutInflater.from(activity);
        View view = layoutinflater.inflate(R.layout.dialog_processing, null);
        android.app.AlertDialog.Builder errorDialog = new android.app.AlertDialog.Builder(activity);
        errorDialog.setView(view);
        TextView tvError = view.findViewById(R.id.tv_count_error_dialog_text);
        tvError.setText(errorText);
        dialog = errorDialog.create();
        dialog.setCancelable(false);
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }

    }

    public void showOkDialog2(final Activity activity, final String errorText) {
        LayoutInflater layoutinflater = LayoutInflater.from(activity);
        View view = layoutinflater.inflate(R.layout.ok_dialog_app, null);
        android.app.AlertDialog.Builder errorDialog = new android.app.AlertDialog.Builder(activity);
        errorDialog.setView(view);
        Button btnOk = view.findViewById(R.id.btn_error_dialog_ok);
        TextView message = view.findViewById(R.id.message);
        message.setText(errorText);
        final AlertDialog dialog = errorDialog.create();
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
                finish();
            }
        });
    }

    private void setImage() {
        Bitmap picture = Trap.setImage(trap.getPhoto());
        ivPhoto.setImageBitmap(picture);
    }

    class OnchangeSw implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            isEdit = true;
            showButton();
        }
    }

    private void showButton() {
        if (isEdit) {
            btnSave.setVisibility(View.VISIBLE);
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
                Snackbar.make(ivPhoto, "Не удалось считать QR код", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG, "Have scan result in your app activity :" + result);
            trap.setBarCode(result);
            ivQrCode.setImageResource(R.drawable.ic_qr_code);

            Snackbar.make(ivPhoto, "QR код успешно считан", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            Uri fileUri = Uri.fromFile(new File(mCurrentPhotoPath));

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                InputStream imageStream = getContentResolver().openInputStream(fileUri);
                BitmapFactory.decodeStream(imageStream, null, options);
                imageStream.close();

                options.inSampleSize = calculateInSampleSize(options, 360, 240);
                options.inJustDecodeBounds = false;
                imageStream = getContentResolver().openInputStream(fileUri);

                Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, options);
                bitmap = BitmapUtils.rotateImageIfRequired(TrapActivity.this, bitmap, fileUri);

                ivPhoto.setImageBitmap(bitmap);
                trap.setPhoto(saveBitmap(bitmap));
                Utils.photo = trap.getPhoto();
                isEdit = true;
                showButton();

            } catch (Exception e) {
            }

        }
    }

    private java.io.File createImageFile() throws IOException {

        // Если нет пути /Sminex/Pictures на девайсе, то создаем
        String rootPath = Environment.getExternalStorageDirectory().toString();
        java.io.File sminexRoot = new java.io.File(rootPath + "/Sminex");
        if (!sminexRoot.exists()) {
            sminexRoot.mkdir();
        }
        java.io.File sminexDir = new java.io.File(rootPath + "/Sminex/Media");
        if (!sminexDir.exists()) {
            sminexDir.mkdir();
        }

        sminexDir = new java.io.File(rootPath + "/Sminex/Media/Pictures");
        if (!sminexDir.exists()) {
            sminexDir.mkdir();
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        java.io.File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        java.io.File image = java.io.File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                sminexDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
