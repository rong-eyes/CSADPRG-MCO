/*
********************
Last names: Balbanero, Sandoval, Racimo, Yu
Language: Kotlin
Paradigm(s): Object-Oriented
********************
*/

//kotlinc MCO2_Main.kt -include-runtime -d MCO2.jar
//java -jar MCO2.jar

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun main(){
    //Get current time and date
    val now: LocalDateTime = LocalDateTime.now() //must be called every time a transaction is made

    //transfer these lines of code into the file parser later
    val currentDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val current time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

    //from the file parser
    var totalAccounts = 90 //change later once done with the file parser 

}

//FUNCTIONS
fun registerAccountName(acc: MutableList<account>, options: Map<Int, String>): accounts {
    println("Register Account Name\n")
    print("Account Name: ")
    var name = readln()

    if(acc.any { it.accountName == name }) {
        println("Account name already exists. Please choose a different name.\n")
        return registerAccountName(acc,options) // Recursively call the function to prompt for a new name
    }else{
        print("Initial Deposit: ")
        var deposit = readln().toDouble()
        if(deposit < 5000){
            println("Initial Deposit must be at least 5,000.\n")
            return registerAccountName(accs, options) // Recursively call the function to prompt for a new name
        }else{
            totalAccounts+=1
            println("\nAccount created successfully!\n")

            return account(name, deposit, totalAccounts, now)
        }
    }
}

fun depositAmount(accounts: MutableList<account>) {
    println("Deposit Amount\n")
    print("Account Name: ")
    var name = readln()
    if(accounts.any { it.accountName == name }) {
        val acc = accounts.find { it.accountName == name }

        if(acc == null) {
            println("Account not found.")
            return
        }else{
            printAccDetails(acc)
            print("Deposit Amount: ")            
            var depositAmount: Double = readln().toDoubleOrNull()
            if(depositAmount <= 0) {
                println("Deposit amount must be greater than zero. Please try again.\n")
                return
            }else if(depositAmount == null){
                println("Invalid Input")
                return
            }
            acc.deposit(depositAmount)
            println("Updated Balance: ${String.format("%.2f",acc.currentBalance)}\n")
        }
    } else {
        println("Account name does not exist.")
    }
}

fun withdrawAmount(accounts: MutableList<account>) {
    println("Withdraw Amount\n")
    print("Account Name: ")
    var name = readln()
    if(accounts.any { it.accountName == name }) {
        val acc = accounts.find { it.accountName == name }

        if(acc == null) {
            println("Account not found.")
            return
        }else if(acc.currentBalance <= 0) {
            println("Insufficient balance. Cannot withdraw from an account with zero balance.")
            return
        }else{
            printAccDetails(acc)
            print("Withdraw Amount: ")
            var withdrawAmount: Double = readln().toDoubleOrNull()

            if(withdrawAmount <= 0) {
                println("Withdraw amount must be greater than zero. Please try again.")
                return
            }else if{
                println("Invalid Input")
                return
            }

            acc.withdraw(withdrawAmount)
            println("Updated Balance: ${String.format("%.2f",acc.currentBalance}\n")

            }

    } else {
        println("Account name does not exist.")
    }
}

//for readbility
// change indictes if the function calling it is the currency exchange or record exchange rate, 
//we cnnot change PHP rate hence the change parameter is used to determine if the PHP currency should be printed or not
fun printCurrencies(change: Boolean){
    if(change){
        println("[2] United States Dollar (USD)")
        println("[3] Japanese Yen (JPY)")
        println("[4] British Pound Sterling (GBP)")
        println("[5] Euro (EUR)")
        println("[6] Chinese Yuan Renminni (CNY)\n")
    }else{
        println("[1] Philippine Peso (PHP)")
        println("[2] United States Dollar (USD)")
        println("[3] Japanese Yen (JPY)")
        println("[4] British Pound Sterling (GBP)")
        println("[5] Euro (EUR)")
        println("[6] Chinese Yuan Renminni (CNY)\n")
    }
 
}

