#ifndef ACCOUNT_SUMMARY_H
#define ACCOUNT_SUMMARY_H

#include "csvData.h"
#include "transactions.h"               
#include "reportCommon.h"

#define PROJECTION_DAYS 30

typedef struct{
    StringName accountName;
    long long accountNumber;        //just in case the first digits of the number reach more than what int can handle           
    int accountAge;
    double latestBalance;
    double monthEndBalance;
    double avgDailyBalance;         //based on the reference month
    double expectedInterest;
} AccountSummary;

typedef struct{
    AccountSummary *data;
    int count;
} AccountSummaryList;

void referenceMonth(const TransactionList *l, int *month, int *year);
int allocAccountSummary(const TransactionList *l, AccountSummaryList *s);
void freeAccountSummary(AccountSummaryList *s);
void fillAccountSummary(const TransactionList *l, AccountSummaryList *s, int month, int year);

#endif
