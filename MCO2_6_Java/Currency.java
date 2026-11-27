/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

public class Currency{
    public String name;
    public double rate;

    Currency(String name){
        this.name = name;
        this.rate = 0;
    }

    Currency(String name, double rate){
        this.name = name;
        this.rate = rate;
    }
}
