#include "inputCheck.h"

//use fgets to store the input in a string but removes the newline and discards excess input
int readLine(char *buffer, int size){
    int ret = 0;                            //means wrong input, line too long, or no input

    if(fgets(buffer, size, stdin) != NULL){     //make sure theres an input
        int len = strlen(buffer);

        if(len > 0 && buffer[len - 1] == '\n'){     //change the inputted new line into a null terminator
            buffer[len - 1] = '\0';
            ret = 1;                        //success
        }else{
            int c;
            while((c = getchar()) != '\n' && c != EOF);     //line exceeded the buffer so just discard them
        }
    }
    return ret;
}

//returns 1 if read line true and sscanf was able to input the value in buffer to output
int readInt(int *output){
    char buffer[BUFFER_LEN];
    return readLine(buffer, sizeof buffer) && sscanf(buffer, "%d", output) == 1;   
}

//returns 1 if read line true and sscanf was able to input the value in buffer to output, make sure input is not inf
int readDouble(double *output){
    char buffer[BUFFER_LEN];
    return readLine(buffer, sizeof buffer) && sscanf(buffer, "%lf", output) == 1 && isfinite(*output);
}

//returns 1 if read line true and there is only a single character in buffer
int readChar(char *output){
    char buffer[BUFFER_LEN];
    int ret = readLine(buffer, sizeof buffer);

    if(ret && buffer[0] != '\0' && buffer[1] == '\0'){
        *output = buffer[0];
    }else{
        ret = 0;                    //empty, more than one character, or too long
    }
    return ret;
}

//returns 1 if read line true and buffer is not empty
int readName(StringName output){
    char buffer[MAX_NAME_LEN + 2];             //100 chars + '\n' + '\0'
    int ret = readLine(buffer, sizeof buffer);

    if(ret && buffer[0] != '\0'){
        strcpy(output, buffer);
    }else{
        ret = 0;
    }

    return ret;
}