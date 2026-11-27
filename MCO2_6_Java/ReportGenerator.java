/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



public class ReportGenerator{
    static final int PROJECTION_DAYS = 30;
    private static final long CENTS = 100;
    private static final long ADB_FILTER = 100_000L * CENTS;
    private static final long GOLD_FROM = 500_000L * CENTS;
    private static final long PLATINUM_FROM = 1_000_000L * CENTS;
    private static final long INTEREST_FILTER = 10_000L * CENTS;
    private static final long BIG_WITHDRAWAL = 500_000L * CENTS;

    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final List<Transaction> data;
    private final Map<String, List<Transaction>> byAccount = new TreeMap<>(
            Comparator.comparingLong((String n) -> Long.parseLong(n)));
    private final LocalDate latest;

    ReportGenerator(List<Transaction> data){
        this.data = data;

        for (Transaction t : data){
            byAccount.computeIfAbsent(t.accountNumber(), k -> new ArrayList<>()).add(t);
        }

        this.latest = data.isEmpty() ? LocalDate.now() : data.get(data.size() - 1).date();
    }

    private static Transaction lastOf(List<Transaction> txs){
        return txs.get(txs.size() - 1);
    }

    private static long cents(double amount){
        return Math.round(amount * CENTS);
    }

    private static BigDecimal toMoney(long cents){
        return BigDecimal.valueOf(cents, 2);
    }


    Table report1(){
        record Row(long number, String name, long age, long balance, long sum, int days){}

        YearMonth latestMonth = YearMonth.from(latest);
        YearMonth month = latest.equals(latestMonth.atEndOfMonth()) ? latestMonth : latestMonth.minusMonths(1);
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        int days = month.lengthOfMonth();
        List<Row> out = new ArrayList<>();

        for (List<Transaction> txs : byAccount.values()){
            LocalDate opened = txs.get(0).date();

            if (opened.isAfter(end)){
                continue;
            }

            Map<LocalDate, Long> endOfDay = new HashMap<>();
            long balance = 0;

            for (Transaction t : txs){
                if (t.date().isBefore(start)){
                    balance = cents(t.balance());
                }
                else if (!t.date().isAfter(end)){
                    endOfDay.put(t.date(), cents(t.balance()));
                }
            }

            long sum = 0;

            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)){
                balance = endOfDay.getOrDefault(d, balance);
                sum += balance;
            }

