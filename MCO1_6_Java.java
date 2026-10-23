/*
********************
Last names: Balbanero, Sandoval, Racimo, Yu
Language: Java
Paradigm(s): Object-Oriented
********************
*/

import java.util.*;

class Account{
    public String name;
    public double balance;
    public String currency;

    Account(){
        this.name = "Default";
        this.balance = 0.00;
        this.currency = "";
    }

    Account(String accName){
        this.name = accName;
        this.balance = 0.00;
        this.currency = "";
    }
}

class Currency{
    public String name;
    public double rate;

    Currency(String name){
        this.name = name;
        this.rate = 0;
    }

    Currency(String name, double rate){
        this.name = name;
        this.rate = rate;
    }
}

class Exchange{
    List<Currency> currencies = new ArrayList<>();

    Exchange(){
        currencies.add(new Currency("Philippine Peso (PHP)",1));
        currencies.add(new Currency("United States Dollar (USD)"));
        currencies.add(new Currency("Japanese Yen (JPY)"));
        currencies.add(new Currency("British Pound Sterling (GBP)"));
        currencies.add(new Currency("Euro (EUR)"));
        currencies.add(new Currency("Chinese Yuan Renminbi (CNY)"));
    }

    public void displayCurrencies(){
        int i;

        for (i = 0; i < currencies.size(); i++){
            System.out.println("[" + (i+1) + "] " + currencies.get(i).name);
        }
    }
}


public class MCO1_6_Java{
    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        
        int choice;
        choice = 0;

        String name;
        boolean validAcc;

        boolean runMenu = true;
        String returnMenu;
        boolean returnMenuPrompt;
        boolean runOption = true;

        String depositor;
        double depositAmt;
        
        String withdrawer;
        double withdrawAmt;

        Account account = new Account();
        Exchange exchange = new Exchange();
        int accCurrency;
        boolean runCurrDisplay;

        int currencyChoice;
        double exchangeSrcAmt;
        int srcCurr;
        double exchangedAmt;

        String intAcc;
        double ANNUAL_INTEREST = 0.05;
        int intDays;
        int day;
        double intDaily;
        double intBalance;

