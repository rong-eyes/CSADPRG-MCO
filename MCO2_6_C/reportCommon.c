#include "reportCommon.h"

//helper function that rets 1 if year is a leap year; 0 otherwise
static int isLeapYear(int y){
    return ((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0);
}

//helper function that translates the days in number from 01/01/1970 to given date
int dayNumber(int day, int month, int year){
    int days = 0;

    for(int y = 1970; y < year; y++)
        days += isLeapYear(y) ? 366 : 365;

    for(int m = 1; m < month; m++)
        days += daysInMonth(m, year);
    
    return days + day;
}

//helper function that packs a transaction's date as yyyymmdd (bigger = later)
int dateKey(const Transaction *t){
    return t->year * 10000 + t->month * 100 + t->day;
}

//helper function that packs a transaction's date as yyyymm (bigger = later)
int monthKey(const Transaction *t){
    return t->year * 100 + t->month;
}

//helper function to round to 2 decimal places
double roundDecimals(double x){
    return round(x * 100.0) / 100.0;
}

//helper function that writes value into out as text with thousands separators and 2 decimals
void formatNumber(double value, char *out){
    char digits[24];
    long long cents = llround(fabs(value) * 100.0);     //get abs value and round to the nearest cent first 
    long long whole = cents / 100;                      //whole part
    int frac = (int)(cents % 100);                      //decimal part
    int len;
    int pos = 0;

    snprintf(digits, sizeof digits, "%lld", whole);
    len = (int)strlen(digits);

    if(value < 0 && cents != 0){
        out[pos++] = '-';
    }

    for(int i = 0; i < len; i++){
        if(i > 0 && (len - i) % 3 == 0){                //a comma goes before every group of three digits counted from the right
            out[pos++] = ',';
        }
        out[pos++] = digits[i];
    }

    snprintf(out + pos, NUM_LEN- pos, ".%02d", frac);    //adds the decimal part into the string
}

//helper function that wraps the num string in "" so that the commas dont split in the csv
void formatNumberCsv(double value, char *out){
    char plain[NUM_LEN];

    formatNumber(value, plain);
    sprintf(out, "\"%s\"", plain);                      
}

//writes a whole number into out with thousands separators, e.g. 9189 -> "9,189" (assumes value >= 0)
void formatCount(int value, char *out){
    char digits[16];
    int len;
    int pos = 0;

    snprintf(digits, sizeof digits, "%d", value);
    len = (int)strlen(digits);


    for(int i = 0; i < len; i++){
        if(i > 0 && (len - i) % 3 == 0){                //a comma goes before every group of three digits counted from the right
            out[pos++] = ',';
        }
        out[pos++] = digits[i];
    }
    out[pos] = '\0';
}

//writes dateKey (yyyymmdd) into out as dd/mm/yyyy; out needs at least 11 bytes
void formatDate(int key, char *out){
    snprintf(out, DATE_LEN, "%02d/%02d/%04d", key % 100, (key / 100) % 100, key / 10000);
}

void formatMonth(int key, char *out){
    snprintf(out, DATE_LEN, "%02d/%04d", key % 100, key / 100);
}

//opens a report file for writing and writes the header line; returns NULL (after printing a message) on failure
FILE *openReportFile(const char *path, const char *header){
    FILE *fp = fopen(path, "w");

    if(fp == NULL){
        printf("Could not create %s. Is it open in another program?\n", path);
        return NULL;
    }

    fprintf(fp, "%s\n", header);
    return fp;
}

//closes a report file; returns 1 if everything was written correctly, 0 otherwise
int closeReportFile(FILE *fp){
    return fclose(fp) == 0;                             //flushing can fail while closing
}
