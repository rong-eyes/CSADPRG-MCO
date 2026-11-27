/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class MCO2_6_Java{

    static final double ANNUAL_INTEREST = 0.05;
    static Bank bank;
    static Exchange exchange = new Exchange();
    static Path csvPath;

    static String money(double v){
        return String.format(Locale.US, "%.2f", v);
    }

    static void registerAccount(){
        do{
            System.out.println("Register Account");

            String name;
            double initial = Input.readDouble("Initial Deposit: ");

            while (true){
                name = Input.readLine("Account Name: ");

                if (name.isEmpty()){
                    System.out.println("Please provide an account name.");
                }
                else if (name.split("\\s+").length < 2){
                    System.out.println("Please enter your first name and last name.");
                }
                else if (bank.findByName(name) != null){
                    System.out.println("Account already exists.");
                }
                else{
                    break;
                }
            }

            while (initial < Bank.MIN_INITIAL_DEPOSIT){
                System.out.println("Minimum initial deposit is Php. " + Csv.money(Bank.MIN_INITIAL_DEPOSIT) + ".");
                initial = Input.readDouble("Initial Deposit: ");
            }

            Account account = bank.register(name, initial);

            System.out.println("\n***");
            System.out.println("Account Name: " + account.name);
            System.out.println("Account Number: " + account.number);
            System.out.println("Currency: " + account.currency);
            System.out.println("Balance: " + money(account.balance));
            System.out.println("Account created successfully!");
            System.out.println("\n***");
        }
        while (Input.askYesNo("Register another account? (Y/N): "));
    }

    static void depositAmount(){
        do{
            System.out.println("Deposit Amount");
            Account account = bank.findByName(Input.readLine("Account Name: "));

            if (account == null){
                System.out.println("Account not found.");
            }
            else{
                System.out.println("Current Balance: " + money(account.balance));
                System.out.println("Currency: " + account.currency);

                double amount = Input.readPositive("Deposit Amount: ");
                bank.deposit(account, amount);

                System.out.println("\n***");
                System.out.println("Account Name: " + account.name);
                System.out.println("Deposit Amount: " + money(amount));
                System.out.println("Updated Balance: " + money(account.balance));
            }

            System.out.println("\n***");
        }
        while (Input.askYesNo("Deposit another amount? (Y/N): "));
    }

    static void withdrawAmount(){
        do{
            System.out.println("Withdraw Amount");
            Account account = bank.findByName(Input.readLine("Account Name: "));

            if (account == null){
                System.out.println("Account not found.");
            }
            else{
                System.out.println("Current Balance: " + money(account.balance));
                System.out.println("Currency: " + account.currency);

                double amount = Input.readPositive("Withdraw Amount: ");

                System.out.println("\n***");

                if (bank.withdraw(account, amount)){
                    System.out.println("Account Name: " + account.name);
                    System.out.println("Withdraw Amount: " + money(amount));
                    System.out.println("Updated Balance: " + money(account.balance));
                }
                else{
                    System.out.println("Insufficient balance.");
                }
            }

            System.out.println("\n***");
        }
        while (Input.askYesNo("Withdraw another amount? (Y/N): "));
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
        }
        while (Input.askYesNo("Convert another currency? (Y/N): "));
    }

    static void recordExchangeRates(){
        do{
            System.out.println("Record Exchange Rates\n");
            exchange.displayCurrencies();

            int choice = Input.readChoice("\nSelect Foreign Currency: ", 1, exchange.currencies.size());
            Currency currency = exchange.currencies.get(choice - 1);

            System.out.println("\n***");

            if (choice == 1){
                System.out.println("PHP is the base currency; its exchange rate is fixed at 1.00.");
            }
            else{
                currency.rate = Input.readPositive("Exchange Rate: ");

                System.out.println("\n***");
                System.out.println("Select Foreign Currency = " + currency.name);
                System.out.println("Exchange Rate = " + money(currency.rate));
            }

            System.out.println("\n***");
        }
        while (!Input.askYesNo("Return to main menu? (Y/N): "));
    }

    static void showInterest(){
        do{
            System.out.println("Show Interest\n");
            Account account = bank.findByName(Input.readLine("Account Name: "));

            if (account == null){
                System.out.println("Account not found.");
            }
            else{
                int days = (int) Input.readPositive("Total number of days: ");
                double balance = account.balance;

                System.out.println("\n***");
                System.out.println("Account Name: " + account.name);
                System.out.println("Currency: " + account.currency);
                System.out.println("Current Account Balance: " + money(account.balance));
                System.out.println("Annual Interest Rate: 5%");
                System.out.println("\nExpected Daily Interest:");
                System.out.printf("%-10s | %-20s | %-20s%n", "Day", "Interest", "Balance");

                for (int day = 1; day <= days; day++){
                    double daily = balance * (ANNUAL_INTEREST / 365);
                    balance += daily;

                    System.out.printf(Locale.US, "%-10d | %-20.2f | %-20.2f%n", day, daily, balance);
                }
            }

            System.out.println("\n***");
        }
        while (!Input.askYesNo("Return to main menu? (Y/N): "));
    }

    static void generateReports(){
        DataSet dataset = null;
        boolean running = true;

        while (running){
            System.out.println("Generate Reports");
            System.out.println("[1] Load the file\n[2] Generate Reports\n[0] Back to main menu");
            int choice = Input.readChoice("Enter choice: ", 0, 2);

            switch (choice){
                case 1:
                    try{
                        System.out.println("Processing dataset...");
                        dataset = DataLoader.load(csvPath);

                        System.out.printf(Locale.US, "(%,d rows loaded, %,d filtered for PHP currency)%n",
                                dataset.rowsRead, dataset.transactions.size());
                        System.out.printf(Locale.US, "Validation: %,d non-PHP rows excluded, %,d rows with errors dropped, %,d missing balances imputed%n",
                                dataset.nonPhpRows, dataset.errors.size(), dataset.imputedBalances);

                        for (int i = 0; i < Math.min(5, dataset.errors.size()); i++){
                            System.out.println("  " + dataset.errors.get(i));
                        }
                        if (dataset.errors.size() > 5){
                            System.out.println("  ... and " + (dataset.errors.size() - 5) + " more");
                        }
                    }
                    catch (IOException e){
                        System.out.println("Could not read " + csvPath + ": " + e.getMessage());
                    }
                    break;

                case 2:
                    if (dataset == null){
                        System.out.println("Please load the file first.");
                    }
                    else if (dataset.transactions.isEmpty()){
                        System.out.println("No usable PHP transactions were found.");
                    }
                    else{
                        writeReports(new ReportGenerator(dataset.transactions));
                    }
                    break;

                default:
                    running = false;
            }

            System.out.println();
        }
    }

    static void writeReports(ReportGenerator gen){
        System.out.println("Generating reports...");

        List<Table> tables = List.of(gen.report1(), gen.report2(), gen.report3(), gen.report4());
        Path outDir = Paths.get(".");

        try{
            System.out.println("Outputs saved to individual files...\n");

            for (Table t : tables){
                t.write(outDir);

                System.out.println(t.title);
                t.preview(2);
                System.out.println("(Full table exported to " + t.fileName + ")\n");
            }

            String json = gen.summaryJson();
            Files.writeString(outDir.resolve("summary.json"), json, StandardCharsets.UTF_8);

            System.out.println("Summary State (summary.json):");
            System.out.print(json);
        }
        catch (IOException e){
            System.out.println("Could not write report files: " + e.getMessage());
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

        boolean runMenu = true;

        while (runMenu){
            System.out.println("Select Transaction:\n");
            System.out.println("[1] Register Account\n[2] Deposit Amount\n[3] Withdraw Amount");
            System.out.println("[4] Currency Exchange\n[5] Record Exchange Rates\n[6] Show Interest Amount");
            System.out.println("[7] Generate Reports\n[0] Exit\n");

            int choice = Input.readInt("Choice: ");

            System.out.println("*** \nChoice: " + choice + "\n");

            switch (choice){
                case 1: registerAccount(); break;
                case 2: depositAmount(); break;
                case 3: withdrawAmount(); break;
                case 4: currencyExchange(); break;
                case 5: recordExchangeRates(); break;
                case 6: showInterest(); break;
                case 7: generateReports(); break;
                case 0: runMenu = false; break;
                default: System.out.println("Please choose a valid option.");
            }
        }

        Input.close();
    }
}