        while (runMenu){
            System.out.println("Select Transaction:\n");
            System.out.println("[1] Register Account Name\n[2] Deposit Amount\n[3] Withdraw Amount");
            System.out.println("[4] Currency Exchange\n[5] Record Exchange Rates\n[6] Show Interest Amount\n[0] Exit\n");
            System.out.print("Choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            System.out.println("*** \nChoice: " + choice + "\n");

            runOption = true;

            switch(choice){
                case 1:
                    while (runOption) {
                        runCurrDisplay = true;

                        System.out.println("Register Account");

                        validAcc = false;

                        while (!validAcc){
                            System.out.print("Account Name: ");

                            name = sc.nextLine().trim();

                            if (name.isEmpty()){
                                System.out.println("Please provide an account name.");
                            }
                            else if (name.equalsIgnoreCase(account.name)){
                                System.out.println("Account already exists.");
                            }
                            else{
                                account.name = name;
                                validAcc = true;
                            }
                        }

                        System.out.println("\nSelect Account Currency:\n");
                        exchange.displayCurrencies();

                        while (runCurrDisplay) {
                            System.out.print("\nAccount Currency: ");
                            accCurrency = sc.nextInt();
                            sc.nextLine();

                            if (accCurrency < 1 || accCurrency > exchange.currencies.size()) {
                                System.out.println("Please select a valid currency option.");
                            }
                            else {
                                account.currency = exchange.currencies.get(accCurrency - 1).name;
                                runCurrDisplay = false;
                            }
                        }

                        System.out.println("***");
                        System.out.println("Account Name: " + account.name);
                        System.out.println("Account Currency: " + account.currency);

                        System.out.println("\n***");
                        System.out.print("Return to main menu? (Y/N): ");

                        returnMenuPrompt = true;

                        while (returnMenuPrompt) {
                            returnMenu = sc.nextLine().trim();

                            if (returnMenu.equalsIgnoreCase("Y")) {
                                runOption = false;
                                returnMenuPrompt = false;
                            }
                            else if (returnMenu.equalsIgnoreCase("N")) {
                                runOption = true;
                                returnMenuPrompt = false;
                            }
                            else {
                                System.out.println("Please choose a valid option.");
                            }
                        }
                    }

                    break;

                case 2:
                    while(runOption){
                        System.out.println("Deposit Amount");
                        System.out.print("Account Name: ");
                        depositor = sc.nextLine().trim();

                        if (depositor.equals(account.name)){
                            System.out.println("Balance: " + account.balance);
                            System.out.println("Currency: " + account.currency);

                            System.out.print("Deposit Amount: ");
                            depositAmt = sc.nextDouble();
                            sc.nextLine();

                            account.balance += depositAmt;

                            System.out.println("\n***");
                            System.out.println("Account Name: " + depositor);
                            System.out.println("Deposit Amount: " + String.format("%.2f", depositAmt));
                            System.out.println("Balance: " + String.format("%.2f", account.balance));
                        }
                        else{
                            System.out.println("Account not found.");
                        }

                        System.out.println("\n***");
                        System.out.print("Return to main menu? (Y/N): ");
                        returnMenuPrompt = true;

                        while (returnMenuPrompt){
                            returnMenu = sc.nextLine().trim();

                            if (returnMenu.equalsIgnoreCase("Y")){
                                runOption = false;
                                returnMenuPrompt = false;
                            }
                            else if (returnMenu.equalsIgnoreCase("N")){
                                runOption = true;
                                returnMenuPrompt = false;
                            }
                            else{
                                System.out.println("Please choose a valid option.");
                            }
                        }
                    }

                    break;

                case 3:
                    while(runOption){
                        System.out.println("Withdraw Amount");
                        System.out.print("Account Name: ");
                        withdrawer = sc.nextLine().trim();
                    
                        if (withdrawer.equals(account.name)){
                            System.out.println("Balance: " + String.format("%.2f", account.balance));
                            System.out.println("Currency: " + account.currency);

                            System.out.print("Withdraw Amount: ");
                            withdrawAmt = sc.nextDouble();
                            sc.nextLine();
                            account.balance -= withdrawAmt;

                            System.out.println("\n***");
                            System.out.println("Account Name: " + withdrawer);
                            System.out.println("Withdraw Amount: " + String.format("%.2f", withdrawAmt));
                            System.out.println("Balance: " + String.format("%.2f", account.balance));
                        }
                        else{
                            System.out.println("Account not found.");
                        }

                        System.out.println("\n***");
                        System.out.print("Return to main menu? (Y/N): ");

                        returnMenuPrompt = true;
                        
                        while (returnMenuPrompt){
                            returnMenu = sc.nextLine().trim();

                            if (returnMenu.equalsIgnoreCase("Y")){
                                runOption = false;
                                returnMenuPrompt = false;
                            }
                            else if (returnMenu.equalsIgnoreCase("N")){
                                runOption = true;
                                returnMenuPrompt = false;
                            }
                            else{
                                System.out.println("Please choose a valid option.");
                            }
                        }
                    }

                    break;

                case 4:
                    while(runOption){
                        System.out.println("Foreign Currency Exchange");

                        System.out.println("Source Currency Options:\n");
                        exchange.displayCurrencies();
                        System.out.print("\nSource Currency: ");
                        srcCurr = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Source Amount: ");
                        exchangeSrcAmt = sc.nextDouble();
                        sc.nextLine();

                        System.out.println("\n***");
                        System.out.println("Available Currencies for Exchange: ");
                        exchange.displayCurrencies();
                        System.out.print("\nCurrency to exchange to: ");
                        currencyChoice = sc.nextInt();
                        sc.nextLine();

                        if (exchange.currencies.get(srcCurr-1).rate == 0){
                            System.out.println("\n***");
                            System.out.println("Source currency has no recorded exchanged rate yet.");
                        }
                        else if (exchange.currencies.get(currencyChoice-1).rate == 0){
                            System.out.println("\n***");
                            System.out.println("Selected currency has no recorded exchange rate yet.");
                        }
                        else{
                            exchangedAmt = (exchangeSrcAmt * exchange.currencies.get(srcCurr - 1).rate) / exchange.currencies.get(currencyChoice - 1).rate;

                            System.out.println("\n***");
                            System.out.println("Source Currency: " + exchange.currencies.get(srcCurr-1).name);
                            System.out.println("Source Amount: " + String.format("%.2f", exchangeSrcAmt));
                            System.out.println("Exchanged Currency: " + exchange.currencies.get(currencyChoice-1).name);
                            System.out.println("Exchanged Amount: " + String.format("%.2f", exchangedAmt));
                        }

                        System.out.println("\n***");
                        System.out.print("Return to main menu? (Y/N): ");

                        returnMenuPrompt = true;

                        while (returnMenuPrompt){
                            returnMenu = sc.nextLine().trim();

                            if (returnMenu.equalsIgnoreCase("Y")){
                                runOption = false;
                                returnMenuPrompt = false;
                            }
                            else if (returnMenu.equalsIgnoreCase("N")){
                                runOption = true;
                                returnMenuPrompt = false;
                            }
                            else{
                                System.out.println("Please choose a valid option.");
                            }
                        }
                        
                    }

                    break;

                case 5:
                    while(runOption){
                        System.out.println("Record Exchange Rates\n");
                        exchange.displayCurrencies();

                        System.out.print("\nSelect Foreign Currency: ");
                        currencyChoice = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Exchange Rate: ");
                        exchange.currencies.get(currencyChoice - 1).rate = sc.nextDouble();
                        sc.nextLine();


                        System.out.println("\n***");
                        System.out.println("Select Foreign Currency = " + exchange.currencies.get(currencyChoice - 1).name);
                        System.out.println("Exchange Rate = " + String.format("%.2f", exchange.currencies.get(currencyChoice - 1).rate));

                        System.out.println("\n***");
                        System.out.print("Return to main menu? (Y/N): ");

                        returnMenuPrompt = true;

                        while (returnMenuPrompt){
                            returnMenu = sc.nextLine().trim();

                            if (returnMenu.equalsIgnoreCase("Y")){
                                runOption = false;
                                returnMenuPrompt = false;
                            }
                            else if (returnMenu.equalsIgnoreCase("N")){
                                runOption = true;
                                returnMenuPrompt = false;
                            }
                            else{
                                System.out.println("Please choose a valid option.");
                            }
                        }
                    }

                    break;

                case 6:
                    while (runOption) {
                        System.out.println("Show Interest\n");

                        System.out.print("Account Name: ");
                        intAcc = sc.nextLine().trim();

                        if(intAcc.equals(account.name)){
                            System.out.print("Total number of days: ");
                            intDays = sc.nextInt();
                            sc.nextLine();

                            while (intDays <= 0){
                                System.out.println("Please enter a number greater than 0.");
                                System.out.print("Enter number of expected days: ");
                                intDays = sc.nextInt();
                                sc.nextLine();
                            }

                            intBalance = account.balance;

                            System.out.println("\n***");
                            System.out.println("Current Account Balance: " + String.format("%.2f", account.balance));
                            System.out.println("Annual Interest Rate: 5%");
                            System.out.println("\nExpected Daily Interest:");
                            System.out.printf("%-10s | %-20s | %-20s%n", "Day", "Interest", "Balance");

                            for (day = 1; day <= intDays; day++){
                                intDaily = intBalance * (ANNUAL_INTEREST / 365);
                                intBalance += intDaily;

                                System.out.printf("%-10d | %-20.2f | %-20.2f%n", day, intDaily, intBalance);
                            }

                        }
                        else{
                            System.out.println("Account not found.");
                        }

                        System.out.println("\n***");
                        System.out.print("Return to main menu? (Y/N): ");

                        returnMenuPrompt = true;

                        while (returnMenuPrompt) {
                            returnMenu = sc.nextLine().trim();

                            if (returnMenu.equalsIgnoreCase("Y")) {
                                runOption = false;
                                returnMenuPrompt = false;
                            }
                            else if (returnMenu.equalsIgnoreCase("N")) {
                                runOption = true;
                                returnMenuPrompt = false;
                            }
                            else {
                                System.out.println("Please choose a valid option.");
                                System.out.print("Return to main menu? (Y/N): ");
                            }
                        }
                    }

                    break;

                case 0:
                    runMenu = false;
                    break;

                default:
                    System.out.println("Please choose a valid option.");
            }
        }
        
        sc.close();
    }
}
