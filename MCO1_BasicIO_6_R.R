#********************
#Last names: Balbanero
#Language: R
#Paradigm(s): Procedural & Object Oriented
#********************

mainmenu <- c("[1] Register Account Name",
              "[2] Deposit Amount",
              "[3] Withdraw Amount",
              "[4] Currency Exchange",
              "[5] Record Exchange Rates",
              "[6] Show Interest Amount")

accountInfo <- c("Title",
                 "Account Name = Dela Cruz, Juan",
                 "Current Balance: 1000.00",
                 "Currency: PHP")

rates <- c("[1] Philippine Peso (PHP)",
           "[2] United States Dollar (USD)",
           "[3] Japanese Yen (JPY)",
           "[4] British Pound Sterling (GBP)",
           "[5] Euro (EUR)",
           "[6] Chinese Yuan Renminni (CNY)")

rateValues <- c(1.0, 62.00, 0.40, 84.00, 72.00, 9.00)
rateValues <- as.numeric(rateValues)

registerName <- function(){
  accountName <- readline("Insert Full Name: ")
  cat("***\n","Account Name: ",
      accountName)
  
  return(accountName)
}

deposit <- function(accountInfo){
  accountInfo[1] <- "Deposit Amount"
  writeLines(accountInfo)
  
  depositAmt <- readline("\nDeposit Amount: ")
  depositAmt <- as.numeric(depositAmt)
  
  cat("***\n", accountInfo[2],
      "\n New", accountInfo[1],
      "=", depositAmt)
  
  return(depositAmt)
}

withdraw <- function(accountInfo){
  accountInfo[1] <- "Withdraw Amount"
  writeLines(accountInfo)
  
  withdrawAmt <- readline("\nWithdraw Amount: ")
  withdrawAmt <- as.numeric(withdrawAmt)
  
  cat("***\n", accountInfo[2],
      "\n", accountInfo[1],
      "=", withdrawAmt)
  
  return(withdrawAmt)
}

currencyExchange <- function(rates, rateValues){
  cat("\nForeign Currency Exchange\n")
  sourceAmt <- readline("Source Amount (PHP): ")
  sourceAmt <- as.numeric(sourceAmt)
  
  rateValues <- rateValues * sourceAmt
  rateValues <- as.numeric(rateValues)
  
  cat("\nExchanged Currency\n")
  writeLines(paste(rates, "=", as.character(rateValues)))
  
  cat("\n***\n", "Source Currency: Philippine Peso (PHP)",
      "\n Source Amount (PHP): ",
      sourceAmt,"\n")
}

recordExchange <- function(rates, rateValues){
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
  }
  
  return(rateValues[foreignCurrency])
}


cat("\nSelect Transaction\n",
writeLines(mainmenu))
option <- readline("Choice: ")
option <- as.integer(option)

if(option == 1) {
    cat("***\nSelected Choice =", option,"\n***\n")
    Name <- registerName()
    accountInfo[2] <- paste("Account Name =", Name)

} else if(option == 2) {
    cat("***\nSelected Choice =", option,"\n***\n")
    opt2Name <- registerName()
    accountInfo[2] <- paste("Account Name =", opt2Name)
    balance <- deposit(accountInfo)
    
} else if(option == 3) {
    cat("***\nSelected Choice =", option,"\n***\n")
    opt3Name <- registerName()
    accountInfo[2] <- paste("Account Name =", opt3Name)
    balance <- withdraw(accountInfo)

} else if(option == 4) {
    cat("***\nSelected Choice =", option,"\n***\n")
    currencyExchange(rates, rateValues)
  
} else if(option == 5) {
    cat("***\nSelected Choice =", option,"\n***\n")
    recordExchange(rates,rateValues)
    
}  
