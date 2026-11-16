#ifndef REPORT3_H
#define REPORT3_H

#include "csvData.h"
#include "reportCommon.h"

#define REPORT3 "report3_suspicious_client_behavior.csv"
#define SUSPICIOUS_WITHDRAWAL 500000.00
#define SUSPICIOUS_VELOCITY 90.00

typedef struct{
    StringName accountName;
    long long accountNumber;        //just in case the first digits of the number reach more than what int can handle           
    int dateKey;                    //yyyymmdd, for sorting and printing
    double totalWithdrawal;
    int numDeposits;
    double totalDeposit;
    int hasVelocity;                //0 when there were no deposits that day
    double velocity;                //only meaningful if hasVelocity is 1
    int suspicious;                 //1 = suspicious, 0 = normal
} DailyBehavior;

typedef struct{
    DailyBehavior *data;
    int count;
} DailyBehaviorList;

int fillDailyBehavior(const TransactionList *l, DailyBehaviorList *out);
void freeDailyBehavior(DailyBehaviorList *d);
int generateReport3(DailyBehaviorList *d);

#endif
