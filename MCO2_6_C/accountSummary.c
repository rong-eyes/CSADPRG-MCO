#include "accountSummary.h"

//helper function that gets the last complete month for report 1
void referenceMonth(const TransactionList *l, int *month, int *year){
    int bestKey = 0;
    int m = 0, y = 0, d = 0;

    for(int i = 0; i < l->count; i++){
        const Transaction *t = &l->data[i];
        int key = dateKey(t);

        if(key > bestKey){
            bestKey = key;
            y = t->year;
            m = t->month;
            d = t->day;
        }
    }

    if(bestKey != 0 && d != daysInMonth(m, y)){        //newest month is not finished, so use the one before it
        m--;
        if(m == 0){
            m = 12;
            y--;
        }
    }

    *month = m;
    *year = y;
}

//helper function to get the number of accounts in the transaction csv
static int countAccounts(const TransactionList *l){
    int count = 0;
    long long prevNum = -1;

    for(int i = 0; i < l->count; i++){
        const Transaction *t = &l->data[i];
        if(prevNum != t->accountNumber){
            count++;
            prevNum = t->accountNumber;
        }
    }
    return count;
}

//helper function that allocates memory for an account summary list for the reports
int allocAccountSummary(const TransactionList *l, AccountSummaryList *s){
    int n = countAccounts(l);

    s->data = NULL;
    s->count = 0;
    if(n == 0) return 0;                          //nothing to summarize

    s->data = malloc(n * sizeof *s->data);
    if(s->data == NULL) return 0;               //not enough memory

    s->count = n;                               //the fill pass will set all n entries
    return 1;
}

//helper function that frees the allocated space for the account summary list
void freeAccountSummary(AccountSummaryList *s){
    free(s->data);
    s->data = NULL;
    s->count = 0;
}

//helper function to get the expected interest after 30 days with 5% interest
static double calcExpectedInterest(double balance){
    double current = balance;
    double rate = INTEREST_RATE / 365;

    for(int i = 1; i <= PROJECTION_DAYS; i++){
        current += roundDecimals(current * rate);
    }
    return roundDecimals(current - balance);
}

//fills one summary per account; assumes the list is sorted by account number then time
void fillAccountSummary(const TransactionList *l, AccountSummaryList *s, int month, int year){
    int monthDays = daysInMonth(month, year);
    int monthEnd = dayNumber(monthDays, month, year);       //day number of the last day of the reference month
    int filled = 0;                                         //index used for account summary
    int start = 0;                                          //starting index of that acc number

    while(start < l->count && filled < s->count){
        int end = start;              
        AccountSummary *a = &s->data[filled];
        const Transaction *first;       //first transaction of the acc
        const Transaction *last;        //most current transaction of the acc
        double carry = 0;               //balance carried forward from the latest row seen so far
        double sum = 0;                 //total sum of end day balance
        int idx = start;                //starting idx of the current acc

        //find where this account's rows end
        while(end < l->count && l->data[end].accountNumber == l->data[start].accountNumber){
            end++;
        }

        first = &l->data[start];
        last = &l->data[end - 1];       //end - 1 because current end index points to the start of a new acc

        strcpy(a->accountName, first->accountName);     //copy name
        a->accountNumber = first->accountNumber;        //copy acc number
        a->latestBalance = last->balance;               //copy latest balance
        a->expectedInterest = calcExpectedInterest(a->latestBalance); 

        a->accountAge = monthEnd - dayNumber(first->day, first->month, first->year);    //accAge = end of reference month - date created
        if(a->accountAge < 0) a->accountAge = 0;       //account opened after the reference month

        //go through each day of the month and add that day's end-of-day balance
        for(int day = 1; day <= monthDays; day++){
            int dayKey = year * 10000 + month * 100 + day;

            //use every row dated on or before this day; the last one used is the end-of-day balance
            while(idx < end && dateKey(&l->data[idx]) <= dayKey){
                carry = l->data[idx].balance;
                idx++;
            }
            sum += carry;               //no rows today means yesterday's balance carries forward
        }
        
        a->monthEndBalance = carry;     //last carry serves as the balance at the end of the month
        a->avgDailyBalance = roundDecimals(sum / monthDays);

        filled++;               //next account to fill in the summary list
        start = end;            //since end is the start of the new acc
    }

    s->count = filled;                  //should equal the allocated count; keeps the count truthful if not
}
