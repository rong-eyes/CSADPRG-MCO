#ifndef ACCOUNTS_H
#define ACCOUNTS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_NAME_LEN 100    //assume that full names dont go beyond 100 char
typedef char StringName[MAX_NAME_LEN + 1];  //string data type for name inputs; +1 for \0
typedef char StringCurrency[4];             //string data type for 3 letter currency inputs; +1 for \0

typedef struct accountNodeTag{
    StringName account;                     //acc name
    StringCurrency currency;                //set currency of account
    double amount;                           //how much they have in the "bank"
    struct accountNodeTag *pNext;
} AccountNode;

typedef struct accountListTag{
    AccountNode *pHead;                     //first on the list
    AccountNode *pTail;                     //last on the list
} AccountList;

void CreateAccountList(AccountList *A);
void DestroyAccountList(AccountList *A);
int newAccount(AccountList *A, StringName acc, StringCurrency curr);
AccountNode *searchAccount(AccountList *A, StringName acc);

#endif