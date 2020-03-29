package kolesnikov.ru.traps.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import kolesnikov.ru.traps.Objects.Trap;
import kolesnikov.ru.traps.R;

public class TrapsActivity extends AppCompatActivity {
    private Trap trap = new Trap();
    private ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ловушка");
        toolbar.setNavigationIcon(R.drawable.ic_trap);
        setSupportActionBar(toolbar);
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
            trap.setPhoto(extras.getString("photo"));
        }
    }

    private void init(){
        ivPhoto = findViewById(R.id.iv_photo);
    }

    private void setImage(){
        Bitmap picture = Trap.setImage(trap.getPhoto());
        ivPhoto.setImageBitmap(picture);
    }

}
