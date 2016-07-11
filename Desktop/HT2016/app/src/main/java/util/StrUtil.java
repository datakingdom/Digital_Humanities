package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hongzhang on 8/24/15.
 */
public class StrUtil {
    public static String convertToString(InputStream is) {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(" ");
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
            } finally {
                try {
                    is.close();
                } catch (Exception e) {

                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
