#********************
#Language: R
#Last names:  Balbanero, Sandoval, Racimo, Yu
#Paradigm(s): Procedural & Object Oriented
#********************

library(dplyr)
library(lubridate)
library(jsonlite)
library(ggplot2)



mainmenu <- c("\nSelect Transaction\n",
              "[1] Register Account Name",
              "[2] Deposit Amount",
              "[3] Withdraw Amount",
              "[4] Currency Exchange",
              "[5] Record Exchange Rates",
              "[6] Show Interest Amount",
              "[7] Generate Reports",
              "[0] Exit")

accountDetails <- c("Account Name: ",
                    "Account Number: ",
                    "Current Balance: ")

newAccount <- data.frame()

rates <- c("[1] Philippine Peso (PHP)",
           "[2] United States Dollar (USD)",
           "[3] Japanese Yen (JPY)",
           "[4] British Pound Sterling (GBP)",
           "[5] Euro (EUR)",
           "[6] Chinese Yuan Renminni (CNY)")

rateValues <- c(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
rateValues <- as.numeric(rateValues)

exitFunc <- function(){
  exit <- "A"
  while(exit != "Y" & exit != "N"){
    exit <- readline("Back to the Main Menu (Y/N): ")
    
    if(exit != "Y" & exit != "N")
      cat("\n ! Error Wrong Input !\n")
  }
  
  return(exit)
}

accountFind <- function(bank_df){
  
}

cleanFile <- function(file){
  
  df <- file %>%
    filter(currency == "PHP")
  
  df$date <- parse_date_time(df$date, order = c("d/m/Y"))
  df$time <- hms(df$time)
  
  return(df)
  
}

setRates <- function(rates, rateValues){
  writeLines(rates)
  for(i in seq_along(rateValues)){
    rateValues[i] <- as.numeric(readline("Input rate value: "))
    cat("\n", rates[i], " = ", rateValues[i],"\n")
  }
  
  return(rateValues)
}

registerName <- function(new_Account){
  nameExit <- "N"
  
  while(nameExit != "Y"){
    accountName <- readline("Account Name: ")
    accountDeposit <- readline("Insert Full Name: ")
    
    new_Account = data.frame(account_name = accountName, )
    
    cat("***\n","Account created successfully! \n\n",)
    nameExit <- exitFunc()
  }
  return(accountName)
}

deposit <- function(bankList, accountDetails){
  depositExit <- "N"
  
  while(depositExit != "Y"){
    cat("\nDeposit Amount")
    user <- accountFind(bankList)
    
    if(is.null(user)){
      cat("\n ! Account not Found !\n")
    } else {
    writeLines(paste(accountDetails, bankList[[user]]))

    depositAmt <- readline("\nDeposit Amount: ")
    depositAmt <- as.numeric(depositAmt)
    
    if(depositAmt < 0){
      cat("\n ! Invalid Deposit amount !\n")
    } else {
    cat("\n***\n")
    bankList[[user]]$balance <- bankList[[user]]$balance + depositAmt
    writeLines(paste(accountDetails, bankList[[user]]))
    }
  }
    
    depositExit <- exitFunc()
  }
  
  return(bankList)
}

withdraw <- function(bankList, accountDetails){
  withdrawExit <- "N"
  
  while(withdrawExit != "Y"){
    cat("\nWithdraw Amount")
    user <- accountFind(bankList)
    
    if(is.null(user)){
      cat("\n ! Account not Found !\n")
    } else {
    writeLines(paste(accountDetails, bankList[[user]]))
  
    withdrawAmt <- readline("\nWithdraw Amount: ")
    withdrawAmt <- as.numeric(withdrawAmt)
    
    if(withdrawAmt < 0){
      cat("\n ! Invalid Withdrawal amount !\n")
    } else {
      cat("\n***\n")
      bankList[[user]]$balance <- bankList[[user]]$balance - withdrawAmt
      writeLines(paste(accountDetails, bankList[[user]]))
    }
    }
    
    withdrawExit <- exitFunc()
  }
  
  return(bankList)
}

currencyExchange <- function(rates, rateValues){
  exchangeExit <- "N"
  
  while(exchangeExit != "Y"){
    cat("\nForeign Currency Exchange\n")
    writeLines(rates)
    sourceCurrency <- as.integer(readline("Source Currency: "))
    sourceAmt <- as.numeric(readline("Source Amount: "))
    
    if(sourceCurrency < 7 & sourceCurrency > 0){
      if(sourceCurrency != 1)
        toPeso <- sourceAmt / as.numeric(rateValues[sourceCurrency])
      else
        toPeso <- sourceAmt
    
      cat("\n\nExchange Currency Option\n")
      writeLines(rates)
      exchangeCurrency <- as.integer(readline("Source Currency: "))
      exchangeAmount <- toPeso * as.numeric(rateValues[exchangeCurrency])
      
      cat("Exchange Amount: ", round(exchangeAmount, 2))
      
    } else
        cat("\n ! Currency not Found !\n")
    
    exchangeExit <- exitFunc()
  }
}

recordExchange <- function(rates, rateValues){
  recordExit <- "N"
  
  while(recordExit != "Y"){
    cat("Record Exchange Rates\n")
    writeLines(rates)
  
    foreignCurrency <- readline("\nSelect Foreign Currency: ")
    foreignCurrency <- as.integer(foreignCurrency)
  
    if(foreignCurrency < 7 && foreignCurrency > 1){
      cat("Exchange Rate: ",
          rateValues[foreignCurrency], "\n")
  
      cat("\n***\n", "Select Foreign Currency: ",
          foreignCurrency, "\n Exchange Rate: ",
          rateValues[foreignCurrency],"\n")
    } else
        cat("\n ! Currency not Found !\n")
    
    recordExit <- exitFunc()
  }
  
  return(rateValues[foreignCurrency])
}

showInterest <- function(bankList, accountDetails){
  
interestExit <- "N"
InterestRate <- 0.05

  while(interestExit != "Y"){
    cat("\nShowinterest Amount")
    user <- accountFind(bankList)
    
    if(is.null(user)){
      cat("\n ! Account not Found !\n")
    } else {
      
      writeLines(paste(accountDetails, bankList[[user]]))
      cat("Interest Rate: 5%\n\n")
      
      days <- readline("Total Number of Days: ")
      DailyInterest <- numeric(days)
      Balance <- numeric(days)
      currentBalance <- bankList[[user]]$balance
      
      for (i in 1:days){
        DailyInterest[i] <- currentBalance * (InterestRate / 365)
        currentBalance <- currentBalance + DailyInterest[i]
        Balance[i] <- currentBalance
      }
      
      Interest <- round(DailyInterest, 2)
      Balance <- round(Balance,2)
      
      accountiInterest <- data.frame (
        Interest,
        Balance
      )
      
      print(accountiInterest)
    }
    
    interestExit <- exitFunc()
  }
}

report1 <- function(bank_df){
  
  modify_df <- bank_df %>%
    group_by(account_number) %>%
    arrange(date, time, .by_group = FALSE) %>%
    mutate(AccountAge = as.integer(difftime(Sys.Date(),head(date, n=1),"day"))) %>%
    ungroup()
  
  report_1 <- modify_df %>%
    
    filter(!is.na(date)) %>%
    filter(date >= floor_date(Sys.Date() - months(1), "month"),
           date < floor_date(Sys.Date(), "month")) %>%
    
    group_by(account_number, date) %>%
    arrange(time, .by_group = TRUE) %>%
    slice_tail(n = 1) %>%
    ungroup() %>%
    
    group_by(account_number) %>%
    mutate(AverageDailyBalance = mean(current_balance),
           PrivelegeTier = case_when(
             AverageDailyBalance < 500000.00 ~ "Silver",
             AverageDailyBalance < 1000000.00 ~ "Gold",
             AverageDailyBalance >= 1000000.00 ~ "Platinum"
           )) %>%
    
    filter(AverageDailyBalance >= 100000) %>%
    ungroup() %>%
    arrange(desc(current_balance), .by_group = FALSE) %>%
    select(account_name, AccountAge, current_balance, AverageDailyBalance, PrivelegeTier)
  
  write.csv(report_1, "R_report1_banking_privilege_tiers.csv")
  
  return(report_1)
}

report2 <- function(bank_df){
  
  report_2 <- bank_df %>%
    filter(!is.na(date)) %>%
    
    group_by(account_number) %>%
    arrange(date, time, .by_group = TRUE) %>%
    slice_tail(n = 1) %>%
    ungroup() %>%
    
    filter(current_balance >= 10000.00) %>%
    mutate(expected_interest = (current_balance * (0.05 / 365)) * 30) %>%
    
    arrange(desc(current_balance), .by_group = FALSE) %>%
    select(account_name,current_balance,expected_interest)
  
  write.csv(report_2, "R_report2_interest_expense_projection.csv")
  
  return(report_2)
}

report3 <- function(bank_df){
  
  report_3 <- bank_df %>%
    group_by(account_name, date) %>%
    summarise(TotalWithdrawalAmount = sum(amount[type == "withdrawal"]),
              NumberOfDeposits = sum(type == "deposit"),
              TotalDepositAmount = sum(amount[type == "deposit"])) %>%
    
    mutate(TurnoverVelocity = if_else(TotalDepositAmount == 0, 0,
                                     (TotalWithdrawalAmount / TotalDepositAmount) * 100)) %>%
    
    mutate(RiskFlagLevel = case_when(
            TotalWithdrawalAmount >= 500000 ~ "Suspicious",
            TurnoverVelocity >= 90.00 ~ "Suspicious",
            .default = "Normal")) %>%
    
    ungroup() %>%
    arrange(date, .by_group = FALSE)
  
  write.csv(report_3, "R_report3_suspicious_client_behavior.csv")
  
  return(report_3)
}

report_4_deposit <- function(bank_df){
  
  modify_df <- bank_df
  modify_df$transaction_month <- month(modify_df$date, label = TRUE)
  
  report_4_Deposit <- modify_df  %>%
    filter(year(date) == year(Sys.Date())) %>%
    group_by(transaction_month) %>%
    
    summarise(total_deposits = sum(amount[type == "deposit"], na.rm = TRUE)) %>%
    
    ggplot(aes(x = transaction_month, y = total_deposits, group = 1)) +
    geom_line() +
    geom_point() +
    labs(Title = "Deposits per Month in the Current Year",
         x = "Month",
         y = "Total Deposits") +
    theme_minimal()
  
  return(report_4_Deposit)
  
}

report_4_withdraw <- function(bank_df){
  
  modify_df <- bank_df
  modify_df$transaction_month <- month(modify_df$date, label = TRUE)
  
  report_4_Withdraw <- modify_df  %>%
    filter(year(date) == year(Sys.Date())) %>%
    group_by(transaction_month) %>%
    
    summarise(total_withdraws = sum(amount[type == "withdrawal"], na.rm = TRUE)) %>%
    
    ggplot(aes(x = transaction_month, y = total_withdraws, group = 1)) +
    geom_line() +
    geom_point() +
    labs(Title = "Withdrawals per Month in the Current Year",
         x = "Month",
         y = "Total Withdrawals") +
    theme_minimal()
  
  return(report_4_Withdraw)
  
}

summaryReport <- function(bank_df, report2){
  
  summary <- bank_df %>%
    summarise(total_accounts = n_distinct(account_name),
              total_balance = sum(report2$current_balance),
              total_interest = sum(report2$expected_interest),
              total_deposits = sum(amount[type == "deposit"]),
              total_withdraws = sum(amount[type == "withdrawal"]))
  
  write_json(summary, "summary.json")
  
  return(summary)
}

printReports <- function(bank_df){
  
  readline("Generating Reports...")
  
  cat("\n\n Report 1: Suspicious Client Behavior\n")
  report1_table <- report1(bank_df)
  print(report1_table, n=2)
  cat("\n(Full table exported to R_report1_banking_privilege_tiers.csv)\n")
  
  cat("\n\n Report 2: Interest Expense Projection\n")
  report2_table <- report2(bank_df)
  print(report2_table, n=2)
  cat("\n(Full table exported to R_report2_interest_expense_projection.csv)\n")
  
  cat("\n\n Report 3: Suspicious Client Behavior\n")
  report3_table <- report3(bank_df)
  print(report3_table, n=2)
  cat("\n(Full table exported to R_report3_suspicious_client_behavior.csv)\n")
  
  cat("\n\n Report 4: Monthly Transaction Graph\n")
  report4_table1 <- report_4_deposit(bank_df)
  report4_table2 <- report_4_withdraw(bank_df)
  print(report4_table1)
  cat("\n")
  print(report4_table2)
  
  cat("\n\n Summary State (summary.csv)\n")
  summary_table <- summaryReport(bank_df, report2_table)
  print(summary_table)
  
  readline("End of Reports...")
}

generateReports <- function(bank_df){
  
  cat("\nGenerate Reports\n",
      "\n[1] Load the file",
      "\n[2] Generate Reports\n")
  option <- readline("Enter choice: ")
  
  if(option == 1){
    cat(paste0("\nProcessing dataset... (",nrow(bank_df),
      " rows loaded, ",nrow(bank_df)," filtered for PHP currency)\n"
    ))
  } else if(option == 2){
      printReports(bank_df)
  } else
    cat("\n!Invalid Option!\n")
    
}

if(file.exists("bank_transactions-1.csv")){
  bankdf <- read.csv("bank_transactions-1.csv")
  bankdf <- cleanFile(bankdf)

  option <- 9
  rateSet <- FALSE
  registered <- FALSE
  
  while(option != 0){
    
  cat(writeLines(mainmenu))
  option <- readline("Choice: ")
  option <- as.integer(option)
  
    if(option == 1) {
        cat("***\nSelected Choice =", option,"\n***\n")
        registerName(newAccount)
        registered <- TRUE
        
    } else if(option == 2) {
        cat("***\nSelected Choice =", option,"\n***\n")
        deposit(bankList, accountDetails)
        
    } else if(option == 3) {
        cat("***\nSelected Choice =", option,"\n***\n")
        withdraw(bankList, accountDetails)
  
    } else if(option == 4) {
        cat("***\nSelected Choice =", option,"\n***\n")
      
        if(rateSet == TRUE){
          currencyExchange(rates, rateValues)
      } else {
          cat("\n ! Rates not Set !\n")
          rateValues <- setRates(rates, rateValues)
          rateSet <- TRUE
      }
    } else if(option == 5) {
        cat("***\nSelected Choice =", option,"\n***\n")
      
        if(rateSet == TRUE){
          recordExchange(rates,rateValues)
      } else{
          cat("\n ! Rates not Set !\n")
          rateValues <- setRates(rates, rateValues)
          rateSet <- TRUE
      }
    } else if(option == 6){
        cat("***\nSelected Choice =", option,"\n***\n")
        showInterest(bankdf, accountDetails)
      
    } else if(option == 7){
        cat("***\nSelected Choice =", option,"\n***\n")
        generateReports(bankdf)
    }
  }
} else
  cat("\n!File Cannot be Read!\n")
