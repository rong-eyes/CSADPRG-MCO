#ifndef INPUT_CHECK_H
#define INPUT_CHECK_H

#include <stdio.h>
#include <string.h>
#include <math.h>
#include "accounts.h"

#define BUFFER_LEN 64               //assume a 63 buffer limit


int readLine(char *buffer, int size);
int readInt(int *output);
int readDouble(double *output);
int readName(StringName output);
int readChar(char *output);

#endif