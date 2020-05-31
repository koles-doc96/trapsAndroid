package kolesnikov.ru.traps.servers;

import java.util.List;

import kolesnikov.ru.traps.Objects.Keys;
import kolesnikov.ru.traps.Objects.Trap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static kolesnikov.ru.traps.servers.Server.ADD_TRAP;
import static kolesnikov.ru.traps.servers.Server.EDIT_TRAP;
import static kolesnikov.ru.traps.servers.Server.FIND_KEY;
import static kolesnikov.ru.traps.servers.Server.FIND_TRAP_FOR_BARCODE;
import static kolesnikov.ru.traps.servers.Server.GET_TRAPS;

public interface IRetrofit {
    @POST(ADD_TRAP)
    Call<String> addTrap(@Query("barCode") String barCode, @Query("traceBittes") String traceBittes,
                         @Query("adhesivePlateReplacement") String adhesivePlateReplacement,
                         @Query("numberPests") String numberPests,
                         @Query("isTrapDamage") String isTrapDamage,
                         @Query("isTrapReplacement") String isTrapReplacement,
                         @Query("isTrapReplacementDo") String isTrapReplacementDo,
                         @Query("customNumber") String customNumber,
                         @Query("comment") String comment,
                         @Query("commentPhoto") String commentPhoto,
                         @Query("nameTrap") String nameTrap,
                         @Query("kind") String kind,
                         @Body String photo
    );

    @POST(EDIT_TRAP)
    Call<String> editTrap(@Query("id") String id,
                          @Query("traceBittes") String traceBittes,
                          @Query("adhesivePlateReplacement") String adhesivePlateReplacement,
                          @Query("numberPests") String numberPests,
                          @Query("isTrapDamage") String isTrapDamage,
                          @Query("isTrapReplacement") String isTrapReplacement,
                          @Query("isTrapReplacementDo") String isTrapReplacementDo,
                          @Query("customNumber") String customNumber,
                          @Query("comment") String comment,
                          @Query("commentPhoto") String commentPhoto,
                          @Query("nameTrap") String nameTrap,
                          @Query("kind") String kind,
                          @Body String photo
    );


    @GET(GET_TRAPS)
    Call<List<Trap>> getTraps();

    @GET(FIND_TRAP_FOR_BARCODE)
    Call<List<Trap>> findTrap(@Query("barCode") String barCode);
    @GET(FIND_KEY)
    Call<List<Keys>> findKey(@Query("key") String key);
}
