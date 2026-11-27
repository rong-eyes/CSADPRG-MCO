/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Csv{
    private Csv(){}

    static List<String> parse(String line){
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++){
            char c = line.charAt(i);

            if (quoted){
                if (c == '"'){
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"'){
                        sb.append('"');
                        i++;
                    }
                    else{
                        quoted = false;
                    }
                }
                else{
                    sb.append(c);
                }
            }
            else if (c == '"'){
                quoted = true;
            }
            else if (c == ','){
                fields.add(sb.toString());
                sb.setLength(0);
            }
            else{
                sb.append(c);
            }
        }

        fields.add(sb.toString());
        return fields;
    }

    static String field(String s){
        if (s.contains(",") || s.contains("\"") || s.contains("\n")){
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    static String money(double v){
        return String.format(Locale.US, "%,.2f", v);
    }

    static double round2(double v){
        return Math.round(v * 100.0) / 100.0;
    }
}
