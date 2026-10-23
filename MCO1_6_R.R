#********************
#Language: R
#Last names: Balbanero
#Paradigm(s): Procedural & Object Oriented
#********************

mainmenu <- c("\nSelect Transaction\n",
              "[1] Register Account Name",
              "[2] Deposit Amount",
              "[3] Withdraw Amount",
              "[4] Currency Exchange",
              "[5] Record Exchange Rates",
              "[6] Show Interest Amount",
              "[7] Set Rates",
              "[0] Exit")

accountDetails <- c("Account Name: ",
                    "Current Balance: ",
                    "Currency: ")

bankList <- list()

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

accountFind <- function(banklist){
  
  Name <- readline("Account Name: ")
  index <- which(sapply(bankList, function(x) x$name) == Name)
  
  if(length(index) > 0){
    return(index)
  } else {
      return(NULL)
  }
}

setRates <- function(rates, rateValues){
  writeLines(rates)
  for(i in seq_along(rateValues)){
    rateValues[i] <- as.numeric(readline("Input rate value: "))
    cat("\n", rates[i], " = ", rateValues[i],"\n")
  }
  
  return(rateValues)
}

registerName <- function(){
  nameExit <- "A"
  
  while(nameExit != "Y"){
    accountName <- readline("Insert Full Name: ")
    cat("***\n","Account Name: ",
        accountName, "\n\n")
      nameExit <- exitFunc()
  }
  return(accountName)
}

deposit <- function(bankList, accountDetails){
  depositExit <- "A"
  
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
  withdrawExit <- "A"
  
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
  exchangeExit <- "A"
  
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
  recordExit <- "A"
  
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
  
interestExit <- "A"
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

option <- 9
users <- 1
rateSet <- FALSE

while(option != 0){
  
cat(writeLines(mainmenu))
option <- readline("Choice: ")
option <- as.integer(option)



if(option == 1) {
  
    cat("***\nSelected Choice =", option,"\n***\n")
    Name <- registerName()
    bankList[[users]] <- list(name = Name, balance = 1000.00, currency = "PHP")
    users <- users + 1
    
} else if(option == 2) {
  
    cat("***\nSelected Choice =", option,"\n***\n")
    bankList <- deposit(bankList, accountDetails)
    
} else if(option == 3) {
  
    cat("***\nSelected Choice =", option,"\n***\n")
    bankList <- withdraw(bankList, accountDetails)

} else if(option == 4) {
  if(rateSet == TRUE){
    cat("***\nSelected Choice =", option,"\n***\n")
    currencyExchange(rates, rateValues)
  } else
      cat("\n ! Rates not Set !\n")
  
} else if(option == 5) {
  if(rateSet == TRUE){
    cat("***\nSelected Choice =", option,"\n***\n")
    recordExchange(rates,rateValues)
  } else
      cat("\n ! Rates not Set !\n")
    
} else if(option == 6){
  
  cat("***\nSelected Choice =", option,"\n***\n")
  showInterest(bankList, accountDetails)
  
}else if (option == 7){
  cat("***\nSelected Choice =", option,"\n***\n")
  rateValues <- setRates(rates, rateValues)
  rateSet <- TRUE
}

}