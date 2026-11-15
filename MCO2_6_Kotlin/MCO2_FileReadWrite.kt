/*
********************
Last names: Balbanero, Sandoval, Racimo, Yu
Language: Kotlin
Paradigm(s): Object-Oriented
********************
*/

import java.io.File
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

//helper data classes
data class lastTransac(var transacdate: LocalDate, var bal: Double)

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
    fun preview_report(){
        //displays the first two lines
    }

    fun privilege_report(acc: MutableList<account>){
        val tiers = arrayOf("Silver","Gold","Platinum")
        val lastMonth: YearMonth = YearMonth.from(LocalDate.now()).minusMonths(1)    //gets the month before this month, this is where the calculations are based off
        val startDate: LocalDate = lastMonth.atDay(1)
        val endDate: LocalDate = lastMonth.atEndOfMonth()
        val daysInMonth: Int = lastMonth.lengthOfMonth()
        val mapLastTransac = mutableMapOf<Int, lastTransac>()
        val mapEodSum = mutableMapOf<String, Double>()

        transactionList.useLines { line->
            line.drop(1)    //drop the first line, which is the header
            .map {it.split(",")}
            .forEach{col ->
                val currNum = col[1].trim()
                val currDate = LocalDate.parse(col[2].trim())
                val currBal = col[7].trim().toDouble()

                if(!currDate.isAfter(endDate)){
                    val previousTransac = mapLastTransac[currNum]
                    if(previousTransac == null){
                        mapLastTransac[currNum] = lastTransac(currDate, currBal) //if no last transac add 
                    } else if (previousTransac.transacdate.isEqual(currDate)){
                        previousTransac.bal = currBal                            //if the previous transac was on the same day, update
                    }else if(previousTransac.transacdate.isBefore(currDate)){
                        val fill = maxOf(previousTransac.transacDate, currDate)  //for the days with no transacetion, it will just multiply the EOD

                        if(fill.isBefore(currDate)){
                            val daysHeld = ChronoUnit.DAYS.between(held, currDate)
                            mapEodSum[currNum] = (mapEodSum.getValue(currNum) ?: 0.0) + (previousTransac.bal * daysHeld)
                        }

                        previousTransac.transacDate = currDate
                        previousTransac.bal = currBal
                    }
                }
            }
        }

        //fill remaining days
        for((accNum,last) in mapLastTransac){
            val fill = maxOf(last.transacdate, startDate)
            if(!fillStart.isAfter(endDate)){
                val remaining = ChronoUnits.DAYS.between(fill, endDate) + 1
                mapEodSum[accNum] = (mapEodSUm.getValue(accNum) ?: 0.0) + (last.bal * remaining)
            }
        }

        //calculate the avgdailybal for each of the values
        val mapAvgDailyBal = mapEodSum.mapValues {it.value / daysInMonth}

        //FILE WRITING PORTION
        val report1 = File("report1_banking_privilege_tiers.csv")

        report1.bufferedWriter().use{ writer ->
            writer.write("AccountName,AccountAge,CurrentBalance,AverageDailyBalance,PrivilegeTier")
            writer.newLine()
            
            mapAvgDailyBal.forEach{(accNum, avgBal) ->
                current = acc.find {it.accNumber == accNum}

                //REFORMAT THE ONES THAT DISPLAY NUMBERS TO ONLY BE 2 DECIMALS, DONT FORGET TO SORT BY AVGDAILYBALANCE IN DESENDING ORDER
                if(avgBal >= 100000.00){
                    if(avgBal < 500000.00){
                        writer.write("${current.accountName},${current.getAge()},${current.currentBalance},${mapAvgDailyBal[accNum]},$tiers[0]")
                        writer.newLine()
                    }else if(avgBal< 1000000.00){
                        writer.write("${current.accountName},${current.getAge()},${current.currentBalance},${mapAvgDailyBal[accNum]},$tiers[1]")
                        writer.newLine()
                    }else{
                        writer.write("${current.accountName},${current.getAge()},${current.currentBalance},${mapAvgDailyBal[accNum]},$tiers[2]")
                        writer.newLine()
                    }
                }
            }
        }
    }

    fun interest_report(acc: MutableList<account>){
    
    }
}
