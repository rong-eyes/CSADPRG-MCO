/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ReportGenerator{
    static final double ANNUAL_INTEREST = 0.05;
    static final int PROJECTION_DAYS = 30;

    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final List<Transaction> data;
    private final Map<String, List<Transaction>> byAccount = new TreeMap<>();
    private final LocalDate asOf;

    ReportGenerator(List<Transaction> data){
        this.data = data;

        for (Transaction t : data){
            byAccount.computeIfAbsent(t.accountNumber(), k -> new ArrayList<>()).add(t);
        }

        // latest transaction date
        this.asOf = data.isEmpty() ? LocalDate.now() : data.get(data.size() - 1).date();
    }

    private static Transaction lastOf(List<Transaction> txs){
        return txs.get(txs.size() - 1);
    }

    // balance x (1 + rate/365)^30 - balance
    static double expectedInterest(double balance){
        return balance * (Math.pow(1 + ANNUAL_INTEREST / 365, PROJECTION_DAYS) - 1);
    }

    Table report1(){
        record Row(String name, long age, double current, double adb){}

        YearMonth month = YearMonth.from(asOf).minusMonths(1);
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<Row> out = new ArrayList<>();

        for (List<Transaction> txs : byAccount.values()){
            Map<LocalDate, Double> endOfDay = new HashMap<>();
            double balance = 0;

            for (Transaction t : txs){
                if (t.date().isBefore(start)){
                    balance = t.balance();                       // balance carried into the month
                }
                else if (!t.date().isAfter(end)){
                    endOfDay.put(t.date(), t.balance());         // last transaction of the day wins
                }
            }

            double sum = 0;

            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)){
                balance = endOfDay.getOrDefault(d, balance);
                sum += balance;
            }

            double adb = sum / month.lengthOfMonth();

            if (adb >= 100_000.00){
                out.add(new Row(lastOf(txs).accountName(),
                        ChronoUnit.DAYS.between(txs.get(0).date(), asOf),
                        lastOf(txs).balance(), adb));
            }
        }

        out.sort(Comparator.comparingDouble(Row::adb).reversed().thenComparing(Row::name));

        Table table = new Table("Report 1: Banking Privilege Tiers", "report1_banking_privilege_tiers.csv",
                "AccountName", "AccountAge", "CurrentBalance", "AverageDailyBalance", "PrivilegeTier");

        for (Row r : out){
            String tier = r.adb() < 500_000.00 ? "Silver" : (r.adb() < 1_000_000.00 ? "Gold" : "Platinum");
            table.rows.add(new String[]{r.name(), String.valueOf(r.age()), Csv.money(r.current()), Csv.money(r.adb()), tier});
        }

        return table;
    }



    Table report2(){
        record Row(String name, double balance, double interest){}

        List<Row> out = new ArrayList<>();

        for (List<Transaction> txs : byAccount.values()){
            Transaction last = lastOf(txs);

            if (last.balance() >= 10_000.00){
                out.add(new Row(last.accountName(), last.balance(), expectedInterest(last.balance())));
            }
        }

        out.sort(Comparator.comparingDouble(Row::interest).reversed().thenComparing(Row::name));

        Table table = new Table("Report 2: Interest Expense Projection", "report2_interest_expense_projection.csv",
                "AccountName", "CurrentBalance", "ExpectedInterest");

        for (Row r : out){
            table.rows.add(new String[]{r.name(), Csv.money(r.balance()), Csv.money(r.interest())});
        }

        return table;
    }

    private static class DayStats{
        final String name;
        final LocalDate date;
        double withdrawals = 0;
        double deposits = 0;
        int depositCount = 0;

        DayStats(String name, LocalDate date){
            this.name = name;
            this.date = date;
        }

        double velocity(){
            if (deposits > 0){
                return withdrawals / deposits * 100;
            }
            return withdrawals > 0 ? Double.POSITIVE_INFINITY : 0;
        }

        boolean suspicious(){
            return withdrawals >= 500_000.00 || velocity() >= 90;
        }
    }

    Table report3(){
        Map<String, DayStats> days = new LinkedHashMap<>();

        for (Transaction t : data){
            DayStats d = days.computeIfAbsent(t.date() + "|" + t.accountNumber(),
                    k -> new DayStats(t.accountName(), t.date()));

            if (t.isDeposit()){
                d.deposits += t.amount();
                d.depositCount++;
            }
            else{
                d.withdrawals += t.amount();
            }
        }

        List<DayStats> list = new ArrayList<>(days.values());


        list.sort(Comparator.comparing((DayStats d) -> d.date)
                .thenComparing(d -> d.suspicious() ? 0 : 1)
                .thenComparing(d -> d.name));

        Table table = new Table("Report 3: Suspicious Client Behavior", "report3_suspicious_client_behavior.csv",
                "AccountName", "Date", "TotalWithdrawalAmounts", "NumberOfDeposits", "TotalDepositAmounts",
                "TurnoverVelocity(%)", "RiskFlagLevel");

        for (DayStats d : list){
            double v = d.velocity();

            table.rows.add(new String[]{
                    d.name,
                    OUT_DATE.format(d.date),
                    Csv.money(d.withdrawals),
                    String.valueOf(d.depositCount),
                    Csv.money(d.deposits),
                    Double.isInfinite(v) ? "N/A" : Csv.money(v),
                    d.suspicious() ? "Suspicious" : "Normal"});
        }

        return table;
    }



    Table report4(){
        Map<YearMonth, List<Transaction>> months = new TreeMap<>();

        for (Transaction t : data){
            months.computeIfAbsent(YearMonth.from(t.date()), k -> new ArrayList<>()).add(t);
        }

        Table table = new Table("Report 4: Monthly Net Cash Flow", "report4_monthly_net_cash_flow.csv",
                "Month", "TotalDeposits", "TotalWithdrawals", "NetCashFlow", "TransactionCount",
                "ActiveAccounts", "MonthEndTotalBalance");

        Map<String, Double> balances = new HashMap<>();

        for (Map.Entry<YearMonth, List<Transaction>> e : months.entrySet()){
            double deposits = 0;
            double withdrawals = 0;
            Set<String> active = new HashSet<>();

            for (Transaction t : e.getValue()){
                if (t.isDeposit()){
                    deposits += t.amount();
                }
                else{
                    withdrawals += t.amount();
                }

                active.add(t.accountNumber());
                balances.put(t.accountNumber(), t.balance());
            }

            double totalBalance = 0;

            for (double b : balances.values()){
                totalBalance += b;
            }

            table.rows.add(new String[]{
                    e.getKey().toString(),
                    Csv.money(deposits),
                    Csv.money(withdrawals),
                    Csv.money(deposits - withdrawals),
                    String.valueOf(e.getValue().size()),
                    String.valueOf(active.size()),
                    Csv.money(totalBalance)});
        }

        return table;
    }


    
    String summaryJson(){
        double deposits = 0;
        double withdrawals = 0;
        double balance = 0;
        double interest = 0;

        for (Transaction t : data){
            if (t.isDeposit()){
                deposits += t.amount();
            }
            else{
                withdrawals += t.amount();
            }
        }

        for (List<Transaction> txs : byAccount.values()){
            double b = lastOf(txs).balance();
            balance += b;

            if (b >= 10_000.00){
                interest += expectedInterest(b);      // same population as Report 2
            }
        }

        return "{\n"
                + "  \"total_accounts\": " + byAccount.size() + ",\n"
                + "  \"total_deposits\": " + String.format(Locale.US, "%.2f", deposits) + ",\n"
                + "  \"total_withdrawals\": " + String.format(Locale.US, "%.2f", withdrawals) + ",\n"
                + "  \"total_balance\": " + String.format(Locale.US, "%.2f", balance) + ",\n"
                + "  \"total_interest_expense\": " + String.format(Locale.US, "%.2f", interest) + "\n"
                + "}\n";
    }
}
