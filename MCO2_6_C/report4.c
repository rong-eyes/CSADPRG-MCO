#include "report4.h"

//called once a month's row have all been added up
static void finishMonth(MonthlyFlow *d){
    d->totalWithdrawal = roundDecimals(d->totalWithdrawal);
    d->totalDeposit = roundDecimals(d->totalDeposit);

    
    d->netCashFlow = roundDecimals(d->totalDeposit - d->totalWithdrawal);
}

//builds one MonthlyFlow per account per active month; assumes the list is sorted by account number then time
int fillMonthlyFlow(const TransactionList *l, MonthlyFlowList *out){
    out->data = NULL;
    out->count = 0;
    if(l->count == 0) return 0;

    out->data = malloc(l->count * sizeof *out->data);       //upper bound: at most one account-day per row
    if(out->data == NULL) return 0;

    for(int i = 0; i < l->count; i++){
        const Transaction *t = &l->data[i];
        MonthlyFlow *cur = (out->count > 0) ? &out->data[out->count - 1] : NULL;    //the month being built

        //a new account-month starts when the account or the month changes
        if(cur == NULL || cur->accountNumber != t->accountNumber || cur->monthKey != monthKey(t)){
            if(cur != NULL) finishMonth(cur);                 //close the previous month first

            cur = &out->data[out->count++];                 //open the next slot
            strcpy(cur->accountName, t->accountName);
            cur->accountNumber = t->accountNumber;
            cur->monthKey = monthKey(t);
            cur->totalWithdrawal = 0;
            cur->totalDeposit = 0;
        }

        if(t->type == DEPOSIT){
            cur->totalDeposit += t->amount;
        }else{
            cur->totalWithdrawal += t->amount;
        }

        cur->monthEndBalance = t->balance;
    }

    finishMonth(&out->data[out->count - 1]);                  //the last day never sees a "change", so close it here
    return 1;
}

void freeMonthlyFlow(MonthlyFlowList *f){
    free(f->data);
    f->data = NULL;
    f->count = 0;
}

//helper function sorts by month, then net cash flow, then account number
static int cmpMonthlyFlow(const void *a, const void *b){
    const MonthlyFlow *x = a, *y = b;

    if(x->monthKey != y->monthKey){
        return (x->monthKey > y->monthKey) - (x->monthKey < y->monthKey);
    }

    if(x->netCashFlow != y->netCashFlow){
        return (x->netCashFlow > y->netCashFlow) - (x->netCashFlow < y->netCashFlow);
    }

    return (x->accountNumber > y->accountNumber) - (x->accountNumber < y->accountNumber);
}

//helper function to sort in a ascending by month, then ascending net cash flow, then ascending account number
static void sortSummary4(MonthlyFlowList *f){
    qsort(f->data, f->count, sizeof(MonthlyFlow), cmpMonthlyFlow);
}

//function to generate report 4
int generateReport4(MonthlyFlowList *f){
    char wit[NUM_LEN + 2], dep[NUM_LEN + 2], net[NUM_LEN + 2], bal[NUM_LEN + 2];
    char month[9];
    int shown = 0;                                          //rows previewed on screen so far
    FILE *fp = openReportFile(REPORT4, "AccountName,Month,TotalWithdrawalAmounts,TotalDepositAmounts,NetCashFlow,EndOfMonthBalance");
    if(fp == NULL) return 0;

    sortSummary4(f);                            //ascending month, then net cash flow, then account number

    for(int i = 0; i < f->count; i++){
        MonthlyFlow *mf = &f->data[i];

        formatMonth(mf->monthKey, month);
        formatNumberCsv(mf->totalWithdrawal, wit);
        formatNumberCsv(mf->totalDeposit, dep);
        formatNumberCsv(mf->netCashFlow, net);
        formatNumberCsv(mf->monthEndBalance, bal);

        fprintf(fp, "%s,%s,%s,%s,%s,%s\n", mf->accountName, month, wit, dep, net, bal);

        if(shown < 2){                                      //the first two rows double as the on-screen preview
            formatNumber(mf->totalWithdrawal, wit);         //same values, without the quotes
            formatNumber(mf->totalDeposit, dep);
            formatNumber(mf->netCashFlow, net);
            formatNumber(mf->monthEndBalance, bal);

            if(shown == 0){
                printf("%-20s %-8s %-23s %-20s %-14s %s\n", "AccountName", "Month", "TotalWithdrawalAmounts",
                       "TotalDepositAmounts", "NetCashFlow", "EndOfMonthBalance");
            }
            printf("%-20s %-8s %-23s %-20s %-14s %s\n", mf->accountName, month, wit, dep, net, bal);
            shown++;
        }
    }

    if(!closeReportFile(fp)){
        printf("Warning: the report file may be incomplete.\n");
        return 0;
    }
    return 1;
}
