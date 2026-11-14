#ifndef CSVDATA_H
#define CSVDATA_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "type.h"
#define TRANSACTIONS_CSV "bank_transactions.csv"
#define FIRST_ACCOUNT_NUMBER 1092000001LL       //in case file is empty

//used for load transactions
#define LOAD_OK 1
#define LOAD_NO_FILE 0
#define LOAD_NO_MEMORY -1

//enum for transaction type
typedef enum{
    UNKNOWN,        //for catching invalid values
    DEPOSIT,
    WITHDRAWAL
} TransactionType;

typedef struct{
    StringName accountName;
    long long accountNumber;        //just in case the first digits of the number reach more than what int can handle
    StringCurrency currency;
    TransactionType   type;            
    int    year, month, day, hour, min, sec;
    double amount;
    double balance;
} Transaction;

//dynamic array for Transactions
typedef struct{
    Transaction *data;
    int count;
    int capacity;
} TransactionList;


int loadTransactions(const char *path, TransactionList *out, int *totalRows, int *badRows);
void freeTransactions(TransactionList *l);
int searchAcc(TransactionList *l, const StringName acc, Transaction *out);
long long nextAccNumber(TransactionList *l);
int appendTransaction(const char *path, const Transaction *t);
int recordTransaction(TransactionList *l, const char *path, const char *name, long long accNum, TransactionType type, double amount, double newBalance);


#endif