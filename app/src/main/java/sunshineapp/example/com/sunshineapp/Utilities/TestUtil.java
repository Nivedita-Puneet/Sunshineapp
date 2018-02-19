package sunshineapp.example.com.sunshineapp.Utilities;

/**
 * Created by PUNEETU on 07-02-2017.
 */

public class TestUtil {

    public static void main(String[] args){
        String hello = new String("Hello");
        StringBuilder sb = new StringBuilder(hello).append(",").append("Hello");
        String result = sb.toString();
        System.out.println(result);
    }
}
