/*
********************
Last names: Balbanero, Natividad, Racimo, Yu
Language: Kotlin
Paradigm(s): Procedural
********************
*/

fun main(){

    var choice: Int = 1
    var accountName: String = ""
    var accountBalance: Double = 1000.00
    var currency: String = "PHP"

    while(choice > 0){

    println("Select Transaction:\n[1] Register Account Name\n[2] Deposit Amount\n[3] Withdraw Amount\n[4] Currency Exchange\n[5] Record Exchange Rates \n[6] Show Interest Rate \n\nChoice:")

    choice = readln().toInt()

    println("\n***\nChoice = $choice\n")

        when (choice) {
        1 -> {
            println("Register Account Name\nAccount Name: ")
            accountName = readln()
            println("\n***\nAccount Name = $accountName\n")
        }
        2 -> {
            println("Deposit Amount\nAccount Name: $accountName\nCurrent Balance: $accountBalance\nCurrency: $currency\nDeposit Amount: ")
            var depositAmount: Double = readln().toDouble()
            println("\n***\nAccount Name = $accountName\nDeposit Amount = $depositAmount\n")
        }
        3 -> {
            println("Withdraw Amount\nAccount Name: $accountName\nCurrent Balance: $accountBalance\nCurrency: $currency\nWithdraw Amount: ")
            var withdrawAmount: Double = readln().toDouble()
            println("\n***\nAccount Name = $accountName\nWithdraw Amount = $withdrawAmount\n")
        }
        4 -> {
            println("Foreign Currency Exchange\nSource Amount (PHP): $accountBalance\n")
            println("Exchanged Currency")
            println("[1] Philippine Peso (PHP) = " + accountBalance)
            println("[2] United States Dollar (USD) = " + accountBalance*62.00)
            println("[3] Japanese Yen (JPY) = " + accountBalance*0.40)
            println("[4] British Pound Sterling (GBP) = " + accountBalance*84.00)
            println("[5] Euro (EUR) = " + accountBalance*72.00)
            println("[6] Chinese Yuan Renminni (CNY) = " + accountBalance*9.00)
            println("\n***\nSource Currency = Philippine Peso (PHP)\nSource Amount = $accountBalance\n")
        }
        5 -> {
            println("Record Exchange Rate\n")
            println("[1] Philippine Peso (PHP)")
            println("[2] United States Dollar (USD)")
            println("[3] Japanese Yen (JPY)")
            println("[4] British Pound Sterling (GBP)")
            println("[5] Euro (EUR)")
            println("[6] Chinese Yuan Renminni (CNY)\n")

            println("Select Foreign Currency:")
            var foreignCurrency: Int = readln().toInt()
            println("Exchange Rate:")
            var exchangeRate: Double = readln().toDouble()

            println("\n***\nForeign Currency = $foreignCurrency\nExchange Rate = $exchangeRate\n")
        }
        6 -> {
            println("Show Interest Rate\n")
        }
        else -> {
            println("Invalid Choice!")
        }
        }
    }
}