#include "report3.h"

//called once a day's rows have all been added up
static void finishDay(DailyBehavior *d){
    d->totalWithdrawal = roundDecimals(d->totalWithdrawal);
    d->totalDeposit = roundDecimals(d->totalDeposit);

    d->hasVelocity = d->totalDeposit > 0;
    d->velocity = d->hasVelocity ? roundDecimals(d->totalWithdrawal / d->totalDeposit * 100.0) : 0.0;

    //the velocity test is skipped when it can't be computed
    d->suspicious = d->totalWithdrawal >= SUSPICIOUS_WITHDRAWAL || (d->hasVelocity && d->velocity >= SUSPICIOUS_VELOCITY);
}

//helper comparison function for qsort to sort by date, risk flag level, and account number
static int cmpDaily(const void *a, const void *b){
    const DailyBehavior *x = a, *y = b;

    if(x->dateKey != y->dateKey) return (x->dateKey > y->dateKey) - (x->dateKey < y->dateKey);          //ascending date
    if(x->suspicious != y->suspicious) return y->suspicious - x->suspicious;                              //Suspicious (1) first
    return (x->accountNumber > y->accountNumber) - (x->accountNumber < y->accountNumber);
}

//helper function to sort in a ascending by Date, descending by RiskFlagLevel (suspicious -> normal), ascending by account number
static void sortSummary3(DailyBehaviorList *d){
    qsort(d->data, d->count, sizeof(DailyBehavior), cmpDaily);
}

//builds one DailyBehavior per account per active day; assumes the list is sorted by account number then time
int fillDailyBehavior(const TransactionList *l, DailyBehaviorList *out){
    out->data = NULL;
    out->count = 0;
    if(l->count == 0) return 0;

    out->data = malloc(l->count * sizeof *out->data);       //upper bound: at most one account-day per row
    if(out->data == NULL) return 0;

    for(int i = 0; i < l->count; i++){
        const Transaction *t = &l->data[i];
        DailyBehavior *cur = (out->count > 0) ? &out->data[out->count - 1] : NULL;    //the day being built

        //a new account-day starts when the account or the date changes
        if(cur == NULL || cur->accountNumber != t->accountNumber || cur->dateKey != dateKey(t)){
            if(cur != NULL) finishDay(cur);                 //close the previous day first

            cur = &out->data[out->count++];                 //open the next slot
            strcpy(cur->accountName, t->accountName);
            cur->accountNumber = t->accountNumber;
            cur->dateKey = dateKey(t);
            cur->totalWithdrawal = 0;
            cur->numDeposits = 0;
            cur->totalDeposit = 0;
        }

        //add this row to the current day
        if(t->type == DEPOSIT){
            cur->numDeposits++;
            cur->totalDeposit += t->amount;
        }else{
            cur->totalWithdrawal += t->amount;
        }
    }

    finishDay(&out->data[out->count - 1]);                  //the last day never sees a "change", so close it here
    return 1;
}

void freeDailyBehavior(DailyBehaviorList *d){
    free(d->data);
    d->data = NULL;
    d->count = 0;
}

//function to generate report 3
int generateReport3(DailyBehaviorList *d){
    char wit[NUM_LEN + 2], dep[NUM_LEN + 2], vel[NUM_LEN + 2];
    char date[12];
    int shown = 0;                                          //rows previewed on screen so far
    FILE *fp = openReportFile(REPORT3, "AccountName,Date,TotalWithdrawalAmounts,NumberOfDeposits,TotalDepositAmounts,TurnoverVelocity,RiskFlagLevel");
    if(fp == NULL) return 0;

    sortSummary3(d);                            //ascending date, then Suspicious first, then account number

    for(int i = 0; i < d->count; i++){
        DailyBehavior *b = &d->data[i];
        const char *flag = b->suspicious ? "Suspicious" : "Normal";

        formatDate(b->dateKey, date);
        formatNumberCsv(b->totalWithdrawal, wit);
        formatNumberCsv(b->totalDeposit, dep);
        if(b->hasVelocity) formatNumberCsv(b->velocity, vel);
        else strcpy(vel, "N/A");                //no deposits that day, so the velocity can't be computed

        fprintf(fp, "%s,%s,%s,%d,%s,%s,%s\n", b->accountName, date, wit, b->numDeposits, dep, vel, flag);

        if(shown < 2){                                      //the first two rows double as the on-screen preview
            formatNumber(b->totalWithdrawal, wit);           //same values, without the quotes
            formatNumber(b->totalDeposit, dep);
            if(b->hasVelocity) formatNumber(b->velocity, vel);
            else strcpy(vel, "N/A");

            if(shown == 0){
                printf("%-20s %-11s %-23s %-17s %-20s %-17s %s\n", "AccountName", "Date", "TotalWithdrawalAmounts", 
                        "NumberOfDeposits", "TotalDepositAmounts", "TurnoverVelocity", "RiskFlagLevel");
            }
            printf("%-20s %-11s %-23s %-17d %-20s %-17s %s\n", b->accountName, date, wit, b->numDeposits, dep, vel, flag);
            shown++;
        }
    }

    if(!closeReportFile(fp)){
        printf("Warning: the report file may be incomplete.\n");
        return 0;
    } 
    return 1;
}
