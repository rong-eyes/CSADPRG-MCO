/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

public class MCO2_6_Java{
    static final String BACK_PROMPT = "Back to the Main Menu (Y/N): ";
    static final int INTEREST_RATE_PERCENT = 5;

    static Bank bank;
    static Exchange exchange = new Exchange();
    static Path csvPath;
    static DataSet dataset;

    static String money(double v){
        return String.format(Locale.US, "%.2f", v);
    }

    static boolean backToMenu(){
        System.out.println();
        return Input.askYesNo(BACK_PROMPT);
    }

    static void registerAccount(){
        do{
            System.out.println("Register Account");

            String name = Input.readLine("Account Name: ");

            while (!Bank.isValidName(name)){
                System.out.println("Invalid name.");
                name = Input.readLine("Account Name: ");
            }

            if (bank.findByName(name) != null){
                System.out.println("Account already exists.");
                continue;
            }

            BigDecimal initial = Input.parseAmount(Input.readLine("Initial Deposit: "), RoundingMode.DOWN);

            while (initial == null || initial.compareTo(Bank.MIN_INITIAL_DEPOSIT) < 0){
                System.out.println("Minimum initial deposit is " + Bank.MIN_INITIAL_DEPOSIT + ".");
                initial = Input.parseAmount(Input.readLine("Initial Deposit: "), RoundingMode.DOWN);
            }

            Account account = bank.register(name, initial);

            System.out.println("Account created successfully!");
            System.out.println("Account Name: " + account.name);
            System.out.println("Account Number: " + account.number);
            System.out.println("Currency: " + account.currency);
            System.out.println("Balance: " + money(account.balance));
        } while (!backToMenu());
    }

    static void depositAmount(){
        do{
            System.out.println("Deposit Amount");
            Account account = bank.findByName(Input.readLine("Account Name: "));

            if (account == null){
                System.out.println("Account does not exist.");
                continue;
            }

            System.out.println("Current Balance: " + money(account.balance));
            System.out.println("Currency: " + account.currency);
            System.out.println();

            BigDecimal amount = Input.parseAmount(Input.readLine("Deposit Amount: "), RoundingMode.DOWN);

            while (amount == null || amount.signum() <= 0){
                System.out.println("Invalid amount. Try again!");
                System.out.println();
                amount = Input.parseAmount(Input.readLine("Deposit Amount: "), RoundingMode.DOWN);
            }

            bank.deposit(account, amount);
            System.out.println("Updated Balance: " + money(account.balance));
        } while (!backToMenu());
    }

    static void withdrawAmount(){
        do{
            System.out.println("Withdraw Amount");
            Account account = bank.findByName(Input.readLine("Account Name: "));

            if (account == null){
                System.out.println("Account does not exist.");
                continue;
            }

            System.out.println("Current Balance: " + money(account.balance));
            System.out.println("Currency: " + account.currency);
            System.out.println();

            if (BigDecimal.valueOf(account.balance).signum() <= 0){
                System.out.println("Account is empty.");
                continue;
            }

            while (true){
                BigDecimal amount = Input.parseAmount(Input.readLine("Withdraw Amount: "), RoundingMode.UP);

                if (amount == null || amount.signum() <= 0){
                    System.out.println("Invalid amount.");
                }
                else if (!bank.withdraw(account, amount)){
                    System.out.println("Insufficient funds.");
                }
                else{
                    System.out.println("Updated Balance: " + money(account.balance));
                    break;
                }

                System.out.println();
            }
        } while (!backToMenu());
    }

    static void currencyExchange(){
        do{
            System.out.println("Foreign Currency Exchange");

            System.out.println("Source Currency Options:\n");
            exchange.displayCurrencies();
            int srcCurr = Input.readChoice("\nSource Currency: ", 1, exchange.currencies.size());
            double srcAmt = Input.readPositive("Source Amount: ");

            System.out.println("\n***");
            System.out.println("Available Currencies for Exchange: ");
            exchange.displayCurrencies();
            int dstCurr = Input.readChoice("\nCurrency to exchange to: ", 1, exchange.currencies.size());

            Currency src = exchange.currencies.get(srcCurr - 1);
            Currency dst = exchange.currencies.get(dstCurr - 1);

            System.out.println("\n***");

            if (src.rate == 0){
                System.out.println("Source currency has no recorded exchanged rate yet.");
            }
            else if (dst.rate == 0){
                System.out.println("Selected currency has no recorded exchange rate yet.");
            }
            else{
                double exchanged = (srcAmt * src.rate) / dst.rate;

                System.out.println("Source Currency: " + src.name);
                System.out.println("Source Amount: " + money(srcAmt));
                System.out.println("Exchanged Currency: " + dst.name);
                System.out.println("Exchanged Amount: " + money(exchanged));
            }

            System.out.println("\n***");
        } while (Input.askYesNo("Convert another currency? (Y/N): "));
    }

