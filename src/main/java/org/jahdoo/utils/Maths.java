package org.jahdoo.utils;

import java.text.DecimalFormat;

import static org.jahdoo.utils.ModHelpers.Random;

public class Maths {

    public static double getPercentage(double multiplier, double baseValue){
        return (multiplier * baseValue) / 100;
    }

    public static double getPercentageTotal(double multiplier, double baseValue){
        return baseValue + (multiplier * baseValue) / 100;
    }

    public static boolean percentageChance(int percentageChance) {
        if(percentageChance == 0) return false;
        if (percentageChance < 0 || percentageChance > 100) {
            throw new IllegalArgumentException("Percentage chance must be between 0 and 100.");
        }

        int randomValue = Random.nextInt(100) + 1; // Generates a number between 1 and 100
        return randomValue <= percentageChance;
    }

    public static String roundNonWholeString(double number) {
        double decimalPart = number - (int) number;
        if (decimalPart == 0) return String.valueOf(Math.round(number));
        return String.valueOf(number);
    }

    public static String roundNonWholeString(String input) {
        StringBuilder result = new StringBuilder();
        StringBuilder numberBuffer = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else {
                if (!numberBuffer.isEmpty()) {
                    result.append(processNumber(numberBuffer.toString()));
                    numberBuffer.setLength(0);
                }
                result.append(c);
            }
        }

        if (!numberBuffer.isEmpty()) {
            result.append(processNumber(numberBuffer.toString()));
        }
        return result.toString();
    }

    private static String processNumber(String number) {
        try {
            double num = Double.parseDouble(number);
            double decimalPart = num - (int) num;
            if (decimalPart == 0) {
                return String.valueOf(Math.round(num));
            }
            return String.valueOf(num);
        } catch (NumberFormatException e) {
            // In case of unexpected parsing errors
            return number;
        }
    }

    public static double roundNonWholeDouble(double number) {
        double decimalPart = number - (int) number;
        if (decimalPart == 0) return Math.round(number);
        return number;
    }

    public static float getFormattedFloat(float value){
        var decimalFormat = new DecimalFormat("#.##");
        return  Float.parseFloat(decimalFormat.format(value));
    }

    public static double singleFormattedDouble(double value){
        var decimalFormat = new DecimalFormat("#.#");
        return roundNonWholeDouble(Double.parseDouble(decimalFormat.format(value)));
    }

    public static double doubleFormattedDouble(double value){
        var decimalFormat = new DecimalFormat("#.##");
        return roundNonWholeDouble(Double.parseDouble(decimalFormat.format(value)));
    }

    public static double tripleFormattedDouble(double value){
        var decimalFormat = new DecimalFormat("#.###");
        return roundNonWholeDouble(Double.parseDouble(decimalFormat.format(value)));
    }

}