            if (sum >= ADB_FILTER * days){
                out.add(new Row(Long.parseLong(lastOf(txs).accountNumber()), lastOf(txs).accountName(),
                        ChronoUnit.DAYS.between(opened, end), balance, sum, days));
            }
        }

        out.sort(Comparator.comparingLong(Row::sum).reversed().thenComparingLong(Row::number));

        Table table = new Table("Report 1: Banking Privilege Tiers", "report1_banking_privilege_tiers.csv",
                "AccountName", "AccountAge", "CurrentBalance", "AverageDailyBalance", "PrivilegeTier");

        for (Row r : out){
            BigDecimal adb = BigDecimal.valueOf(r.sum()).divide(BigDecimal.valueOf(CENTS * r.days()), 2, RoundingMode.HALF_UP);
            String tier = r.sum() < GOLD_FROM * r.days() ? "Silver"
                    : (r.sum() < PLATINUM_FROM * r.days() ? "Gold" : "Platinum");

            table.rows.add(new String[]{Csv.field(r.name()), String.valueOf(r.age()),
                    Csv.quotedMoney(toMoney(r.balance())), Csv.quotedMoney(adb), tier});
        }

        return table;
    }

    private record Projection(long number, String name, BigDecimal balance, BigDecimal interest){}

    private List<Projection> projections(){
        List<Projection> out = new ArrayList<>();

        for (List<Transaction> txs : byAccount.values()){
            Transaction last = lastOf(txs);

            if (cents(last.balance()) >= INTEREST_FILTER){
                BigDecimal balance = BigDecimal.valueOf(cents(last.balance()), 2);
                out.add(new Projection(Long.parseLong(last.accountNumber()), last.accountName(),
                        balance, Interest.projected(balance, PROJECTION_DAYS)));
            }
        }

        out.sort(Comparator.comparing(Projection::interest).reversed().thenComparingLong(Projection::number));
        return out;
    }

    Table report2(){
        Table table = new Table("Report 2: Interest Expense Projection", "report2_interest_expense_projection.csv",
                "AccountName", "CurrentBalance", "ExpectedInterest");

        for (Projection p : projections()){
            table.rows.add(new String[]{Csv.field(p.name()), Csv.quotedMoney(p.balance()), Csv.quotedMoney(p.interest())});
        }

        return table;
    }

    private static class DayStats{
        final long number;
        final String name;
        final LocalDate date;
        long withdrawals = 0;
        long deposits = 0;
        int depositCount = 0;

        DayStats(long number, String name, LocalDate date){
            this.number = number;
            this.name = name;
            this.date = date;
        }

        boolean hasVelocity(){
            return deposits > 0;
        }

        boolean suspicious(){
            return withdrawals >= BIG_WITHDRAWAL || (hasVelocity() && withdrawals * 100 >= 90 * deposits);
        }
    }

    Table report3(){
        Map<String, DayStats> days = new LinkedHashMap<>();

        for (Transaction t : data){
            DayStats d = days.computeIfAbsent(t.date() + "|" + t.accountNumber(),
                    k -> new DayStats(Long.parseLong(t.accountNumber()), t.accountName(), t.date()));

            if (t.isDeposit()){
                d.deposits += cents(t.amount());
                d.depositCount++;
            }
            else{
                d.withdrawals += cents(t.amount());
            }
        }

        List<DayStats> list = new ArrayList<>(days.values());

        list.sort(Comparator.comparing((DayStats d) -> d.date)
                .thenComparing(d -> d.suspicious() ? 0 : 1)
                .thenComparingLong(d -> d.number));

        Table table = new Table("Report 3: Suspicious Client Behavior", "report3_suspicious_client_behavior.csv",
                "AccountName", "Date", "TotalWithdrawalAmounts", "NumberOfDeposits", "TotalDepositAmounts",
                "TurnoverVelocity", "RiskFlagLevel");

        for (DayStats d : list){
            String velocity = "N/A";

            if (d.hasVelocity()){
                velocity = Csv.quotedMoney(BigDecimal.valueOf(d.withdrawals).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(d.deposits), 2, RoundingMode.HALF_UP));
            }

            table.rows.add(new String[]{
                    Csv.field(d.name),
                    OUT_DATE.format(d.date),
                    Csv.quotedMoney(toMoney(d.withdrawals)),
                    String.valueOf(d.depositCount),
                    Csv.quotedMoney(toMoney(d.deposits)),
                    velocity,
                    d.suspicious() ? "Suspicious" : "Normal"});
        }

        return table;
    }

    private static class Activity{
        final long number;
        final String name;
        int count = 0;
        long deposits = 0;
        long withdrawals = 0;
        long largestWithdrawal = 0;
        LocalDate lastDate;

        Activity(long number, String name){
            this.number = number;
            this.name = name;
        }
    }

    Table report4(){
        List<Activity> list = new ArrayList<>();

        for (List<Transaction> txs : byAccount.values()){
            Activity a = new Activity(Long.parseLong(lastOf(txs).accountNumber()), lastOf(txs).accountName());

            for (Transaction t : txs){
                long amount = cents(t.amount());
                a.count++;
                a.lastDate = t.date();

                if (t.isDeposit()){
                    a.deposits += amount;
                }
                else{
                    a.withdrawals += amount;
                    a.largestWithdrawal = Math.max(a.largestWithdrawal, amount);
                }
            }

            list.add(a);
        }

        list.sort(Comparator.comparingInt((Activity a) -> a.count).reversed().thenComparingLong(a -> a.number));

        Table table = new Table("Report 4: Customer Activity Summary", "report4_customer_activity.csv",
                "AccountName", "TransactionCount", "TotalDepositAmounts", "TotalWithdrawalAmounts",
                "LargestWithdrawal", "AverageTransactionAmount", "LastTransactionDate");

        for (Activity a : list){
            BigDecimal average = toMoney(a.deposits + a.withdrawals)
                    .divide(BigDecimal.valueOf(a.count), 2, RoundingMode.HALF_UP);

            table.rows.add(new String[]{
                    Csv.field(a.name),
                    String.valueOf(a.count),
                    Csv.quotedMoney(toMoney(a.deposits)),
                    Csv.quotedMoney(toMoney(a.withdrawals)),
                    Csv.quotedMoney(toMoney(a.largestWithdrawal)),
                    Csv.quotedMoney(average),
                    OUT_DATE.format(a.lastDate)});
        }

        return table;
    }

    String summaryJson(){
        long deposits = 0;
        long withdrawals = 0;
        long balance = 0;
        BigDecimal interest = BigDecimal.ZERO;

        for (Transaction t : data){
            if (t.isDeposit()){
                deposits += cents(t.amount());
            }
            else{
                withdrawals += cents(t.amount());
            }
        }

        for (List<Transaction> txs : byAccount.values()){
            balance += cents(lastOf(txs).balance());
        }

        for (Projection p : projections()){
            interest = interest.add(p.interest());
        }

        return "{\n"
                + "  \"total_accounts\": " + byAccount.size() + ",\n"
                + "  \"total_deposits\": " + toMoney(deposits).toPlainString() + ",\n"
                + "  \"total_withdrawals\": " + toMoney(withdrawals).toPlainString() + ",\n"
                + "  \"total_balance\": " + toMoney(balance).toPlainString() + ",\n"
                + "  \"total_interest_expense\": " + interest.setScale(2, RoundingMode.HALF_UP).toPlainString() + "\n"
                + "}\n";
    }
}
