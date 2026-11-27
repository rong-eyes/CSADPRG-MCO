/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
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
    static final BigDecimal MIN_INITIAL_DEPOSIT = new BigDecimal("5000.00");
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
            if (a.name.equals(name.trim())){
                return a;
            }
        }
        return null;
    }

    static boolean isValidName(String name){
        return !name.isEmpty() && name.length() <= 100 && !name.contains(",");
    }

    private String nextAccountNumber(){
        long highest = FIRST_ACCOUNT_NUMBER - 1;

        for (String n : accounts.keySet()){
            highest = Math.max(highest, Long.parseLong(n));
        }

        return String.valueOf(highest + 1);
    }

    // register
    Account register(String name, BigDecimal initialDeposit){
        Account a = new Account(nextAccountNumber(), name, 0);
        accounts.put(a.number, a);
        deposit(a, initialDeposit);
        return a;
    }

    void deposit(Account a, BigDecimal amount){
        a.balance = BigDecimal.valueOf(a.balance).add(amount).doubleValue();
        save(a, "deposit", amount);
    }

    // false when insufficient funds
    boolean withdraw(Account a, BigDecimal amount){
        BigDecimal balance = BigDecimal.valueOf(a.balance);

        if (amount.compareTo(balance) > 0){
            return false;
        }

        a.balance = balance.subtract(amount).doubleValue();
        save(a, "withdrawal", amount);
        return true;
    }

    // save transaction to csv
    private void save(Account a, String type, BigDecimal amount){
        LocalDateTime now = LocalDateTime.now();

        String row = String.join(",",
                Csv.field(a.name),
                a.number,
                OUT_DATE.format(now),
                OUT_TIME.format(now),
                a.currency,
                amount.toPlainString(),
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
