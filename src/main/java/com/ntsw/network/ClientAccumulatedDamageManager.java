// ClientAccumulatedDamageManager.java

package com.ntsw.network;

public class ClientAccumulatedDamageManager {
    private static double accumulatedDamage = 0.0;

    public static double getAccumulatedDamage() {
        return accumulatedDamage;
    }

    public static void setAccumulatedDamage(double damage) {
        accumulatedDamage = damage;
    }
}
