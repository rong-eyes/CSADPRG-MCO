/*
Last Names: Balbanero, Sandoval, Racimo, Yu
Language: C
Paradigm: Procedural
*/

//gcc -Wall -std=c99 MCO1_BasicIO_6_C.c -o run.exe
#include <stdio.h>

#define MAX_NAME_LEN 100    //assume that full names dont go beyond 100 char

typedef char StringName[MAX_NAME_LEN + 1];  //string data type for name inputs; +1 for /0
typedef char StringCurrency[4];             //string data type for 3 letter currency inputs; +1 for /0

void printMainMenu(){
    printf("Select Transaction:\n");
    printf("[1] Register Account Name\n");
    printf("[2] Deposit Amount\n");
    printf("[3] Withdraw Amount\n");
    printf("[4] Currency Exchange\n");
    printf("[5] Record Exchange Rates\n");
    printf("[6] Show Interest Amount\n");
    printf("\nChoice: ");
}

void printRegisterAccount(){
    printf("Register Account Name\n");
    printf("Account Name: ");
}

void printRecordExchange(){
    printf("Record Exchange Rate\n");
    printf("\n");
    printf("[1] Philippine Peso (PHP)\n");
    printf("[2] United States Dollar (USD)\n");
    printf("[3] Japanese Yen (JPY)\n");
    printf("[4] British Pound Sterling (GBP)\n");
    printf("[5] Euro (EUR)\n");
    printf("[6] Chinese Yuan Renminni (CNY)\n");
}

void printExchangeCurrency(float src, float PHP, float USD, float JPY, float GBP, float EUR, float CNY){
    printf("Exchanged Currency\n");
    printf("[1] Philippine Peso (PHP) = %.2f\n", src * PHP);
    printf("[2] United States Dollar (USD) = %.2f\n", src * USD);
    printf("[3] Japanese Yen (JPY) = %.2f\n", src * JPY);
    printf("[4] British Pound Sterling (GBP) = %.2f\n", src * GBP);
    printf("[5] Euro (EUR) = %.2f\n", src * EUR);
    printf("[6] Chinese Yuan Renminni (CNY) = %.2f\n", src * CNY);
}

void printBalance(float balance, StringCurrency currency){
    printf("Current Balance: %.2f\n", balance);
    printf("Currency: %s\n", currency);
}

int main(){
    //Main Menu
    int choice;
    printMainMenu();
    scanf("%d", &choice);

    printf("\n***\n");
    printf("Choice = %d\n", choice);

    printf("\n"); //Separator

    //Register Account Name
    StringName accName;
    printRegisterAccount();
    scanf(" %100[^\n]", accName);   //format specifier to include spaces in the string up to 100 char

    printf("\n***\n");
    printf("Account Name = %s\n", accName);

    printf("\n"); //Separator

    //Deposit Ammount
    float depAmount;
    printf("Deposit Amount\n");
    printf("Account Name: ");
    scanf(" %100[^\n]", accName);   //format specifier to include spaces in the string up to 100 char
    printBalance(1000.00, "PHP");      //default value of 1000, PHP 
    

    printf("\nDeposit Amount: ");
    scanf("%f", &depAmount);

    printf("\n***\n");
    printf("Account Name = %s\n", accName);
    printf("Deposit Amount = %.2f\n", depAmount);

    printf("\n"); //Separator

    //Withdraw Ammount
    float widAmount;
    printf("Withdraw Amount\n");
    printf("Account Name: ");
    scanf(" %100[^\n]", accName);   //format specifier to include spaces in the string up to 100 char
    printBalance(1000.00, "PHP");      //default value of 1000, PHP 

    printf("\nWithdraw Amount: ");
    scanf("%f", &widAmount);

    printf("\n***\n");
    printf("Account Name = %s\n", accName);
    printf("Withdraw Amount = %.2f\n", widAmount);

    printf("\n"); //Separator

    //Record Exchange Rate
    int selectCurrency;
    float rate;
    printRecordExchange();
    printf("\nSelect Foreign Currency: ");
    scanf("%d", &selectCurrency);
    printf("Exchange Rate: ");
    scanf("%f", &rate);

    printf("\n***\n");
    printf("Select Foreign Currency = %d\n", selectCurrency);
    printf("Exchange Rate = %.2f\n", rate);

    printf("\n"); //Separator

    //Currency Exchange
    float srcAmount;
    printf("Foreign Currency Exchange\n");
    printf("Source Amount (PHP): ");
    scanf("%f", &srcAmount);

    printf("\n");

    printExchangeCurrency(srcAmount, 1, 62.00, 0.40, 84.00, 72.00, 9.00);   //default exchange values of PHP(1), USD(62), JPY(0.40), GBP(84.00), EUR(72.00), CNY(9.00)

    printf("\n***\n");
    printf("Select Foreign Currency = Philippine Peso (PHP)\n");
    printf("Source Amount (PHP) = %.2f\n", srcAmount);
}
