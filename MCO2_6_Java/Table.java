/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Table{
    final String title;
    final String fileName;
    final String[] header;
    final List<String[]> rows = new ArrayList<>();

    Table(String title, String fileName, String... header){
        this.title = title;
        this.fileName = fileName;
        this.header = header;
    }

    void write(Path dir) throws IOException{
        List<String> lines = new ArrayList<>();
        lines.add(String.join(",", header));

        for (String[] r : rows){
            lines.add(Arrays.stream(r).map(Csv::field).reduce((a, b) -> a + "," + b).orElse(""));
        }

        Files.write(dir.resolve(fileName), lines, StandardCharsets.UTF_8);
    }

    void preview(int n){
        System.out.println(String.join(" | ", header));

        for (int i = 0; i < Math.min(n, rows.size()); i++){
            System.out.println(String.join(" | ", rows.get(i)));
        }

        System.out.println("(" + rows.size() + " rows in total)");
    }
}
