package com.gambleutils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads explicit "Sell Value" labels only; avoids treating unrelated prices as sell values. */
public final class WorthParser {
    private static final Pattern SELL = Pattern.compile("(?i)(?:sell\\s*(?:value|price|worth)|worth\\s*to\\s*sell)\\s*[:=]\\s*\\$?\\s*([\\d,]+(?:\\.\\d+)?)([kmbt]?)");
    private WorthParser() {}

    public static Optional<BigDecimal> parsePerItem(String text) {
        if (text == null) return Optional.empty();
        Matcher m = SELL.matcher(text.replace('§', ' ').replaceAll("(?i) [0-9a-fk-or]", ""));
        if (!m.find()) return Optional.empty();
        try {
            BigDecimal base = new BigDecimal(m.group(1).replace(",", ""));
            BigDecimal mult = switch (m.group(2).toLowerCase()) {
                case "k" -> new BigDecimal("1000");
                case "m" -> new BigDecimal("1000000");
                case "b" -> new BigDecimal("1000000000");
                case "t" -> new BigDecimal("1000000000000");
                default -> BigDecimal.ONE;
            };
            return Optional.of(base.multiply(mult));
        } catch (NumberFormatException ex) { return Optional.empty(); }
    }
    public static Optional<BigDecimal> parseNumber(String text) {
        if (text == null || text.isBlank()) return Optional.empty();
        String value = text.trim().replace(",", "").replace("$", "");
        Matcher m = Pattern.compile("(?i)^([\\d]+(?:\\.\\d+)?)([kmbt]?)$").matcher(value);
        if (!m.matches()) return Optional.empty();
        try {
            return parsePerItem("Sell Value: " + value);
        } catch (Exception e) { return Optional.empty(); }
    }
}
