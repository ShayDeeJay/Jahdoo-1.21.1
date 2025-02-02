package org.jahdoo.challenge;

public record ChoiceData(
    String message,
    Runnable task,
    double multiplier,
    boolean isPositive
){
//    public static ChoiceData getRegen = new ChoiceData("Adds regeneration X for X", () ->{}, 1, true);
    public static ChoiceData getRegen = getAndFormat("Adds regeneration X for X", () ->{}, 1, true);
    public static ChoiceData getZombieResistance = getAndFormat("Adds resistance X to zombies for X", () ->{}, 1, true);

    public static ChoiceData getAndFormat(String message, Runnable task, double multiplier, boolean isPositive) {
        // Replace the first "X" in the message with the multiplier
        String formattedMessage = message.replaceFirst("X", String.valueOf(multiplier));
        return new ChoiceData(formattedMessage, task, multiplier, isPositive);
    }

}
