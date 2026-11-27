/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

public class Account{
    public String name;
    public String number;
    public double balance;
    public String currency;

    Account(String number, String name, double balance){
        this.number = number;
        this.name = name;
        this.balance = balance;
        this.currency = "PHP";
    }
}
