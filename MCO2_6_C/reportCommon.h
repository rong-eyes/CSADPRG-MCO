#ifndef REPORT_COMMON_H
#define REPORT_COMMON_H

#include <math.h>
#include "csvData.h"

#define NUM_LEN 32                    //enough for the largest long long value, commas, sign and decimals
#define DATE_LEN 16                   //enough for dd/mm/yyyy or mm/yyyy, even in the worst case

//date helpers
int dayNumber(int day, int month, int year);            //days since 01/01/1970, so two dates can be subtracted
int dateKey(const Transaction *t);                      //yyyymmdd (bigger = later)
int monthKey(const Transaction *t);                     //yyyymm
void formatDate(int key, char *out);                    //yyyymmdd -> dd/mm/yyyy
void formatMonth(int key, char *out);                   //yyyymm -> mm/yyyy

//number helpers
double roundDecimals(double x);                         //rounds to 2 decimal places
void formatNumber(double value, char *out);             //1234567.891 -> 1,234,567.89
void formatNumberCsv(double value, char *out);          //same, wrapped in "" so the commas dont split the csv
void formatCount(int value, char *out);                 //9189 -> 9,189

//report file helpers
FILE *openReportFile(const char *path, const char *header);
int closeReportFile(FILE *fp);

#endif
