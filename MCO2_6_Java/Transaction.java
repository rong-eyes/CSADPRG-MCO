/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public record Transaction(String accountName, String accountNumber, LocalDate date, LocalTime time,
                   String currency, double amount, String type, double balance){

    boolean isDeposit(){
        return type.equals("deposit");
    }

    LocalDateTime timestamp(){
        return LocalDateTime.of(date, time);
    }

    Transaction withBalance(double newBalance){
        return new Transaction(accountName, accountNumber, date, time, currency, amount, type, newBalance);
    }
}
