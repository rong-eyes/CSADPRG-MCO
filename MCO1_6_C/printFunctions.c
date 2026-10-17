#include "printFunctions.h"

void printMainMenu(){
    printf("Select Transaction:\n");
    printf("[1] Register Account Name\n");
    printf("[2] Deposit Amount\n");
    printf("[3] Withdraw Amount\n");
    printf("[4] Currency Exchange\n");
    printf("[5] Record Exchange Rates\n");
    printf("[6] Show Interest Amount\n");
    printf("[0] Exit Program\n");
    printf("\nChoice: ");
}

void printRecordExchangeOptions(){
    printf("[1] United States Dollar (USD)\n");
    printf("[2] Japanese Yen (JPY)\n");
    printf("[3] British Pound Sterling (GBP)\n");
    printf("[4] Euro (EUR)\n");
    printf("[5] Chinese Yuan Renminni (CNY)\n");
}

void printCurrencyOptions(){
    printf("[1] Philippine Peso (PHP)\n");
    printf("[2] United States Dollar (USD)\n");
    printf("[3] Japanese Yen (JPY)\n");
    printf("[4] British Pound Sterling (GBP)\n");
    printf("[5] Euro (EUR)\n");
    printf("[6] Chinese Yuan Renminni (CNY)\n");
}

void printBalance(double balance, StringCurrency currency){
    printf("Current Balance: %.2lf\n", balance);
    printf("Currency: %s\n", currency);
}