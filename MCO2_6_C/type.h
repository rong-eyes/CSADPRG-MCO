#ifndef TYPE_H
#define TYPE_H

#define MAX_NAME_LEN 100                     //assume that full names dont go beyond 100 char
typedef char StringName[MAX_NAME_LEN + 1];  //string data type for name inputs; +1 for \0
typedef char StringCurrency[4];             //string data type for 3 letter currency inputs; +1 for \0

#endif