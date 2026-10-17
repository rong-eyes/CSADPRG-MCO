#include "menuFunctions.h"

//HELPER FUNC: keeps asking until the user types a valid account name
static void promptName(StringName name){
    int valid;
 
    do{
        printf("Account Name: ");
        valid = readName(name);

        //only valid if not empty or doesnt go over 100 char
        if(!valid){
            printf("Invalid name (empty or over %d characters). Try again!\n", MAX_NAME_LEN);
        }
    }while(!valid);
}
 
//HELPER FUNC: keeps asking until the user types an integer between min and max (inclusive)
static int promptChoice(const char *prompt, int min, int max){
    int choice;
    int valid;
 
    do{
        printf("%s", prompt);
        valid = readInt(&choice) && choice >= min && choice <= max;     //only numeric inputs and within bounds are valid
 
        if(!valid){
            printf("Invalid input. Try again!\n");
        }
    }while(!valid);
 
    return choice;
}
 
//HELPER FUNC: keeps asking until the user types a number greater than 0
static double promptPositive(const char *prompt, const char *errorMsg){
    double value;
    int valid;
 
    do{
        printf("%s", prompt);
        valid = readDouble(&value) && value > 0;           //only positive numeric inputs are valid
 
        if(!valid){
            printf("%s", errorMsg);
        }
    }while(!valid);
 
    return value;
}

static int backToMainMenu(){
    char choice;
    int ret;

    do{
        printf("Back to the Main Menu (Y/N): ");
        
        if(!readChar(&choice)){          //only single char values are accepted
            choice = '.';               //force the invalid branch 
        }

        if(choice == 'Y' || choice == 'y'){
            ret = 0;                                        //0 = Y (leave)
        }else if(choice == 'N' || choice == 'n'){
            ret = 1;                                        //1 = N (stay)
        }else{
            printf("Invalid input. Try again!\n");
            ret = -1;                                       //loop again
        }
    
    }while(ret == -1);

    return ret;
}

//Registers a non-existing account (different name) & account currency to the account list
void registerAccountMenu(AccountList *A){
    StringCurrency codes[] = {"PHP", "USD", "JPY", "GBP", "EUR", "CNY"};        //array of the currency codes
    int again;                              //takes the return of backToMainMenu

    do{
        StringName accName;
        int currency;

        printf("Register Account Name\n");
        promptName(accName);                        //keeps prompting till valid name

        printf("Account Currency:\n");
        printCurrencyOptions();

        currency = promptChoice("\nChoice: ", 1, 6);            //keeps prompting till numeric input from 1-6 is inputted

        //newAccount returns 1 if adding of acc is successful else it already exists in the list
        if(newAccount(A, accName, codes[currency - 1])){        //-1 since array are indexed 0
            printf("\nAccount registered!\n");                  
        }else{
            printf("\nAccount already exists.\n");
        }

        printf("\n");
        again = backToMainMenu();                   //ask if they wanna go back to the main menu
    }while(again);
}

//Deposit a postive numeric value to an existing acc
void depositAmountMenu(AccountList *A){
    int again;                              //takes the return of backToMainMenu

    do{
        StringName accName;
        AccountNode *account;               //pointer to the account struct that shares the same name as input

        printf("\nDeposit Amount\n");
        promptName(accName);                        //keeps prompting till valid name
        account = searchAccount(A, accName);        //look for the acc

        if(account != NULL){                        //make sure it exists
            double depAmount;
            int check;

            printBalance(account->amount, account->currency);   //prints the current balance and currency of the account
            
            do{
                printf("\nDeposit Amount: ");
                if(!readDouble(&depAmount)){                //if input is non-numeric, force the inavlid branch
                    depAmount = -1;                         
                }

                check = depositAmount(account, depAmount);  //returns 1 for positive amt and -1 otherwise

                if(check == 1){
                    printf("Updated Balance: %.2lf\n", account->amount);
                
                }else{
                    printf("Invalid amount. Try again!\n");
                }
            }while(check != 1);
            

        }else{
            printf("Account does not exist.\n");
        }
        
        printf("\n"); 
        again = backToMainMenu();            //ask if they wanna go back to the main menu
    }while(again);
}

