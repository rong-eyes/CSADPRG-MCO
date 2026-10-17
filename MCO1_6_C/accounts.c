#include "accounts.h"

void CreateAccountList(AccountList *A){
    A->pHead = NULL;
    A->pTail = NULL;
}

void DestroyAccountList(AccountList *A){
    AccountNode *pRemove;

    while(A->pHead != NULL){                        //deallocate everything in the list
        pRemove = A->pHead;
        A->pHead = A->pHead->pNext;

        if(A->pHead == NULL){                       //if theres nothing in the list already, make sure to make the pTail point to just null
            A->pTail = NULL;
        }
        free(pRemove);
    }
}

int newAccount(AccountList *A, StringName acc, StringCurrency curr){
    int nRet = 0;
    if(searchAccount(A, acc) == NULL){                                          //make sure acc doesnt exist already
        AccountNode *pNew = (AccountNode *)malloc(sizeof(AccountNode));

        if(pNew != NULL){                                                       //make sure the malloc was able to allocate memory
            strcpy(pNew->account, acc);
            strcpy(pNew->currency, curr);
            pNew->amount = 0;
            pNew->pNext = NULL;

            if(A->pHead == NULL){                                       //means the list is still empty
                A->pHead = pNew;
                A->pTail = pNew;
            }else{                                                      //means the list already has values
                A->pTail->pNext = pNew;
                A->pTail = pNew;
            }
            nRet = 1;                               //addition of newAcc is successfull
        }
    }
    return nRet;
}

AccountNode *searchAccount(AccountList *A, StringName acc){
    AccountNode *pCurr = A->pHead;

    //if list not empty and its not the same account, keep going to the next account
    while(pCurr != NULL && strcmp(pCurr->account, acc) != 0){
        pCurr = pCurr->pNext;
    }
    return pCurr;   // NULL if not found
}