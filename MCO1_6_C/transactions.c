#include "transactions.h"

int depositAmount(AccountNode *account, double amt){
    int ret;

    amt = floor(amt * 100.00 + 1e-9) / 100.00;      //round down, customer never gets extra

    if(amt > 0){
        account->amount += amt;
        account->amount = round(account->amount * 100.00) / 100.00;           //round off to two decimal places
        ret = 1;                        //successful deposit
    }else{
        ret = -1;                       //means amt is 0 or negative
    }

    return ret;
}

int withdrawAmount(AccountNode *account, double amt){
    int ret;

    amt = ceil(amt * 100.00 - 1e-9) / 100.00;       //round up, customer never pays less

    if(amt > 0){
        if(account->amount >= amt){
            account->amount -= amt;
            account->amount = round(account->amount * 100.00) / 100.00;           //round off to two decimal places
            ret = 1;                    //successful withdraw
        }else{
            ret = -2;                   //means amt is greater than whats in the bank
        } 
    }else{
        ret = -1;                    //means amt is 0 or negative   
    }
    
    return ret;
}

int interestAmount(AccountNode *account, int days){
    int ret = 0;

    if(days > 0){
        ret = 1;                                        //successful interest
        double balance = account->amount;
        double rate = INTEREST_RATE / 365;
        printf("Day  | Interest | Balance       |\n");
        for(int i = 1; i <= days; i++){
            double interest = balance * rate;
            interest = round(interest * 100.00) / 100.00;                 //round off to two decimal places
            balance += interest;
            printf("%-5d| %-9.2lf| %-14.2lf|\n", i, interest, balance);
        }
    }else{
        ret = -1;                                       //means days is 0 or negative
    }
    
    return ret;
}
