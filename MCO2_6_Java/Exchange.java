/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.util.ArrayList;
import java.util.List;

public class Exchange{
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
