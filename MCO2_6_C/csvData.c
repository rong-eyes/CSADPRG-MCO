#include "csvData.h"

//helper function for loadTransactions to add the rows to the dynamic array
static int listPush(TransactionList *l, const Transaction *t){
    if (l->count == l->capacity){
        int newCap = l->capacity ? l->capacity * 2 : 1024;       //initializes the capacity to 1024 the first time
        Transaction *tmp = realloc(l->data, newCap * sizeof *tmp);  //reallocates the pointer to fit in a block of that size 
        if (!tmp) return 0;          //keep old block intact on failure
        l->data = tmp;
        l->capacity = newCap;
    }

    l->data[l->count++] = *t;
    return 1;                       //means succesfully added the row to the array
}

//helper function to get the allowed days of each month (not static anymore since its used in report)
int daysInMonth(int month, int year){
    //static const array of number of days per month
    static const int days[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    //check if the year is a leap year
    int isLeap = ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    if(month == 2 && isLeap) return 29;

    return days[month - 1];
}

static int isValidDate(int day, int month, int year){
    if (month < 1 || month > 12 || day < 1 || day > 31) return 0;
    return day <= daysInMonth(month, year);
}

//helper function to return correct enum for type
static TransactionType parseType(const char *s){
    if(strcmp(s, "deposit") == 0) return DEPOSIT;       
    if(strcmp(s, "withdrawal") == 0) return WITHDRAWAL;
    return UNKNOWN;         //return unknown if value is not any of the two
}

//helper function to split a row into distinct fields
static int splitCSV(char *line, char *fields[], int maxFields){
    int n = 0;
    char *p = line;             //acts as the cursor for the row

    //keep going until the number of fields is not met
    while (n < maxFields){
        fields[n++] = p;         //save the start of the line into fields array and increments the field number 
        p = strchr(p, ',');     //moves the pointer to the first comma it encounters
        if(!p) break;          //means reached the last field
        *p++ = '\0';            //replace ',' with a null byte and move to the cursor to next char so that next field starts there
    }
    return n;
}

// helper function to parse the row for invalid values
static int parseRow(char *line, Transaction *t){
    char *f[8];                             //local varb where the fiels get saved

    line[strcspn(line, "\r\n")] = '\0';
    if(splitCSV(line, f, 8) < 8) return 0;     //if there are less fields in the row than the correct number of fields return 0 (invalid)

    //any empty field = missing value
    for(int i = 0; i < 8; i++)
        if(f[i][0] == '\0') return 0;     //return 0 if there are empty fields

    //copy field name to accountName
    strncpy(t->accountName, f[0], MAX_NAME_LEN);   
    t->accountName[MAX_NAME_LEN] = '\0';
    
    //convert field acc number to long long and copy it to transaction accountNumber
    t->accountNumber = atoll(f[1]);             
    if(t->accountNumber <= 0) return 0;  //return 0 if accountNumber is negative / text

    //copy the date field to respective values, make sure 3 values were copied
    if(sscanf(f[2], "%d/%d/%d", &t->day, &t->month, &t->year) != 3) return 0;
    if(!isValidDate(t->day, t->month, t->year)) return 0;   //if not valid date return 0

    //copy the time field to respective values, make sure 3 values were copied
    if(sscanf(f[3], "%d:%d:%d", &t->hour, &t->min, &t->sec) != 3) return 0;
    if (t->hour < 0 || t->hour > 23 || t->min < 0 || t->min > 59 || t->sec  < 0 || t->sec  > 59) return 0;  //if not valid time return 0;

    if (strlen(f[4]) != 3) return 0;         //make sure currency only has 3 characters          

    //copy field currency to currency
    strncpy(t->currency, f[4], 3);
    t->currency[3] = '\0';

    //convert field to double / float and copy it to transaction amount
    t->amount = atof(f[5]);
    if(t->amount <= 0) return 0;  //return 0 if amount is negative / text

    //copy field type to transaction type
    t->type = parseType(f[6]);
    if(t->type == UNKNOWN) return 0;             

    //convert field to double / float and copy it to transaction balance
    t->balance = atof(f[7]);
    if(t->balance < 0) return 0;  //return 0 if balance is negative
    
    return 1;           // returns 1 if row is valid
}

//helper function to transform date and time to a single long long value to compare
static long long timeKey(const Transaction *t){
    long long key = t->year;
    key = key * 100 + t->month;
    key = key * 100 + t->day;
    key = key * 100 + t->hour;
    key = key * 100 + t->min;
    key = key * 100 + t->sec;
    return key;
}

//helper comparison function for qsort to sort by account number then date and time of transactions
static int cmpByAccountThenTime(const void *a, const void *b){
    const Transaction *x = a, *y = b;
    
    //compare account number (+ if a > b, - if a < b, 0 if a = b)
    long long ax = x->accountNumber, ay = y->accountNumber;
    int c = (ax > ay) - (ax < ay);
    if (c) return c;                        //only return if account number is not the same
    
    //compare timeKey (+ if a > b, - if a < b, 0 if a = b)
    long long kx = timeKey(x), ky = timeKey(y);
    return (kx > ky) - (kx < ky);
}

//helper function to sort in an ascending order the account number then date and time of transactions
static void sortTransactions(TransactionList *l){
    qsort(l->data, l->count, sizeof(Transaction), cmpByAccountThenTime);
}

//helper function to get the the current timestamp
static void stampNow(Transaction *t){
    time_t now = time(NULL);            //returns how much second has passed since January 1 1970
    struct tm *lt = localtime(&now);    //converts the time into broken down calander values based on PH time
    t->day = lt->tm_mday;  
    t->month = lt->tm_mon + 1;          //starts at 0 for january
    t->year = lt->tm_year + 1900;       //years since 1900
    t->hour = lt->tm_hour; 
    t->min = lt->tm_min;        
    t->sec = lt->tm_sec;
}

//helper function that returns 1 if the transaction would pass parseRow's checks
static int isValidTransaction(const Transaction *t){
    return t->accountName[0] != '\0'                    //not empty name
        && strchr(t->accountName, ',') == NULL          //no , in acc name
        && t->accountNumber > 0                         //non 0 acc number
        && strlen(t->currency) == 3                     //currency has len 3
        && t->type != UNKNOWN                           //not an unkown type
        && t->amount > 0                                //amount is non 0
        && t->balance >= 0;                             //balance is not negative
}

//function to load the csv file into transaction list
int loadTransactions(const char *path, TransactionList *out, int *totalRows, int *badRows){
    out->count = 0;                              //restart the count in the list for when theres already a previously loaded list
    
    FILE *fp = fopen(path, "r");
    char buffer[512];
    
    *totalRows = 0;                 //count of total rows  
    *badRows = 0;                   //count of bad rows
    if (!fp) return LOAD_NO_FILE;                           //ret 0 if file does not exist

    fgets(buffer, sizeof buffer, fp);            //skip the header
    while(fgets(buffer, sizeof buffer, fp)){
        Transaction temp;                   //temporarily store the values in here
        (*totalRows)++;                     //increment since counting total rows
        
        if(!parseRow(buffer, &temp)){      //if it does not pass the parsing
            (*badRows)++;                   //increment bad rows
            continue;                       //go to the next row
        }

        if(strcmp(temp.currency, "PHP") != 0) continue;        //we only include PHP transactions
        
        if(!listPush(out, &temp)){          //if the system does not have enough memory anymore
            fclose(fp); 
            return LOAD_NO_MEMORY;                       //ret -1
        }
    }
    fclose(fp);
    if(out->count > 1) sortTransactions(out);      //sort transactions
    return LOAD_OK;                   //ret 1
}

//function to free the transaction list
void freeTransactions(TransactionList *l){
    free(l->data);
    l->data = NULL;
    l->count = 0;
    l->capacity = 0;
}

//function that finds the latest transaction of an acc name, assumes list is sorted by acc number and time thats why lastIdx of same acc returns latest transaction
int searchAcc(TransactionList *l, const StringName acc, Transaction *out){
    int latestIdx = -1;

    for(int i = 0; i < l->count; i++){
        if(strcmp(l->data[i].accountName, acc) == 0){
            latestIdx = i;
        }
    }

    if(latestIdx != -1){
        *out = l->data[latestIdx];              //copies the data in list into out
    }

    return latestIdx != -1;         //return 0 if no acc found else returns 1
}

//function to get the next account number
long long nextAccNumber(TransactionList *l){
    if (l->count == 0){                 //if list is empty
        return FIRST_ACCOUNT_NUMBER;    
    }else{
        return l->data[l->count - 1].accountNumber + 1;     //always guaranteed that the last index has biggest acc number because it is sorted
    }
}

int appendTransaction(const char *path, const Transaction *t){
    int success;

    if(!isValidTransaction(t)) return 0;

    FILE *fp = fopen(path, "a");
    if (!fp) return 0;               //if file does not exist ret 0

    fseek(fp, 0, SEEK_END);                             //position at the end so ftell reports the real size
    success = 1;

    if(ftell(fp) == 0){                                 //file is new or empty
        success = fprintf(fp, "account_name,account_number,date,time,currency,amount,type,current_balance\n") > 0;
    }

    if(success){                                        //only attempt the row if the header (when needed) went through
        success = fprintf(fp, "%s,%lld,%02d/%02d/%04d,%02d:%02d:%02d,%s,%.2f,%s,%.2f\n",
                          t->accountName, t->accountNumber,
                          t->day, t->month, t->year, t->hour, t->min, t->sec,
                          t->currency, t->amount,
                          t->type == DEPOSIT ? "deposit" : "withdrawal",
                          t->balance) > 0;              //negative means write error
    }

    if(fclose(fp) != 0) success = 0;                    //flushing can fail while closing
    return success;                     //ret 1 means append success
}

int recordTransaction(TransactionList *l, const char *path, const char *name, long long accNum, TransactionType type, double amount, double newBalance){
    Transaction t;

    strncpy(t.accountName, name, MAX_NAME_LEN);
    t.accountName[MAX_NAME_LEN] = '\0';
    t.accountNumber = accNum;
    strcpy(t.currency, "PHP");              //accounts are PHP-only (group decision)
    t.type = type;
    t.amount = amount;
    t.balance = newBalance;
    stampNow(&t);                           //fills in the date and time

    if(!appendTransaction(path, &t)){
        return 0;           //list untouched, so nothing to undo 
    }

    if(!listPush(l, &t)){
        return -1;         //the row is in the file but not the list (happens when realloc cant allocate more space anymore)
    }

    if(l->count > 1) sortTransactions(l);   //keep the list ordered
    return 1;           //succesfully added the record in the list
}