/*
********************
Last names: Balbanero, Sandoval, Racimo, Yu
Language: Kotlin
Paradigm(s): Procedural
********************
*/

//kotlinc MCO1_BasicIO_6_Kotlin.kt -include-runtime -d BasicIO.jar

//FUNCTIONS 
fun registerAccountName() {
    println("Register Account Name\n")
    print("Account Name: ")
    var accountName = readln()
    println("\n***\nAccount Name = $accountName\n")
}

fun depositAmount() {
    println("Deposit Amount\n")
    print("Account Name: ")
    var accountName = readln()
    println("Current Balance: 1000.00\nCurrency: PHP\n")    //hard-coded for milestone 1
    print("Deposit Amount: ")            
    var depositAmount: Double = readln().toDouble()
    println("\n***\nAccount Name = $accountName\nDeposit Amount = $depositAmount\n")
}

fun withdrawAmount() {
    println("Withdraw Amount\n")
    print("Account Name: ")
    var accountName = readln()
    println("Current Balance: 1000.00\nCurrency: PHP\n")    //hard-coded for milestone 1
    print("Withdraw Amount: ")           
    var withdrawAmount: Double = readln().toDouble()
    println("\n***\nAccount Name = $accountName\nWithdraw Amount = $withdrawAmount\n")
}

fun currencyExchange(){                                   //current exchange rates are hardcoded & 
    println("Foreign Currency Exchange\n")
    print("Source Amount (PHP): ")
    var accountBalance = readln().toDouble()                                
    println("\nExchanged Currency")
    println("[1] Philippine Peso (PHP) = " + accountBalance)
    println("[2] United States Dollar (USD) = " + accountBalance*62.00)
    println("[3] Japanese Yen (JPY) = " + accountBalance*0.40)
    println("[4] British Pound Sterling (GBP) = " + accountBalance*84.00)
    println("[5] Euro (EUR) = " + accountBalance*72.00)
    println("[6] Chinese Yuan Renminni (CNY) = " + accountBalance*9.00)
    println("\n***\nSource Currency = Philippine Peso (PHP)\nSource Amount = $accountBalance\n")
}

fun recordExchangeRate(){
    println("Record Exchange Rate\n")
    println("[1] Philippine Peso (PHP)")
    println("[2] United States Dollar (USD)")
    println("[3] Japanese Yen (JPY)")
    println("[4] British Pound Sterling (GBP)")
    println("[5] Euro (EUR)")
    println("[6] Chinese Yuan Renminni (CNY)\n")

    print("Select Foreign Currency: ")
    var choice = readln().toInt()
    println("Exchange Rate: ")
    var exchangeRate = readln().toDouble()

    println("\n***\nSelect Foreign Currency = [$choice]\nExchange Rate = $exchangeRate\n")
}

fun interestRate(){
    println("Show Interest Rate\n")
}

fun main(){

    //Selecting Choice
    println("Select Transaction:\n[1] Register Account Name\n[2] Deposit Amount\n[3] Withdraw Amount\n[4] Currency Exchange\n[5] Record Exchange Rates \n[6] Show Interest Amount \n\nChoice:")

    var choice = readln().toInt()

    println("\n***\nChoice = $choice\n")

    //Registeering
    registerAccountName()

    //Deposting
    depositAmount()

    //Withdrawing
    withdrawAmount()

    //Currency Exchange
    currencyExchange()

    //Record Exchange Rate
    recordExchangeRate()

    //Interest Rate
    interestRate()
}
