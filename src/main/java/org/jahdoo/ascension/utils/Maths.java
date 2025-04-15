package org.jahdoo.ascension.utils;

import java.text.DecimalFormat;
import java.util.Random;

import static org.jahdoo.ascension.utils.Helpers.Random;

public class Maths {

    public static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public static double getPercentage(double multiplier, double baseValue){
        return (multiplier * baseValue) / 100;
    }

    public static double getPercentageTotal(double multiplier, double baseValue){
        return baseValue + (multiplier * baseValue) / 100;
    }

    public static float getFormattedFloat(float value){
        return  Float.parseFloat(FORMAT.format(value));
    }

    public static double singleFormattedDouble(double value){
        var decimalFormat = new DecimalFormat("#.#");
        return roundNonWholeDouble(Double.parseDouble(decimalFormat.format(value)));
    }

    public static double doubleFormattedDouble(double value){
        return roundNonWholeDouble(Double.parseDouble(FORMAT.format(value)));
    }

    public static double tripleFormattedDouble(double value){
        var decimalFormat = new DecimalFormat("#.###");
        return roundNonWholeDouble(Double.parseDouble(decimalFormat.format(value)));
    }

    public static double roundNonWholeDouble(double number) {
        double decimalPart = number - (int) number;
        if (decimalPart == 0) return Math.round(number);
        return number;
    }

    public static String roundNonWholeString(double number) {
        double decimalPart = number - (int) number;
        if (decimalPart == 0) return String.valueOf(Math.round(number));
        return String.valueOf(number);
    }

    public static boolean percentageChance(int percentageChance) {
        if(percentageChance == 0) return false;
        if (percentageChance < 0 || percentageChance > 100) {
            throw new IllegalArgumentException("Percentage chance must be between 0 and 100.");
        }

        int randomValue = Random.nextInt(100) + 1;
        return randomValue <= percentageChance;
    }

    public static boolean percentageChance(int percentageChance, long seed) {
        if(percentageChance == 0) return false;
        if (percentageChance < 0 || percentageChance > 100) {
            throw new IllegalArgumentException("Percentage chance must be between 0 and 100.");
        }

        int randomValue = new Random(seed).nextInt() + 1;
        return randomValue <= percentageChance;
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

    public static double toPercent(double max) {
        if (max == 0) return 100.0;
        if (max <= 0) return 0;
        return Double.parseDouble(FORMAT.format((1.0 / max) * 100));
    }

    public static String ticksToTime(String current) {
        int duration = (int) (Double.parseDouble(current) / 20); // Convert ticks to seconds
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;

        var converter = new StringBuilder();
        if (hours > 0) converter.append(hours).append("h ");
        if (minutes > 0) converter.append(minutes).append("m ");
        if (seconds > 0 || converter.isEmpty()) converter.append(seconds).append("s");

        return converter.toString().trim();
    }

    public static String ticksToTime(String current, boolean showMilliseconds) {
        var totalSeconds = Double.parseDouble(current) / 20.0; // Convert ticks to seconds
        var hours = (int) (totalSeconds / 3600);
        var minutes = (int) ((totalSeconds % 3600) / 60);
        var seconds = (int) totalSeconds;
        var milliseconds = (int) ((totalSeconds - seconds) * 1000);
        var converter = new StringBuilder();

        if (hours > 0) converter.append(hours).append("h ");
        if (minutes > 0) converter.append(minutes).append("m ");

        if(minutes == 0){
            if (showMilliseconds) {
                var secWithMs = seconds + (milliseconds / 1000.0);
                converter.append(String.format("%.3fs", secWithMs));
            } else {
                if (seconds > 0 || converter.isEmpty()) converter.append(seconds).append("s");
            }
        }

        return converter.toString().trim();
    }


    public static String roundNonWholeString(String input) {
        var result = new StringBuilder();
        var numberBuffer = new StringBuilder();

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


}
