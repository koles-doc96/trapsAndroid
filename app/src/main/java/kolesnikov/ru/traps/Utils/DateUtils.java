package kolesnikov.ru.traps.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm:ss");

    public  static Date stringToDate(String date){
        Date resultDate = new Date();
        try{
            String stringDate="";

            resultDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

            System.out.println("Date is : "+resultDate);
        }catch(Exception e){
            System.out.println(e);
        }
        return resultDate;
    }
}