    static void recordExchangeRates(){
        do{
            System.out.println("Record Exchange Rates\n");
            exchange.displayCurrencies();

            int choice = Input.readChoice("\nSelect Foreign Currency: ", 1, exchange.currencies.size());
            Currency currency = exchange.currencies.get(choice - 1);

            System.out.println("\n***");

            if (choice == 1){
                System.out.println("The base currency is PHP. Exchange rate = 1.00");
            }
            else{
                currency.rate = Input.readPositive("Exchange Rate: ");

                System.out.println("\n***");
                System.out.println("Select Foreign Currency = " + currency.name);
                System.out.println("Exchange Rate = " + money(currency.rate));
            }
        } while (!backToMenu());
    }

    static void showInterest(){
        do{
            System.out.println("Show Interest Amount");
            Account account = bank.findByName(Input.readLine("Account Name: "));

            if (account == null){
                System.out.println("Account does not exist.");
                continue;
            }

            System.out.println("Current Balance: " + money(account.balance));
            System.out.println("Currency: " + account.currency);
            System.out.println("Interest Rate: " + INTEREST_RATE_PERCENT + "%");
            System.out.println();

            int days = readDays();

            System.out.println();
            System.out.printf("%-4s | %-8s | %-13s |%n", "Day", "Interest", "Balance");

            BigDecimal balance = BigDecimal.valueOf(account.balance);

            for (int day = 1; day <= days; day++){
                BigDecimal interest = Interest.daily(balance);
                balance = balance.add(interest);

                System.out.printf("%-4d | %-8s | %-13s |%n", day, interest.toPlainString(), balance.toPlainString());
            }
        } while (!backToMenu());
    }

    static int readDays(){
        while (true){
            try{
                int days = Integer.parseInt(Input.readLine("Total Number of Days: "));

                if (days > 0){
                    return days;
                }
            }
            catch (NumberFormatException e){
            }

            System.out.println("Invalid days.");
            System.out.println();
        }
    }

    static void generateReports(){
        do{
            System.out.println("Generate Reports\n");
            System.out.println("[1] Load the file\n[2] Generate Reports\n");

            int choice = readReportsChoice();

            if (choice == 1){
                loadFile();
            }
            else if (dataset == null){
                System.out.println("No transactions loaded.");
            }
            else if (dataset.transactions.isEmpty()){
                System.out.println("No transactions to report.");
            }
            else{
                writeReports(new ReportGenerator(dataset.transactions));
            }
        } while (!backToMenu());
    }

    static int readReportsChoice(){
        while (true){
            String answer = Input.readLine("Enter choice: ");

            if (answer.equals("1") || answer.equals("2")){
                return Integer.parseInt(answer);
            }

            System.out.println("Invalid input.");
            System.out.println();
        }
    }

    static void loadFile(){
        try{
            dataset = DataLoader.load(csvPath);

            System.out.printf(Locale.US, "(%,d rows loaded, %,d filtered for PHP, %,d invalid rows skipped)%n",
                    dataset.rowsRead, dataset.transactions.size(), dataset.errors.size());

            for (int i = 0; i < Math.min(5, dataset.errors.size()); i++){
                System.out.println("  " + dataset.errors.get(i));
            }
            if (dataset.errors.size() > 5){
                System.out.println("  ... and " + (dataset.errors.size() - 5) + " more");
            }
        }
        catch (IOException e){
            dataset = null;
            System.out.println("Could not open " + csvPath.getFileName() + ". Ensure that the file is in the same folder as the program.");
        }
    }

    static void writeReports(ReportGenerator gen){
        System.out.println("Generating reports...");

        List<Table> tables = List.of(gen.report1(), gen.report2(), gen.report3(), gen.report4());
        Path outDir = Paths.get(".");

        try{
            System.out.println("Outputs saved\n");

            for (Table t : tables){
                t.write(outDir);

                System.out.println(t.title);
                t.preview(2);
                System.out.println("(Full table exported to " + t.fileName + ")\n");
            }

            String json = gen.summaryJson();
            Files.writeString(outDir.resolve("summary.json"), json, StandardCharsets.UTF_8);

            System.out.println("summary.json:");
            System.out.print(json);
        }
        catch (IOException e){
            System.out.println("Could not write report: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        
        csvPath = Paths.get(args.length > 0 ? args[0] : "bank_transactions.csv");

        try{
            bank = new Bank(csvPath);
        }
        catch (IOException e){
            System.out.println("Could not read " + csvPath + ": " + e.getMessage());
            return;
        }

        try{
            runMenu();
        }
        catch (NoSuchElementException e){
            System.out.println();
            System.out.println("Exiting.");
        }

        Input.close();
    }

    static void runMenu(){
        boolean running = true;

        while (running){
            System.out.println("Select Transaction:\n");
            System.out.println("[1] Register Account\n[2] Deposit Amount\n[3] Withdraw Amount");
            System.out.println("[4] Currency Exchange\n[5] Record Exchange Rates\n[6] Show Interest Amount");
            System.out.println("[7] Generate Reports\n[0] Exit\n");

            String answer = Input.readLine("Choice: ");
            System.out.println();

            switch (answer){
                case "1": registerAccount(); break;
                case "2": depositAmount(); break;
                case "3": withdrawAmount(); break;
                case "4": currencyExchange(); break;
                case "5": recordExchangeRates(); break;
                case "6": showInterest(); break;
                case "7": generateReports(); break;
                case "0":
                    System.out.println("Exiting.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
                    System.out.println();
            }
        }
    }
}
