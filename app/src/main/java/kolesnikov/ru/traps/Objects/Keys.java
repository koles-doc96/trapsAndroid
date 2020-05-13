package kolesnikov.ru.traps.Objects;

import com.google.gson.annotations.SerializedName;

public class Keys {
    @SerializedName("id")
    String id;
    @SerializedName("keysLic")
    String keysLic;

    public Keys(String id, String keysLic) {
        this.id = id;
        this.keysLic = keysLic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeysLic() {
        return keysLic;
    }

    public void setKeysLic(String keysLic) {
        this.keysLic = keysLic;
    }
}
