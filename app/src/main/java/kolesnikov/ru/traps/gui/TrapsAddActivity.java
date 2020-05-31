package kolesnikov.ru.traps.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.R;
import kolesnikov.ru.traps.servers.Server;
import kolesnikov.ru.traps.Utils.BitmapUtils;
import kolesnikov.ru.traps.Utils.ToastUtils;
import kolesnikov.ru.traps.Utils.Utils;
import kolesnikov.ru.traps.permission.PermissionChecker;

import static com.blikoon.qrcodescanner.utils.QrUtils.calculateInSampleSize;
import static com.google.ads.AdRequest.LOGTAG;
import static kolesnikov.ru.traps.Utils.BitmapUtils.saveBitmap;

public class TrapsAddActivity extends AppCompatActivity {
    private Trap trap = new Trap();
    private ImageView ivPhoto;
    private ImageView ivQrCode;
    public static final int REQUEST_CODE_QR_SCAN = 101;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_STORAGE = 2;
    private ImageView ivAddPhoto;
    private String mCurrentPhotoPath = "";
    boolean isQRCode = false;
    boolean isPhoto = false;
    private Button btnSave;
    private AlertDialog dialog;
    private SwitchCompat swTraceBittes;
    private SwitchCompat swAdhesivePlateReplacement;
    private SwitchCompat swIsTrapDamage;
    private SwitchCompat swIsTrapReplacement;
    private SwitchCompat swIsTrapReplacementDo;
    private EditText edNumberPests;
    private Server server;
    private Handler handler;
    private EditText edNameTrap;
    private EditText edComment;
    private EditText edCommentPhoto;
    private View llComentPhoto;
    private EditText edNumbers;
    private EditText edKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traps);
        server = new Server(this);
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
        init();
        initListeners();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(!isFinishing() && !isDestroyed() && dialog != null) {
                    dialog.dismiss();
                }
                if (msg.what == 0) {
                    Snackbar.make(ivAddPhoto, "Не удалось сохранить, QR code должен быть уникальным", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (msg.what == 1) {
                    showOkDialog2(TrapsAddActivity.this,  "Успешно сохранено");
                }else if (msg.what == 2) {

                    Snackbar.make(ivAddPhoto, "Ошибка, попробуйте позднее", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        };

    }
    public  void showOkDialog2(final Activity activity, final String errorText) {
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

    private void init(){
        ivPhoto = findViewById(R.id.iv_photo);
        ivQrCode = findViewById(R.id.iv_qr_code);
        ivAddPhoto = findViewById(R.id.iv_add_photo);
        btnSave = findViewById(R.id.btn_save);
        swTraceBittes = (SwitchCompat) findViewById(R.id.sw_traceBittes);
        swAdhesivePlateReplacement = (SwitchCompat) findViewById(R.id.sw_adhesivePlateReplacement);
        swIsTrapDamage = (SwitchCompat) findViewById(R.id.sw_isTrapDamage);
        swIsTrapReplacement = (SwitchCompat) findViewById(R.id.sw_isTrapReplacement);
        swIsTrapReplacementDo = (SwitchCompat) findViewById(R.id.sw_isTrapReplacementDo);
        edNumberPests =  findViewById(R.id.ed_number_pests);
        edNumbers = findViewById(R.id.ed_numbers);
        edNameTrap = findViewById(R.id.ed_name_trap);
        edComment = findViewById(R.id.ed_comment);
        edCommentPhoto = findViewById(R.id.ed_comment_photo);
        llComentPhoto = findViewById(R.id.ll_coment_photo);
        edKind = findViewById(R.id.ed_kind);
    }

    private void initListeners(){
        ivQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TrapsAddActivity.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);
            }
        });

        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionChecker.isPermissionGranted(TrapsAddActivity.this, Manifest.permission.CAMERA)) {
                    PermissionChecker.requestPermission(TrapsAddActivity.this, Manifest.permission.CAMERA, REQUEST_IMAGE_CAPTURE);
                } else {
                    if (!PermissionChecker.isPermissionGranted(TrapsAddActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        PermissionChecker.requestPermission(TrapsAddActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE);
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProcessingDialog(TrapsAddActivity.this, "Отправка на сервер..");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String line = "xxx";
                        Utils.photo = trap.getPhoto();
                        line = server.addTrap(trap.getBarCode(), String.valueOf(swTraceBittes.isChecked()), String.valueOf(swAdhesivePlateReplacement.isChecked()),
                                edNumberPests.getText().toString(), String.valueOf(swIsTrapDamage.isChecked()),
                                String.valueOf(swIsTrapReplacement.isChecked()), String.valueOf(swIsTrapReplacementDo.isChecked()), trap.getPhoto(),
                                edNumbers.getText().toString(), edComment.getText().toString(), edCommentPhoto.getText().toString(), edNameTrap.getText().toString(), edKind.getText().toString());

                        if(line.equals("1")){
                            handler.sendEmptyMessage(1);
                        }else if(line.equals("-1")){
                            handler.sendEmptyMessage(0);
                        }else {
                            handler.sendEmptyMessage(2);
                        }
                    }
                }).start();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(ivAddPhoto, "Разрешено", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                return;
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
            isQRCode = true;
            if(isPhoto){
                btnSave.setVisibility(View.VISIBLE);
            }
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
                bitmap = BitmapUtils.rotateImageIfRequired(TrapsAddActivity.this, bitmap, fileUri);

                ivPhoto.setImageBitmap(bitmap);
                trap.setPhoto(saveBitmap(bitmap));
                Utils.photo = trap.getPhoto();
                isPhoto = true;
                if(isQRCode){
                    btnSave.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
            }

        }
    }

    private void showToastHere(String title) {
        if (!isFinishing() && !isDestroyed()) {
            ToastUtils.showToast(TrapsAddActivity.this, title);
        }
    }

    private java.io.File createImageFile() throws IOException {

        // Если нет пути /Sminex/Pictures на девайсе, то создаем
        String rootPath = Environment.getExternalStorageDirectory().toString();
        java.io.File sminexRoot = new java.io.File(rootPath + "/Sminex");
        if(!sminexRoot.exists())
        {
            sminexRoot.mkdir();
        }
        java.io.File sminexDir = new java.io.File(rootPath + "/Sminex/Media");
        if(!sminexDir.exists())
        {
            sminexDir.mkdir();
        }

        sminexDir = new java.io.File(rootPath + "/Sminex/Media/Pictures");
        if(!sminexDir.exists())
        {
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

}
