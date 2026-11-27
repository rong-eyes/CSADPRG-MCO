/********************
Last name: Sandoval
Language: Java
Paradigm: Object-Oriented Programming
********************/

import java.util.Scanner;

/* input helper */
public class Input{
    private static final Scanner sc = new Scanner(System.in);

    private Input(){}

    public static String readLine(String prompt){
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static int readInt(String prompt){
        while (true){
            try{
                return Integer.parseInt(readLine(prompt));
            }
            catch (NumberFormatException e){
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public static int readChoice(String prompt, int min, int max){
        int value = readInt(prompt);

        while (value < min || value > max){
            System.out.println("Please select a valid option.");
            value = readInt(prompt);
        }

        return value;
    }

    public static double readDouble(String prompt){
        while (true){
            try{
                double v = Double.parseDouble(readLine(prompt).replace(",", ""));

                if (Double.isFinite(v)){
                    return v;
                }
            }
            catch (NumberFormatException e){
                // fall through to the message below
            }

            System.out.println("Please enter a valid number.");
        }
    }

    public static double readPositive(String prompt){
        double v = readDouble(prompt);

        while (v <= 0){
            System.out.println("Please enter an amount greater than 0.");
            v = readDouble(prompt);
        }

        return v;
    }

    public static boolean askYesNo(String prompt){
        System.out.print(prompt);

        while (true){
            String answer = sc.nextLine().trim();

            if (answer.equalsIgnoreCase("Y")){
                return true;
            }
            if (answer.equalsIgnoreCase("N")){
                return false;
            }

            System.out.println("Please choose a valid option.");
            System.out.print(prompt);
        }
    }

    public static void close(){
        sc.close();
    }
}
