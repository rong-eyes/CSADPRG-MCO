/*
Last Names: Balbanero, Sandoval, Racimo, Yu
Language: C
Paradigm: Procedural
*/

//gcc -Wall -std=c99 main.c -o run.exe -lm
#include <stdio.h>

#include "exchange.c"
#include "printFunctions.c"
#include "csvData.c"
#include "transactions.c"
#include "menuFunctions.c"
#include "inputCheck.c"

int main(){
    ExchangeRate toPHP = {1.00, 0.00, 0.00, 0.00, 0.00, 0.00};        //we assume PHP as the base currency so we just intialize it to 1 and the rest 0
    TransactionList list = {NULL, 0, 0};                              //intialize the list to point to null, 0 count, 0 capacity
    int choice;                                                  
    
    do{
        printMainMenu();
        if(!readInt(&choice)){
            choice = 100;                     //force the invalid branch if input is non-numeric
        }

        if(choice == 1){
            registerAccountMenu(&list, TRANSACTIONS_CSV);
            printf("\n");

        }else if(choice == 2){
            depositAmountMenu(&list, TRANSACTIONS_CSV);
            printf("\n"); 

        }else if(choice == 3){
            withdrawAmountMenu(&list, TRANSACTIONS_CSV);
            printf("\n");

        }else if(choice == 4){
            currencyExchangeMenu(toPHP);
            printf("\n");

        }else if(choice == 5){
            recordExchangeRateMenu(toPHP);
            printf("\n");

        }else if(choice == 6){
            showInterestMenu(&list);
            printf("\n");
        
        }else{
            printf("Invalid option. Try again!\n\n");
        }

    }while(choice != 0);                    //program stop when 0 is inputted in the main menu
    DestroyAccountList(&list);              //deallocate everything in the list
    return 0;
}
