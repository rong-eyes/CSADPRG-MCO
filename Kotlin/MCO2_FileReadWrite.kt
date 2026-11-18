/*
********************
Last names: Balbanero, Sandoval, Racimo, Yu
Language: Kotlin
Paradigm(s): Object-Oriented
********************
*/

import java.io.File
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

//helper data classes
data class lastTransac(var transacdate: LocalDate, var bal: Double)

//centralized class that handles all the file reading and writing
//this is for the reports and for handling the accounts
class FileReadWrite(){
    val filename = "bank_transactions.csv"
    var transactionList: File? = null
    var current_high_accNum = 0             //used to generate id
    var totalInterest = 0.0 //for jason
    
    //load file command for generating reports
    fun loadFile(): Boolean {
    try {
        transactionList = File(filename)

        val clean = transactionList?.useLines { lines ->
            lines.drop(1)
                .map { it.split(",") }
                .filter { col ->
                    col.size == 8 &&
                    col.none { it.isBlank() }
                }
                .toList()
        } ?: emptyList()

        if (clean.isEmpty()) {
            println("No valid transactions found.")
            return false
        }
        return true

    } catch (e: Exception) {
        println("Error reading the file: ${e.message}")
        return false
    }
}


    //this is the load file function for the overall program
    
    fun load_accounts(accounts: MutableList<account>): Boolean {
        try {
            val file = File(filename)

            if (!file.exists()) {
                println("Transaction file not found!")
                println("Expected location: ${file.absolutePath}")
                return false
            }

            if (!file.isFile || file.length() == 0L) {
                println("Transaction file is empty or invalid.")
                return false
            }

            val lines = file.readLines()

            if (lines.size <= 1) {
                println("No previous transactions found.")
                return false
            }

            lines.drop(1)
                .filter { it.isNotBlank() }
                .forEach { line ->

                    val col = line.split(",")

                    if (col.size != 8) {
                        println("Invalid CSV row: $line")
                        return false
                    }

                    val value = account(
                        name = col[0].trim(),
                        id = col[1].trim().toInt(),
                        date = LocalDate.parse(col[2].trim(),DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        balance = col[7].trim().toDouble()
                    )

                    val existing = accounts.find {it.accountNumber == value.accountNumber}

                        if (existing == null) {
                            accounts.add(value)
                        } else {
                            existing.currentBalance = value.currentBalance
                        }
                }

            if (accounts.isEmpty()) {
                println("ERROR: No valid accounts were loaded.")
                return false
            }

            current_high_accNum =
                accounts.maxOfOrNull { it.accountNumber } ?: 0

            println("Successfully loaded ${accounts.size} account(s).")

            return true

        } catch (e: Exception) {
            println("ERROR loading accounts: ${e.message}")
            return false
        }
    }

    fun generate_accNumber(): Int {
        //goes through the entire list looking for the highest
        //account number and then adds 1 to that and returns that value
        return ++current_high_accNum
    }

    //before passing the acc, have a null checker in main file
    fun write_transaction(acc: account, amount: Double, flag: Boolean){
        //true = deposit, false = withdrawal
        val file = File(filename)
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val transactionType = if(flag) "deposit" else "withdrawal"
        val record = "${acc.accountName},${acc.accountNumber},$date,$time,PHP,${String.format("%.2f", amount)},$transactionType,${String.format("%.2f", acc.currentBalance)}\n"

        file.appendText(record)
    }

    //FUNCTIONS THAT GENERATE THE REPORT
    fun preview_report(read: File){
        //displays the first two lines
        val readOnly = 3 //basical;ly a stoppe for fiel reaidng

        read.useLines { lines ->
            for (line in lines.take(readOnly)){
                println(line)
            }
        }

        println("\n(Full table exported to ${read.name})\n")
    }

    fun privilege_report(acc: MutableList<account>): String {
        val tiers = arrayOf("Silver", "Gold", "Platinum")

        val lastMonth = YearMonth.from(LocalDate.now()).minusMonths(1)
        val startDate = lastMonth.atDay(1)
        val endDate = lastMonth.atEndOfMonth()
        val daysInMonth = lastMonth.lengthOfMonth()

        data class lastTransac(var transacdate: LocalDate,var bal: Double)
        val mapLastTransac = mutableMapOf<Int, lastTransac>()
        val mapEodSum = mutableMapOf<Int, Double>()

        val file = transactionList?: throw IllegalStateException("Transaction file has not been loaded.")

        // READ TRANSACTIONS
        file.useLines { lines ->
            lines.drop(1) //drops the header
                .forEach { line ->
                    val col = line.split(",")
                    if (col.size == 8) {

                        val currNum = col[1].trim().toInt()
                        val currDate = LocalDate.parse(col[2].trim(),DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        val currBal = col[7].trim().toDouble()

                        //lnly transactions up to the end of last month
                        if (!currDate.isAfter(endDate)) {
                            val previousTransac = mapLastTransac[currNum]
                            if (previousTransac == null) {
                                mapLastTransac[currNum] =
                                    lastTransac(currDate, currBal)
                            } else if (previousTransac.transacdate.isEqual(currDate)) {
                                previousTransac.bal = currBal //update balance if multiple transactions occur on the same day
                            } else if (previousTransac.transacdate.isBefore(currDate)) {
                                //assumulate the previous balance for the days it was held
                                val fill = maxOf(previousTransac.transacdate, startDate)

                                if (fill.isBefore(currDate)) {
                                    val daysHeld = ChronoUnit.DAYS.between(fill, currDate)

                                    if (daysHeld > 0) {
                                        mapEodSum[currNum] =
                                            (mapEodSum[currNum] ?: 0.0) +
                                            (previousTransac.bal * daysHeld)
                                    }
                                }

                                previousTransac.transacdate = currDate
                                previousTransac.bal = currBal
                            }
                        }
                    }
                }
        }

        // FILL REMAINING DAYS OF THE MONTH
        for ((accNum, last) in mapLastTransac) {

            val fill = maxOf(last.transacdate, startDate)

            if (!fill.isAfter(endDate)) {

                val remaining = ChronoUnit.DAYS.between(fill, endDate) + 1

                mapEodSum[accNum] =
                    (mapEodSum[accNum] ?: 0.0) +
                    (last.bal * remaining)
            }
        }

        // CALCULATE AVERAGE DAILY BALANCE
        val mapAvgDailyBal = mapEodSum.mapValues {it.value / daysInMonth}

        // FILE WRITING PORTION
        val report1 = File("report1_banking_privilege_tiers.csv")

        report1.bufferedWriter().use { writer ->

            writer.write("AccountName,AccountAge,CurrentBalance,AverageDailyBalance,PrivilegeTier") //writes header
            writer.newLine()

            // SORT BY DESCENDING AVERAGE DAILY BALANCE
            mapAvgDailyBal.entries
                .sortedByDescending { it.value }
                .forEach { (accNum, avgBal) ->

                    val current = acc.find {
                        it.accountNumber == accNum
                    }
                    if (current != null && avgBal >= 100000.0) {
                        val tier = when {
                            avgBal < 500000.0 -> tiers[0]
                            avgBal < 1000000.0 -> tiers[1]
                            else -> tiers[2]
                        }
                        val endingBalance = mapLastTransac[accNum]?.bal ?: 0.0

                        writer.write("${current.accountName}," + "${current.get_Age(endDate)}," + "%.2f".format(endingBalance) + "," + "%.2f".format(avgBal) + "," + tier)
                        writer.newLine()
                    }
                }
        }

        return report1.name
    }

//helper for report 2
    fun calculateExpectedInterest(initialBalance: Double): Double{
        var balance = initialBalance
        var totalInterest = 0.0

        for(day in 1..30){
            val dailyInterest = balance * (0.05 / 365)
            totalInterest += dailyInterest
            balance += dailyInterest
        }

        return totalInterest
    }

    
    fun interest_report(acc: MutableList<account>): String {
        //insert variables
        val mapAccInterest = mutableMapOf<Int, Double>()

        val file = transactionList?: throw IllegalStateException("Transaction file has not been loaded.")

        file.useLines { lines ->
            lines.drop(1)
            .forEach { line ->
                val col = line.split(",")

                if (col.size == 8) {
                    val currNum = col[1].trim().toInt()
                    val currBal = col[7].trim().toDouble()

                    mapAccInterest[currNum] = currBal
                }
            }
        }

        val qualifiedAccounts = mapAccInterest
            .filter { it.value >= 10000.0 }
            .mapValues { (num, balance) ->
                calculateExpectedInterest(balance)
            }

        totalInterest = qualifiedAccounts.values.sum()

        val report2 = File("report2_interest_expense_projection.csv")

        report2.bufferedWriter().use { writer ->

            writer.write("AccountName,CurrentBalance,ExpectedInterest")
            writer.newLine()

            qualifiedAccounts.entries
                .sortedByDescending { it.value }
                .forEach { (accNum, interest) ->
                    val current = acc.find {
                        it.accountNumber == accNum
                    }
                    if (current != null) {
                        writer.write("${current.accountName}," +"%.2f".format(current.currentBalance) + "," + "%.2f".format(interest))
                        writer.newLine()
                    }
                }
        }

        return report2.name
    }

    fun calculateTurnoverVelocity(withdraw: Double, deposit: Double): Double {
        if (deposit == 0.0) {                           //just in the case no ddespotists weremde
            return if (withdraw > 0.0) 100.0 else 0.0
        }

        return (withdraw / deposit) * 100
    }

    fun sus_report(acc: MutableList<account>): String{

        //HELPER CLASS FOR ALL THE FUNCTIONS
        data class accountBehavior(var name: String, var date: LocalDate,
                                   var withdrawAmt: Double, var numOfDeposits: Int,
                                   var depositAmt: Double, var turnVelo: Double,
                                   var riskFlag: Int, var accNum: Int)

        var clients = mutableListOf<accountBehavior>()

        transactionList?.useLines{ line ->
            line.drop(1)    //drop the first line, which is the header
            .map {it.split(",")}
            .forEach{col ->
                var current = accountBehavior(
                                col[0].trim(),
                                LocalDate.parse(col[2].trim(),DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                0.0, 0, 0.0, 0.0, 0,
                                col[1].trim().toInt())

                //check for this account in the list of account behaviors
                var i = clients.find {it.accNum == current.accNum
                                     && it.date.isEqual(current.date)}
                
                if(i == null){ //meaning it is not there yet
                    i = current
                    if(col[6].trim().equals("withdrawal")){
                        i.withdrawAmt += col[5].trim().toDouble()
                    }else if(col[6].trim().equals("deposit")){
                        i.depositAmt += col[5].trim().toDouble()
                        i.numOfDeposits++
                    }
                    clients.add(current)
                }else{ //meaning the account has made a transaction today
                    //look if its a deposit or a withdrawal
                    if(col[6].trim().equals("withdrawal")){
                        i.withdrawAmt += col[5].trim().toDouble()
                    }else if(col[6].trim().equals("deposit")){
                        i.depositAmt += col[5].trim().toDouble()
                        i.numOfDeposits++
                    }
                }
            }
        }

        //processing the behavior:calculating the turnover velocity and assessing the client behavior
        for(i in clients){
            i.turnVelo = calculateTurnoverVelocity(i.withdrawAmt, i.depositAmt)

            if(i.turnVelo >= 90.0 || i.withdrawAmt >= 500000.00){
                i.riskFlag = 1 //this is a suspisciouys flag, it is int for ease for ease of writability
            }
        }

        //after the entire list is filled up we are now able to sort
        clients.sortBy {it.date}
        clients.sortByDescending {it.riskFlag}

        //FILE WRITING PORTION
        val report3 = File("report3_suspicious_client_behavior.csv")

        report3.bufferedWriter().use{ writer ->
            writer.write("AccountName,Date,TotalWithdrawalAmounts,NumberOfDeposits,TotalDepositAmounts,TurnoverVelocity,RiskFlagLevel")
            writer.newLine()

            for(i in clients){
                if(i.riskFlag == 1){
                    writer.write("${i.name},${i.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))},${"%.2f".format(i.withdrawAmt)},${i.numOfDeposits},${"%.2f".format(i.depositAmt)},${"%.2f".format(i.turnVelo)},Suspicious")
                    writer.newLine()
                }else if(i.riskFlag == 0){
                    writer.write("${i.name},${i.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))},${"%.2f".format(i.withdrawAmt)},${i.numOfDeposits},${"%.2f".format(i.depositAmt)},${"%.2f".format(i.turnVelo)},Normal")
                    writer.newLine()
                }
            }
        }

        return "report3_suspicious_client_behavior.csv"
    }

    fun interim_report(acc: MutableList<account>): String{
        val lastMonth: YearMonth = YearMonth.from(LocalDate.now()).minusMonths(1)    //gets the month before this month, this is where the calculations are based off
        val startDate: LocalDate = lastMonth.atDay(1)
        val endDate: LocalDate = lastMonth.atEndOfMonth()
        val daysInMonth: Int = lastMonth.lengthOfMonth()

        data class interim(var name:String, var num: Int, var startBal: Double, var depCount: Int, var totalDep: Double, var withCount: Int, var totalWith: Double, var endBal: Double)
        var accList = mutableListOf<interim>()

        transactionList?.useLines{ line ->
            line.drop(1)    //drop the first line, which is the header
            .map {it.split(",")}
            .forEach{ col -> 

                //filter first so its easier
                val currDate = LocalDate.parse(col[2].trim(),DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                //only processes if the date really is from last month
                if(YearMonth.from(currDate).equals(lastMonth)){
                    val currAcc = interim(col[0].trim(), col[1].trim().toInt(), 0.0, 0, 0.0, 0, 0.0 ,0.0)
                    var i = accList.find {it.num == currAcc.num}

                    if(i == null){
                        //that means the account number isnt there yet, so we must update the other values so that it will have the valeues ... basta
                        i = currAcc
                        i.startBal = col[7].trim().toDouble()
                        if(col[6].trim().equals("deposit")){
                            i.depCount = 1
                            i.totalDep = col[5].trim().toDouble()
                        }else if(col[6].trim().equals("withdrawal")){
                            i.withCount = 1
                            i.totalWith = col[5].trim().toDouble()
                        }

                        accList.add(currAcc)
                    }else{
                        if(col[6].trim().equals("deposit")){
                            i.depCount++
                            i.totalDep += col[5].trim().toDouble()
                        }else if(col[6].trim().equals("withdrawal")){
                            i.withCount++
                            i.totalWith+= col[5].trim().toDouble()
                        }

                        i.endBal = col[7].trim().toDouble()
                    }
                }
            }
        }

        accList.sortBy{it.num}

        //FILL WRITER POTIONG
        val report4 = File("report4_interim_report_for_last_month.csv")

        report4.bufferedWriter().use() { writer ->
            writer.write("AccountName,AccountNumber,StartingBalance,NumberOfDeposits,TotalDepositAmount,NumberOfWithdrawal,TotalWithdrawalAmount,EndingBalance")
            writer.newLine()

            for(i in accList){
                    writer.write("${i.name},${i.num},${"%.2f".format(i.startBal)},${i.depCount},${"%.2f".format(i.totalDep)},${i.withCount},${"%.2f".format(i.totalWith)},${"%.2f".format(i.endBal)}")
                    writer.newLine()
            }
        }

        return "report4_interim_report_for_last_month.csv"
    }

data class summary(var totalAccounts: Int, var totalDeposits: Double, var totalWithdrawals: Double,var totalBalance: Double, var totalInterestExpense: Double) {
    fun toJsonString(): String {
        return """{" total_accounts ": $totalAccounts, "total_deposits": $totalDeposits, "total_withdrawals": $totalWithdrawals, "total_balance": $totalBalance, "total_interest_expense": $totalInterestExpense}"""
    }
}

    
    fun summary_json(acc: MutableList<account>): String {

        val jason = summary(acc.size, 0.0, 0.0, 0.0, 0.0)

        // stroe the latest balance of each account
        val mapAccBalance = mutableMapOf<Int, Double>()

        try {
            val file = transactionList?: throw IllegalStateException("Transaction file has not been loaded.")

            file.useLines { line ->
                line.drop(1)
                    .forEach { transaction ->
                        val col = transaction.split(",")
                        if (col.size == 8) {

                            val accNum = col[1].trim().toInt()
                            val amount = col[5].trim().toDouble()
                            val balance = col[7].trim().toDouble()

                            if (col[6].trim().equals("deposit")) {
                                jason.totalDeposits += amount

                            } else if (col[6].trim().equals("withdrawal")) {
                                jason.totalWithdrawals += amount
                            }

                            //update the latest account balance
                            mapAccBalance[accNum] = balance
                        }
                    }
            }

            // sum the latest balance of every account
            jason.totalBalance = mapAccBalance.values.sum()

            //from report 2
            jason.totalInterestExpense = totalInterest

            // FILE WRITING PORTION
            val outFile = File("summary.json")

            outFile.writeText(
                """
                {
                "total_accounts": ${jason.totalAccounts},
                "total_deposits": ${"%.2f".format(jason.totalDeposits)},
                "total_withdrawals": ${"%.2f".format(jason.totalWithdrawals)},
                "total_balance": ${"%.2f".format(jason.totalBalance)},
                "total_interest_expense": ${"%.2f".format(jason.totalInterestExpense)}
                }
                """.trimIndent()
            )
            return outFile.name
        } catch (e: Exception) {
            println("Error writing json: ${e.message}")
            return ""
        }
    }


    
    fun generate_all_reports(acc: MutableList<account>) {

        println("Generating reports...")

        try {

            val r1 = File(privilege_report(acc))
            val r2 = File(interest_report(acc))
            val r3 = File(sus_report(acc))
            val r4 = File(interim_report(acc))
            val summary = File(summary_json(acc))

            println("Outputs saved to individual files...\n")

            println("Report 1: Banking Privilege Tiers")
            preview_report(r1)

            println("Report 2: Interest Expense Projection")
            preview_report(r2)

            println("Report 3: Suspicious Client Behavior")
            preview_report(r3)

            println("Report 4: Interim Report From The Last Month")
            preview_report(r4)

            println("Summary State (summary.json):")
            println(summary.readText().trim())

        } catch (e: Exception) {
            println("Error generating the reports: ${e.message}")
        }
    }

}