//HELPER FUNCTION FOR CONVERTING CURRENCY
fun convertCurrency(amount: Double, sourceRate: Double, targetRate: Double): Double {
    return (amount / targetRate) * sourceRate
}

fun currencyExchange(options: Map<Int, String>, currencies: MutableMap<String, Double>){
    println("Foreign Currency Exchange\nSource Currency Option:")
    printCurrencies(false)
    
    print("Source Currency: ")
    var sourceChoice = readln().toInt()

    if(sourceChoice <= 0){

        println("Invalid Input.")

    } else if(currencies[options[sourceChoice]] == 0.0){

        println("Exchange Rate for ${options[sourceChoice]} is not recorded yet. Please record the exchange rate first.\n")  
    
    }else {
        val sourceCurrency = currencies[options[sourceChoice]] ?: 0.0     //safety in case it maps to a null

        print("Source Amount: ")
        var sourceAmount = readln().toDouble()

        println("\nExchanged Currency Option:")
        printCurrencies(false)


        print("Exchange Currency: ")
        var exchangedChoice = readln().toInt()

        if(exchangedChoice <= 0){

            println("Invalid Input.")

        }else if(currencies[options[exchangedChoice]] == 0.0){

            println("Exchange Rate for ${options[exchangedChoice]} is not recorded yet. Please record the exchange rate first.\n")
        
        }else {

            val exchangeCurrency = currencies[options[exchangedChoice]] ?: 0.0     //safety in case it maps to a null 
            print("Exchange Rate: ${convertCurrency(sourceAmount, sourceCurrency, exchangeCurrency).let { String.format("%.2f", it) }} \n")
        }
    }
}

fun recordExchangeRate(currencies: MutableMap<String, Double>, options: Map<Int, String>): MutableMap<String, Double> {
    println("Record Exchange Rate\n")
    printCurrencies(true)

    print("Select Foreign Currency: ")
    var choice = readln().toInt()

    if(choice <= 0){

        println("Invalid Input.")

    }else {

        println("Exchange Rate: ")
        var exchangeRate = readln().toDouble()

        val selectedCurrency = options[choice]

        if(exchangeRate > 0.0 && selectedCurrency != null){
            currencies[selectedCurrency] = exchangeRate
            println("Exchange Rate for $selectedCurrency has been updated to $exchangeRate\n")
        }else{
            println("Invalid Input. Please try again.")
        }
    }

    return currencies
}

/*
    Interest Calculation
    currBal = current balance in the bank 
    bal = placeholder for recalculation of daily interest
*/
fun interestAmount(accounts: MutableList<account>) {
    println("Show Interest Amount\n")
    print("Account Name:")
    val name = readln()
    val acc = accounts.find { it.accountName == name }
    if(acc == null) {
        println("Account name does not exist.")
        return
    }else{
        val annualInterest: Double = 0.05   //5% annual interest
        fun dailyInterest(bal: Double) = bal + (acc.currentBalance * (annualInterest / 365))

        println("Interest Rate: ${annualInterest * 100}% \n\n")

        print("Total Number of Days: ")
        var totalDays = readln().toIntOrNull()

        if(totalDays == null){
            println("Input must be Integer.")
            return
        }

        var bal = acc.currentBalance

        println("\nDay  | Interest  | Balance  |")
        for(i in 1..totalDays){
            val interest = dailyInterest(bal) - bal
            bal = dailyInterest(bal)
            println("${i.toString().padEnd(4)} | ${String.format("%.2f", interest).padEnd(9)} | ${String.format("%.2f", bal).padEnd(8)} |")
        }
    }
}

fun generate_reports(){
    
}

//Helper function 
fun backToMainMenu(message: String): Boolean {
    while(true){
        print(message + " (Y/N): ")
        var back = readln()
        if(back == "Y" || back == "y"){
            return true
        }else if(back == "N" || back == "n"){
            return false
        }else{
            println("Invalid Input. Please try again.\n")
        }
    }
}