/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLoader{
    // date mont year accepts both 01/12/2025 and 1/2/2025
    static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    static DataSet load(Path file) throws IOException{
        DataSet ds = new DataSet();
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<Transaction> raw = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++){
            String line = lines.get(i);

            if (i == 0 && line.startsWith("﻿")){
                line = line.substring(1);
            }
            if (line.isBlank()){
                continue;
            }
            if (i == 0 && line.toLowerCase().startsWith("account_name")){
                continue;
            }

            ds.rowsRead++;

            try{
                Transaction t = parseRow(Csv.parse(line));

                if (t == null){
                    ds.nonPhpRows++;
                }
                else{
                    raw.add(t);
                }
            }
            catch (IllegalArgumentException | DateTimeException e){
                ds.errors.add("Line " + (i + 1) + ": " + e.getMessage());
            }
        }

        raw.sort(Comparator.comparing(Transaction::timestamp));

        Map<String, Double> lastBalance = new HashMap<>();

        for (Transaction t : raw){
            if (Double.isNaN(t.balance())){
                double prev = lastBalance.getOrDefault(t.accountNumber(), 0.0);
                t = t.withBalance(Csv.round2(t.isDeposit() ? prev + t.amount() : prev - t.amount()));
                ds.imputedBalances++;
            }

            lastBalance.put(t.accountNumber(), t.balance());
            ds.transactions.add(t);
        }

        return ds;
    }

    private static Transaction parseRow(List<String> f){
        if (f.size() != 8){
            throw new IllegalArgumentException("expected 8 columns but found " + f.size());
        }

        String name = f.get(0).trim();
        String number = f.get(1).trim();
        String date = f.get(2).trim();
        String time = f.get(3).trim();
        String currency = f.get(4).trim().toUpperCase();
        String amount = f.get(5).trim();
        String type = f.get(6).trim().toLowerCase();
        String balance = f.get(7).trim();

        if (name.isEmpty() || number.isEmpty() || date.isEmpty() || time.isEmpty()
                || currency.isEmpty() || amount.isEmpty() || type.isEmpty()){
            throw new IllegalArgumentException("missing value");
        }

        LocalDate d = LocalDate.parse(date, DATE_FMT);
        LocalTime t = LocalTime.parse(time, TIME_FMT);

        if (!currency.equals("PHP")){
            return null;
        }
        if (!type.equals("deposit") && !type.equals("withdrawal")){
            throw new IllegalArgumentException("unknown transaction type '" + type + "'");
        }

        double amt = Double.parseDouble(amount.replace(",", ""));

        if (!Double.isFinite(amt) || amt <= 0){
            throw new IllegalArgumentException("invalid amount '" + amount + "'");
        }

        double bal = Double.NaN;

        if (!balance.isEmpty()){
            bal = Double.parseDouble(balance.replace(",", ""));

            if (!Double.isFinite(bal)){
                throw new IllegalArgumentException("invalid balance '" + balance + "'");
            }
        }

        return new Transaction(name, number, d, t, currency, amt, type, bal);
    }
}
