/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Bank{
    static final String HEADER = "account_name,account_number,date,time,currency,amount,type,current_balance";
    static final double MIN_INITIAL_DEPOSIT = 5000.00;
    static final long FIRST_ACCOUNT_NUMBER = 1092000001L;

    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter OUT_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Path file;
    private final Map<String, Account> accounts = new TreeMap<>();

    Bank(Path file) throws IOException{
        this.file = file;

        if (Files.exists(file)){
            for (Transaction t : DataLoader.load(file).transactions){
                Account a = accounts.computeIfAbsent(t.accountNumber(), n -> new Account(n, t.accountName(), 0));
                a.name = t.accountName();
                a.balance = t.balance();
            }
        }
    }

    int accountCount(){
        return accounts.size();
    }

    Account findByName(String name){
        for (Account a : accounts.values()){
            if (a.name.equalsIgnoreCase(name.trim())){
                return a;
            }
        }
        return null;
    }

    private String nextAccountNumber(){
        long highest = FIRST_ACCOUNT_NUMBER - 1;

        for (String n : accounts.keySet()){
            highest = Math.max(highest, Long.parseLong(n));
        }

        return String.valueOf(highest + 1);
    }

    Account register(String name, double initialDeposit){
        Account a = new Account(nextAccountNumber(), name, 0);
        accounts.put(a.number, a);
        deposit(a, initialDeposit);
        return a;
    }

    void deposit(Account a, double amount){
        a.balance = Csv.round2(a.balance + amount);
        save(a, "deposit", amount);
    }

    boolean withdraw(Account a, double amount){
        if (amount > a.balance){
            return false;
        }

        a.balance = Csv.round2(a.balance - amount);
        save(a, "withdrawal", amount);
        return true;
    }
    
    private void save(Account a, String type, double amount){
        LocalDateTime now = LocalDateTime.now();

        String row = String.join(",",
                Csv.field(a.name),
                a.number,
                OUT_DATE.format(now),
                OUT_TIME.format(now),
                a.currency,
                String.format(Locale.US, "%.2f", amount),
                type,
                String.format(Locale.US, "%.2f", a.balance));

        try{
            StringBuilder sb = new StringBuilder();

            if (!Files.exists(file) || Files.size(file) == 0){
                sb.append(HEADER).append('\n');
            }
            else if (!endsWithNewline()){
                sb.append('\n');
            }

            sb.append(row).append('\n');
            Files.writeString(file, sb, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        catch (IOException e){
            throw new UncheckedIOException("Could not write to " + file, e);
        }
    }

    private boolean endsWithNewline() throws IOException{
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")){
            raf.seek(raf.length() - 1);
            return raf.read() == '\n';
        }
    }
}