//Withdraw a positive numeric value to an existing acc provided the withdrawn amount <= account balance
void withdrawAmountMenu(AccountList *A){
    int again;                              //takes the return of backToMainMenu

    do{
        StringName accName;
        AccountNode *account;               //pointer to the account struct that shares the same name as input

        printf("\nWithdraw Amount\n");
        promptName(accName);                        //keeps prompting till valid name
        account = searchAccount(A, accName);        //look for the acc

        if(account != NULL){                         //make sure it exists
            double witAmount;

            printBalance(account->amount, account->currency);       //prints the current balance and currency of the account

            if(account->amount != 0){                       //check if the account has no money in it
                int check;
                
                //keep asking a withdraw amount if amt < 0 or amt > account balance
                do{
                    printf("\nWithdraw Amount: ");
                    if(!readDouble(&witAmount)){
                        witAmount = -1;                        
                    }

                    check = withdrawAmount(account, witAmount);     //returns 1 for positive amt. -1 for negative amt, -2 for inufficient funds

                    if(check == 1){
                        printf("Updated Balance: %.2lf\n", account->amount);
                    
                    }else if(check == -1){
                        printf("Invalid amount. Try again!\n");
                    }else{
                        printf("Insufficient funds. Try again!\n");
                    }
                }while(check != 1);

            }else{
                printf("\nCannot withdraw. Account has no funds.\n");     
            }

        }else{
            printf("Account does not exist.\n");
        }
        
        printf("\n");
        again = backToMainMenu();
    }while(again);
}

//Calculate the conversion of one currency to another as PHP being the basis of conversion (currency -> PHP)
void currencyExchangeMenu(ExchangeRate toPHP){
    int again;                              //takes the return of backToMainMenu

    do{
        int sourceCurrency;
        int exchangedCurrency;
        double amount;
        double exchangedAmount;

        printf("\nForeign Currency Exchange\n");
        printf("Source Currency Option:\n");
        printCurrencyOptions();

        sourceCurrency = promptChoice("\nSource Currency: ", 1, 6);         //keeps prompting till numeric input from 1-6 is inputted

        amount = promptPositive("Amount: ", "Invalid amount. Try again!\n\n");  //keeps prompting until a postive numeric amount is inputted

        printf("\nExchanged Currency Option:\n");
        printCurrencyOptions();
        exchangedCurrency = promptChoice("\nExchange Currency: ", 1, 6);    //keeps prompting till numeric input from 1-6 is inputted

        //convert currency returns different negative values depending on which currencies have no registered rates
        exchangedAmount = convertCurrency(toPHP, sourceCurrency, exchangedCurrency, amount);
        if(exchangedAmount == -1){
            printf("Source currency does not have a rate.\n");
        
        }else if(exchangedAmount == -2){
            printf("Exchanged currency does not have a rate.\n");
        
        }else if(exchangedAmount == -3){
            printf("Both currencies do not have a rate.\n");

        }else{
            printf("Exchange Amount: %.2lf\n", exchangedAmount);
        }
    
        printf("\n");
        again = backToMainMenu();        //ask if they wanna go back to the main menu
    }while(again);
}

void recordExchangeRateMenu(ExchangeRate toPHP){
    int again;                           //takes the return of backToMainMenu
    
    do{
        int selectCurrency;
        double rate;

        printf("\nRecord Exchange Rate\n\n");
        printRecordExchangeOptions();
        selectCurrency = promptChoice("\nSelect Foreign Currency: ", 1, 5);     //only 1-5 since we exclude PHP since its the basis; default set PHP to 1

        rate = promptPositive("Exchange Rate: ", "Invalid rate. Try again!\n\n");   //keeps prompting until a postive numeric amount is inputted 

        recordExchangeRate(toPHP, selectCurrency, rate);

        printf("\n");
        again = backToMainMenu();       //ask if they wanna go back to the main menu
    }while(again);
}

void showInterestMenu(AccountList *A){
    int again;                          //takes the return of backToMainMenu

    do{
        StringName accName;
        AccountNode *account;               //pointer to the account struct that shares the same name as input

        printf("\nShow Interest Amount\n");
        promptName(accName);                        //keeps prompting till valid name
        account = searchAccount(A, accName);        //look for the acc

        if(account != NULL){                        //make sure it exists
            int days;
            int check;

            printBalance(account->amount, account->currency);            //prints the current balance and currency of the account
            printf("Interest Rate: %.0f%%\n", INTEREST_RATE * 100);
            
            //keeps going until a positive numeric values inputted for days
            do{
                printf("\nTotal Number of Days: ");
                if(!readInt(&days)){
                    days = 0;                               
                }

                check = interestAmount(account, days);

                if(check == -1){
                    printf("Invalid days. Try again!\n");
                }

            }while(check != 1);

        }else{
            printf("Account does not exist.\n");
        }
        
        printf("\n");
        again = backToMainMenu();       //ask if they wanna go back to the main menu
    }while(again);
}