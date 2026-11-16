#include "menuFunctions.h"

//HELPER FUNC: keeps asking until the user types a valid account name
static void promptName(StringName name){
    int valid;
 
    do{
        printf("Account Name: ");
        valid = readName(name);

        //only valid if not empty or doesnt go over 100 char
        if(!valid){
            printf("Invalid name (empty, over %d characters, or contains a comma). Try again!\n", MAX_NAME_LEN);
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

//Registers a non-existing account (different name) & default PHP currency to the account list
void registerAccountMenu(TransactionList *A, const char *path){
    int again;                              //takes the return of backToMainMenu

    do{
        StringName accName;
        Transaction temp;           //temp for the searchAcc func

        printf("Register Account Name\n");
        promptName(accName);                        //keeps prompting till valid name

        if(searchAcc(A, accName, &temp) != 0){
            printf("\nAccount already exists.\n");

        }else{
            double depAmount;
            int valid;
            int saved;

            //keep asking until the amount meets the minimum
            do{
                printf("\nInitial Deposit: ");
                if(!readDouble(&depAmount)){               //non-numeric input: force the invalid branch
                    depAmount = -1;
                }

                valid = depAmount >= MIN_INITIAL_DEPOSIT;
                if(!valid){
                    printf("Invalid amount! Minimum initial deposit is %.2f. Try again!\n", MIN_INITIAL_DEPOSIT);
                }
            }while(!valid);

            strcpy(temp.accountName, accName);
            temp.accountNumber = nextAccNumber(A);
            temp.balance = 0;

            depositAmount(&temp, depAmount);

            saved = recordTransaction(A, path, temp.accountName, temp.accountNumber, DEPOSIT, temp.amount, temp.balance);
            
            if(saved == 1){
                printf("Account created successfully!\n");
            }else if(saved == -1){
                printf("Account saved, but the program could not update its records. Please restart.\n");
            }else{
                printf("Could not save the new account to the file.\n");
            }
        }

        printf("\n");
        again = backToMainMenu();                   //ask if they wanna go back to the main menu
    }while(again);
}

//Deposit a postive numeric value to an existing acc
void depositAmountMenu(TransactionList *A, const char *path){
    int again;                              //takes the return of backToMainMenu

    do{
        StringName accName;
        Transaction account;                        //local storage of transaction
        int found;

        printf("\nDeposit Amount\n");
        promptName(accName);                        //keeps prompting till valid name
        found = searchAcc(A, accName, &account);        //look for the acc

        if(found){                        //make sure it exists
            double depAmount;
            int check;
            int saved;

            printBalance(account.balance, account.currency);   //prints the current balance and currency of the account
            
            do{
                printf("\nDeposit Amount: ");
                if(!readDouble(&depAmount)){                //if input is non-numeric, force the inavlid branch
                    depAmount = -1;                         
                }

                check = depositAmount(&account, depAmount);  //returns 1 for positive amt and -1 otherwise

                if(check == 1){
                    saved = recordTransaction(A, path, account.accountName, account.accountNumber, DEPOSIT, account.amount, account.balance);

                    if(saved == 1){
                        printf("Updated Balance: %.2lf\n", account.balance);
                    }else if(saved == -1){          
                        printf("Deposit saved, but the program could not update its records. Please restart.\n");
                    }else{
                        printf("Could not save the deposit to the file. Your balance was not changed.\n");
                    }
                
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
void withdrawAmountMenu(TransactionList *A, const char *path){
    int again;                              //takes the return of backToMainMenu

    do{
        StringName accName;
        Transaction account;                        //local storage of transaction
        int found;

        printf("\nWithdraw Amount\n");
        promptName(accName);                        //keeps prompting till valid name
        found = searchAcc(A, accName, &account);        //look for the acc

        if(found){                         //make sure it exists
            double witAmount;

            printBalance(account.balance, account.currency);       //prints the current balance and currency of the account

            if(account.balance != 0){                       //check if the account has no money in it
                int check;
                int saved;
                
                //keep asking a withdraw amount if amt < 0 or amt > account balance
                do{
                    printf("\nWithdraw Amount: ");
                    if(!readDouble(&witAmount)){
                        witAmount = -1;                        
                    }

                    check = withdrawAmount(&account, witAmount);     //returns 1 for positive amt. -1 for negative amt, -2 for inufficient funds

                    if(check == 1){
                        saved = recordTransaction(A, path, account.accountName, account.accountNumber, WITHDRAWAL, account.amount, account.balance);
                        if(saved == 1){
                            printf("Updated Balance: %.2lf\n", account.balance);
                        }else if(saved == -1){          
                            printf("Withdrawal saved, but the program could not update its records. Please restart.\n");
                        }else{
                            printf("Could not save the Withdrawal to the file. Your balance was not changed.\n");
                        }
                    
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

void showInterestMenu(TransactionList *A){
    int again;                          //takes the return of backToMainMenu

    do{
        StringName accName;
        Transaction account;                        //local storage of transaction
        int found;

        printf("\nShow Interest Amount\n");
        promptName(accName);                        //keeps prompting till valid name
        found = searchAcc(A, accName, &account);

        if(found){                        //make sure it exists
            int days;
            int check;

            printBalance(account.balance, account.currency);            //prints the current balance and currency of the account
            printf("Interest Rate: %.0f%%\n", INTEREST_RATE * 100);
            
            //keeps going until a positive numeric values inputted for days
            do{
                printf("\nTotal Number of Days: ");
                if(!readInt(&days)){
                    days = 0;                               
                }

                check = interestAmount(&account, days);

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

void generateReportsMenu(TransactionList *A,  const char *path){
    int again;                          //takes the return of backToMainMenu

    do{
        int choice, totalRows, badRows;

        printf("\nGenerate Reports\n\n");
        printReportsOption();

        choice = promptChoice("\nEnter choice: ", 1, 2);         //keeps prompting till numeric input from 1 or 2 is inputted

        if(choice == 1){
            char loaded[NUM_LEN], kept[NUM_LEN], bad[NUM_LEN];
            int status = loadTransactions(path, A, &totalRows, &badRows);

            if(status == LOAD_OK){
                formatCount(totalRows, loaded);
                formatCount(A->count, kept);
                formatCount(badRows, bad);
                printf("Processing dataset... (%s rows loaded, %s filtered for PHP currency, %s invalid rows skipped)\n", loaded, kept, bad);

            }else if(status == LOAD_NO_FILE){
                printf("Could not open %s. Make sure the file is in the same folder as the program.\n", path);

            }else{                                                  //LOAD_NO_MEMORY
                printf("Not enough memory to load the whole file. Please restart the program.\n");
            }
        }else{
            generateAllReports(A);
        }
    
        printf("\n");
        again = backToMainMenu();        //ask if they wanna go back to the main menu
    }while(again);
}