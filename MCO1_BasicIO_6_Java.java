/*
********************
Last names: Balbanero, Sandoval, Racimo, Yu
Language: Java
Paradigm(s): Object-Oriented Programming
********************
*/

import java.util.*;

class Account{
    public String name;
    public double balance;
    public String currency;

    Account(){
        this.name = "Default";
        this.balance = 1000.00;
        this.currency = "PHP";
    }

    Account(String accName){
        this.name = accName;
        this.balance = 1000.00;
        this.currency = "PHP";
    }
}

class Currency{
    public String name;
    public double rate;

    Currency(String name, double rate){
        this.name = name;
        this.rate = rate;
    }
}

class Exchange{
    List<Currency> currencies = new ArrayList<>();

    Exchange(){
        currencies.add(new Currency("Philippine Peso (PHP)", 1.00));
        currencies.add(new Currency("United States Dollar (USD)", 62.00));
        currencies.add(new Currency("Japanese Yen (JPY)", 0.40));
        currencies.add(new Currency("British Pound Sterling (GBP)", 84.00));
        currencies.add(new Currency("Euro (EUR)", 72.00));
        currencies.add(new Currency("Chinese Yuan Renminbi (CNY)", 9.00));
    }

    public void displayCurrencies(){
        int i;

        for (i = 0; i < currencies.size(); i++){
            System.out.println("[" + (i+1) + "] " + currencies.get(i).name);
        }
    }
}

public class MCO1_BasicIO_6_Java{
    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        
        int choice;
        choice = 0;

        String depositor;
        double depositAmt;
        
        String withdrawer;
        double withdrawAmt;

        Account account = new Account();
        Exchange exchange = new Exchange();

        int currencyChoice;
        double exchangeAmt;

        System.out.println("Select Transaction:\n");
        System.out.println("[1] Register Account Name\n[2] Deposit Amount\n[3] Withdraw Amount");
        System.out.println("[4] Currency Exchange\n[5] Record Exchange Rates\n[6] Show Interest Amount\n[0] Exit\n");
        System.out.print("Choice: ");
        choice = sc.nextInt();
        sc.nextLine();
        System.out.println("*** \nChoice: " + choice + "\n");

        switch(choice){
            case 1:
                System.out.println("Register Account Name");
                System.out.print("Account Name: ");
                    
                account.name = sc.nextLine().trim();

                System.out.println("***");
                System.out.println("Account Name: " + account.name + "\n");

                break;

            case 2:
                System.out.println("Deposit Amount");
                System.out.print("Account Name: ");
                depositor = sc.nextLine().trim();

                System.out.println("Balance: " + account.balance);
                System.out.println("Currency: " + account.currency);

                System.out.print("Deposit Amount: ");
                depositAmt = sc.nextDouble();
                sc.nextLine();

                account.balance += depositAmt;

                System.out.println("\n***");
                System.out.println("Account Name: " + depositor);
                System.out.println("Deposit Amount: " + depositAmt);
                System.out.println("Balance: " + account.balance);
                
                break;

            case 3:
                System.out.println("Withdraw Amount");
                System.out.print("Account Name: ");
                withdrawer = sc.nextLine().trim();
               
                System.out.println("Balance: " + account.balance);
                System.out.println("Currency: " + account.currency);

                System.out.print("Withdraw Amount: ");
                withdrawAmt = sc.nextDouble();
                sc.nextLine();
                account.balance -= withdrawAmt;

                System.out.println("\n***");
                System.out.println("Account Name: " + withdrawer);
                System.out.println("Withdraw Amount: " + withdrawAmt);
                System.out.println("Balance: " + account.balance);
                
                break;

            case 4:
                System.out.println("Foreign Currency Exchange");

                System.out.print("Source Amount: ");
                exchangeAmt = sc.nextDouble();
                sc.nextLine();

                exchange.displayCurrencies();
                System.out.println("\n***");
                System.out.println("Source Currency: " + exchange.currencies.get(0).name);
                System.out.println("Source Amount (PHP): " + exchangeAmt);

                break;

            case 5:
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
                System.out.println("Exchange Rate = " + exchange.currencies.get(currencyChoice - 1).rate);

                break;

            case 6:
                System.out.println("Show Interest Amount");
                break;

            case 0:
                System.out.println("Exit");
                break;

            default:
                System.out.println("Please choose a valid option.");
        }
        

        sc.close();
    }
}
