package kolesnikov.ru.traps.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blikoon.qrcodescanner.QrCodeActivity;

import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.R;
import kolesnikov.ru.traps.Utils.ToastUtils;
import kolesnikov.ru.traps.Utils.Utils;

public class TrapActivity extends AppCompatActivity {
    private Trap trap = new Trap();
    private ImageView ivPhoto;
    private ImageView ivQrCode;
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
    }

    private void getExtras(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
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

    private void init(){
        ivPhoto = findViewById(R.id.iv_photo);

    }

    private void initListeners(){

    }
    private void showToastHere(String title) {
        if (!isFinishing() && !isDestroyed()) {
            ToastUtils.showToast(TrapActivity.this, title);
        }
    }
    private void setImage(){
        Bitmap picture = Trap.setImage(trap.getPhoto());
        ivPhoto.setImageBitmap(picture);
    }
}
