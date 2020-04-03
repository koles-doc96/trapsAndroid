package kolesnikov.ru.traps.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;

public class Trap {
    @SerializedName("id")
    private String id = "";
    @SerializedName("barCode")
    private String barCode = "";
    @SerializedName("dateInspection")
    private String dateInspection;
    @SerializedName("traceBittes")
    private boolean traceBittes = false;
    @SerializedName("adhesivePlateReplacement")
    private boolean adhesivePlateReplacement = false;
    @SerializedName("numberPests")
    private int numberPests = 0;
    @SerializedName("isTrapDamage")
    private boolean isTrapDamage = false;
    @SerializedName("isTrapReplacement")
    private boolean isTrapReplacement = false;
    @SerializedName("isTrapReplacementDo")
    private boolean isTrapReplacementDo = false;
    @SerializedName("photo")
    private String photo = "";
    private Bitmap picture;

    /**
     * @param id                       id ловушки
     * @param barCode                  штрих код
     * @param dateInspection           дата проверки
     * @param traceBittes              следы прогрызов
     * @param adhesivePlateReplacement Произведена замена клеевой пластины (да/нет)
     * @param numberPests              Количество вредителей в ловушке ( число)
     * @param isTrapDamage             Ловушка повреждена (да/нет)
     * @param isTrapReplacement        Нужна замена ловушки(да/нет)
     * @param isTrapReplacementDo      Произведена ли замена ловушки (да/нет)
     * @param photo                    base64 фото
     */
    public Trap(String id, String barCode, String dateInspection, boolean traceBittes,
                boolean adhesivePlateReplacement, int numberPests, boolean isTrapDamage,
                boolean isTrapReplacement, boolean isTrapReplacementDo, String photo) {
        this.id = id;
        this.barCode = barCode;
        this.dateInspection = dateInspection;
        this.traceBittes = traceBittes;
        this.adhesivePlateReplacement = adhesivePlateReplacement;
        this.numberPests = numberPests;
        this.isTrapDamage = isTrapDamage;
        this.isTrapReplacement = isTrapReplacement;
        this.isTrapReplacementDo = isTrapReplacementDo;
        this.photo = photo;
    }

    public Trap() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getDateInspection() {
        return dateInspection;
    }

    public void setDateInspection(String dateInspection) {
        this.dateInspection = dateInspection;
    }

    public boolean isTraceBittes() {
        return traceBittes;
    }

    public void setTraceBittes(boolean traceBittes) {
        this.traceBittes = traceBittes;
    }

    public boolean isAdhesivePlateReplacement() {
        return adhesivePlateReplacement;
    }

    public void setAdhesivePlateReplacement(boolean adhesivePlateReplacement) {
        this.adhesivePlateReplacement = adhesivePlateReplacement;
    }

    public int getNumberPests() {
        return numberPests;
    }

    public void setNumberPests(int numberPests) {
        this.numberPests = numberPests;
    }

    public boolean isTrapDamage() {
        return isTrapDamage;
    }

    public void setTrapDamage(boolean trapDamage) {
        isTrapDamage = trapDamage;
    }

    public boolean isTrapReplacement() {
        return isTrapReplacement;
    }

    public void setTrapReplacement(boolean trapReplacement) {
        isTrapReplacement = trapReplacement;
    }

    public boolean isTrapReplacementDo() {
        return isTrapReplacementDo;
    }

    public void setTrapReplacementDo(boolean trapReplacementDo) {
        isTrapReplacementDo = trapReplacementDo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public static Bitmap setImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String imageStr = imageUrl.replaceAll("\"", "").replaceAll("\n", "");
            imageStr = StringUtils.remove(imageStr, "\\n");
            try {
                imageStr = imageStr.substring(imageUrl.indexOf(","));
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] imageAsBytes = Base64.decode(imageStr.getBytes(), Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }
        return null;
    }

    public static Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;
        try {
            YuvImage yuvimage = new YuvImage(string.getBytes(), ImageFormat.YUY2, 640  , 820, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, 20, 20), 100, baos);
            byte[] jdata = baos.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
        } catch (Exception e) {

        }
        return bitmap;
    }
}
