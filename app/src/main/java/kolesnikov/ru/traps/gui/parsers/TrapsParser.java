package kolesnikov.ru.traps.gui.parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kolesnikov.ru.traps.Objects.Trap;

public class TrapsParser {

    public static List<Trap> parseTraps(String line) {
        List<Trap> traps = new ArrayList<>();

        try {
            JSONArray jsonTraps = new JSONArray(line);
            for(int i = 0; i<jsonTraps.length(); i++){
                JSONObject jsonTrap = jsonTraps.getJSONObject(i);
                String id = jsonTrap.getString("id");
                String barCode = jsonTrap.getString("barCode");
                String dateInspection = jsonTrap.getString("dateInspection");
                boolean traceBittes = jsonTrap.getBoolean("traceBittes");
                boolean adhesivePlateReplacement = jsonTrap.getBoolean("adhesivePlateReplacement");
                int numberPests = jsonTrap.getInt("numberPests");
                boolean isTrapDamage = jsonTrap.getBoolean("isTrapDamage");
                boolean isTrapReplacement = jsonTrap.getBoolean("isTrapReplacement");
                boolean isTrapReplacementDo = jsonTrap.getBoolean("isTrapReplacementDo");
                String photo = jsonTrap.getString("photo");

                traps.add(new Trap(id, barCode, dateInspection, traceBittes, adhesivePlateReplacement,
                        numberPests, isTrapDamage, isTrapReplacement, isTrapReplacementDo, photo));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return traps;
    }
}
