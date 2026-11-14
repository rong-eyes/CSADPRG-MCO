/*
********************
Last names: Balbanero, Sandoval, Racimo, Yu
Language: Kotlin
Paradigm(s): Object-Oriented
********************
*/

import java.time.temporal.ChronoUnit
import java.time.LocalDate

class account(var name: String, var id: Int, var date: LocalDate, var balance: Double){
    val accountName: String = name
    val accountNumber: Int = id
    var currentBalance: Double = bal
    var createdAt: LocalDate = date

    
    fun get_Age(): Int{
        return ChronoUnit.DAYS.between(createdAt, LocalDate.now()).toInt()
    }

    //for withdraw and deposit
    fun printAccountDetails(){
        println("Current Balance: $currentBalance")
        println("Currency: PHP") //PHP is default
    }

    fun get_currentBalance(): Double{
        return currentBalance
    }

    fun deposit(amount: Double){
        currentBalance+=amount
    }

    fun withdraw(amount: Double){
        currentBalance-=amount
    }

}