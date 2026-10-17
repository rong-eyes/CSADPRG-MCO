/*
Last Names: Balbanero, Sandoval, Racimo, Yu
Language: C
Paradigm: Procedural
*/

//gcc -Wall -std=c99 main.c -o run.exe -lm
#include <stdio.h>

#include "exchange.c"
#include "printFunctions.c"
#include "accounts.c"
#include "transactions.c"
#include "menuFunctions.c"
#include "inputCheck.c"

int main(){
    ExchangeRate toPHP = {1.00, 0.00, 0.00, 0.00, 0.00, 0.00};        //we assume PHP as the base currency so we just intialize it to 1 and the rest 0
    AccountList list;                                                 //intialize the list
    int choice;                                                  

    CreateAccountList(&list);                                         //create the list (intializes the pointers to null)
    
    do{
        printMainMenu();
        if(!readInt(&choice)){
            choice = 100;                     //force the invalid branch if input is non-numeric
        }

        if(choice == 1){
            registerAccountMenu(&list);
            printf("\n");

        }else if(choice == 2){
            depositAmountMenu(&list);
            printf("\n"); 

        }else if(choice == 3){
            withdrawAmountMenu(&list);
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
