/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.util.ArrayList;
import java.util.List;

public class DataSet{
    final List<Transaction> transactions = new ArrayList<>();
    final List<String> errors = new ArrayList<>();
    int rowsRead = 0;
    int nonPhpRows = 0;
    int imputedBalances = 0;
}
