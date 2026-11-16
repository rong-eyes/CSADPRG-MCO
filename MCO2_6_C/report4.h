#ifndef REPORT4_H
#define REPORT4_H

#include "csvData.h"
#include "reportCommon.h"

#define REPORT4 "report4_monthly_net_cash_flow.csv"

typedef struct{
    StringName accountName;
    long long accountNumber;        //just in case the first digits of the number reach more than what int can handle           
    int monthKey;                   //yyyymm, to keep tracks of month and year
    double totalWithdrawal;
    double totalDeposit;
    double netCashFlow;
    double monthEndBalance;
} MonthlyFlow;

typedef struct{
    MonthlyFlow *data;
    int count;
} MonthlyFlowList;

int fillMonthlyFlow(const TransactionList *l, MonthlyFlowList *out);
void freeMonthlyFlow(MonthlyFlowList *f);
int generateReport4(MonthlyFlowList *f);

#endif
