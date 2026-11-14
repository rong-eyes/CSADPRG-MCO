/*
********************
Last names: Balbanero, Sandoval, Racimo, Yu
Language: Kotlin
Paradigm(s): Object-Oriented
********************
*/

import java.io.File
import java.time.LocalDateTime

//centralized class that handles all the file reading and writing
//this is for the reports and for handling the accounts
class FileReadWrite(){
    val filename = "bank_transactions.csv"
    val transactionList = null
    var current_high_accNum = 0
    var totalRowCount = 0
    
    //load file command for generating reports
    //The flag will dictate what will be printed after
    fun load_file(): Boolean{
        try{
            transactionList = File(filename)
        } catch (e: Exception) {
            println("Error reading the file: ${e.message}")
            return false
        }
        return true
    }

    //this is the load file function for the overall program
    fun load_accounts(accounts: MutableList<account>): Boolean{
        //reads file and collects all the unique accounts
        try{
            transactionList = File(filename)
            transactionList.useLines { line -> //string of the current line being read
                lines.drop(1) //drops the header row
                     .map {it.split(",")}   //splits each line into a list of strings
                     .filter{col -> col.size > 8 && col.none {it.isBlank()}}
                     .forEach{ col ->
                        val value = temp(
                                name = col[0].trim(),
                                num = col[1].trim(),
                                age = LocalDate.parse(col[2].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                bal = col[7].trim())
                            }
                        if (accounts.none { it.accountNumber == temp.num}) {
                            accounts.add(value)
                        }
                      }
            }

            current_high_accNum = accounts.max()

            return true
        } catch (e: Exception){
            println("Error reading the file: ${e.message}")
            return false
        }
    }

    fun generate_accNumber(accounts: MutableList<account>): Int {
        //goes through the entire list looking for the highest
        //account number and then adds 1 to that and returns that value
        return ++current_high_accNum
    }

    //before passing the acc, have a null checker in main file
    fun write_transaction(acc: account, amount: Double, flag: Boolean){
        //true = deposit, false = withdrawal
        if(flag){
            transactionList.writeText("${acc.accountName},${acc.accountNumber},${LocalDate.now()},${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")},PHP,${String.format("%.2f", amount)},deposit,${String.format("%.2f", acc.currentBalance)}")
        }else{
            transactionList.writeText("${acc.accountName},${acc.accountNumber},${LocalDate.now()},${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")},PHP,${String.format("%.2f", amount)},withdrawal,${String.format("%.2f", acc.currentBalance)}")
        }
    }

    //FUNCTIONS THAT GENERATE THE REPORT

}