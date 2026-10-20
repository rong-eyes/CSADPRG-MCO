/*
********************
Last names: Balbanero, Sandoval, Racimo, Yu
Language: Kotlin
Paradigm(s): Procedural
********************
*/

//kotlinc MCO1_6_Kotlin.kt -include-runtime -d MCO1.jar
//java -jar MCO1.jar

//FUNCTIONS 
fun registerAccountName(accounts: MutableList<BankAccount>, options: Map<Int, String>): BankAccount {
    println("Register Account Name\n")
    print("Account Name: ")
    var name = readln()

    if(accounts.any { it.accName == name }) {
        println("Account name already exists. Please choose a different name.\n")
        return registerAccountName(accounts,options) // Recursively call the function to prompt for a new name
    }else{
        printCurrencies(false)
        print("Source Currency: ")
        var currency = readln().toInt()
        if(options[currency] == null){
            println("Invalid Input. Please try again.\n")
            return registerAccountName(accounts, options) // Recursively call the function to prompt for a new name
        }else{
            
            println("\nAccount Registered!\n")

            return BankAccount(name, 0.0, options[currency] ?: "Unknown")
        }
    }
}

//the ff function is to print account details
fun printAccDetails(acc: BankAccount){
    println("Current Balance: ${acc.accountBalance}")
    println("Currency: ${acc.currency}")
}

fun depositAmount(accounts: MutableList<BankAccount>) {
    println("Deposit Amount\n")
    print("Account Name: ")
    var name = readln()
    if(accounts.any { it.accName == name }) {
        val account = accounts.find { it.accName == name }

        if(account == null) {
            println("Account not found.")
            return
        }else{
            printAccDetails(account)
            print("Deposit Amount: ")            
            var depositAmount: Double = readln().toDouble()
            if(depositAmount <= 0) {
                println("Deposit amount must be greater than zero. Please try again.\n")
                return
            }
            account.deposit(depositAmount)
            println("Updated Balance: ${account.accountBalance}\n")
        }
    } else {
        println("Account name does not exist.")
    }
}

fun withdrawAmount(accounts: MutableList<BankAccount>) {
    println("Withdraw Amount\n")
    print("Account Name: ")
    var name = readln()
    if(accounts.any { it.accName == name }) {
        val account = accounts.find { it.accName == name }

        if(account == null) {
            println("Account not found.")
            return
        }else if(account.accountBalance <= 0) {
            println("Insufficient balance. Cannot withdraw from an account with zero balance.")
            return
        }else{
            printAccDetails(account)
            print("Withdraw Amount: ")
            var withdrawAmount: Double = readln().toDouble()

            if(withdrawAmount <= 0) {
                println("Withdraw amount must be greater than zero. Please try again.")
                return
            }

            account.withdraw(withdrawAmount)
            println("Updated Balance: ${account.accountBalance}\n")
            }
    } else {
        println("Account name does not exist.")
    }
}

//for readbility
// change indictes if the function clling it is the currency exchange or record exchange rate, we cnnot change PHP rate hence the change parameter is used to determine if the PHP currency should be printed or not
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

//HELPER FUNCTIONS FOR CONVERTING CURRENCY
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
fun interestAmount(accounts: MutableList<BankAccount>) {
    println("Show Interest Amount\n")
    print("Account Name:")
    val name = readln()
    val account = accounts.find { it.accName == name }
    if(account == null) {
        println("Account name does not exist.")
        return
    }else{
        val annualInterest: Double = 0.05   //5% annual interest
        fun dailyInterest(bal: Double) = bal + (account.accountBalance * (annualInterest / 365))

        println("Interest Rate: ${annualInterest * 100}% \n\n")

        print("Total Number of Days: ")
        var totalDays = readln().toInt()
        var bal = account.accountBalance

        println("\nDay  | Interest  | Balance  |")
        for(i in 1..totalDays){
            val interest = dailyInterest(bal) - bal
            bal = dailyInterest(bal)
            println("${i.toString().padEnd(4)} | ${String.format("%.2f", interest).padEnd(9)} | ${String.format("%.2f", bal).padEnd(8)} |")
        }
    }
}

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

fun main(){
    var choice: Int = 1
    var accounts: MutableList<BankAccount> = mutableListOf()
    var back: Boolean = false

    //Map of Currency with default prices
    var CurrencyExchange = mutableMapOf("PHP" to 1.0, "USD" to 0.0, "JPY" to 0.0, "GBP" to 0.0, "EUR" to 0.0, "CNY" to 0.0)
    val currencyOptions = mapOf(1 to "PHP", 2 to "USD", 3 to "JPY", 4 to "GBP", 5 to "EUR", 6 to "CNY")

    do{
    //MAIN MENU PRINT STATEMENT
    println("\nSelect Transaction:\n[1] Register Account Name\n[2] Deposit Amount\n[3] Withdraw Amount\n[4] Currency Exchange\n[5] Record Exchange Rates \n[6] Show Interest Amount\n[0] Exit\n\nChoice:")
    var input = readln().trim().toIntOrNull()?: -1

    choice = input

    when(choice){
        //REGISTERING
        1 ->{
            back = false
            while(back == false){
                val newAccount = registerAccountName(accounts, currencyOptions)
                accounts.add(newAccount)
                back = backToMainMenu("Back to Main Menu")
            }
        }
        
        //DEPOSIT
        2 -> {
            back = false
            while(back == false){
                depositAmount(accounts)
                back = backToMainMenu("Back to Main Menu")
            }
        }

        //WITHDRAW
        3-> {
            back = false
            while(back == false){
                withdrawAmount(accounts)
                back = backToMainMenu("Back to Main Menu")
            }
        }

        //CURRENCY EXCHANGE
        4 -> {
            back = false
            while(back == false){
                currencyExchange(currencyOptions, CurrencyExchange)
                back = backToMainMenu("Back to Main Menu")
            }
        }

        //RECORD EXCHANGE
        5 -> {
            back = false
            while(back == false){
                CurrencyExchange = recordExchangeRate(CurrencyExchange, currencyOptions)
                back = backToMainMenu("Back to Main Menu")
            }
        }

        //SHOW INTEREST RATE
        6 -> {
            back = false
            while(back == false){
                interestAmount(accounts)
                back = backToMainMenu("Back to Main Menu")
            }
        }

        0 -> {
            println("Exiting the program.")
        }

        else -> {
            println("Invalid Input. Please try again.")
        }
    }

    }while(choice > 0 ) //0 is to terminate the progrm
}

class BankAccount(var accName: String, var accBal: Double, var curr: String) {
    
    val accountName: String = accName
    var accountBalance: Double = accBal
    var currency: String = curr
    
    fun deposit(amount: Double) {
        accountBalance += amount
    }

    fun withdraw(amount: Double) {
        accountBalance -= amount
    }

    fun getBalance(): Double {
        return accountBalance
    }
}