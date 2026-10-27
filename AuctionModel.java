package com.gambleutils;

import java.math.BigDecimal;
import java.util.Optional;

public final class AuctionModel {
    public final GambleConfig config;
    public String itemName = "";
    public int amount = 1;
    public BigDecimal detectedUnitValue;
    public String manualUnitValue = "";
    public long startNanos;
    public int durationSeconds = 30;
    public boolean active = false;

    public AuctionModel(GambleConfig config) { this.config = config; loadSaved(); }
    public void loadSaved() {
        itemName = config.itemName;
        amount = Math.max(1, config.amount);
        manualUnitValue = config.manualUnitValue;
        durationSeconds = Math.max(1, config.durationSeconds);
    }
    public void save() {
        config.itemName = itemName;
        config.amount = amount;
        config.manualUnitValue = manualUnitValue;
        config.durationSeconds = durationSeconds;
        config.save();
    }
    public Optional<BigDecimal> worth() {
        Optional<BigDecimal> manual = WorthParser.parseNumber(manualUnitValue);
        BigDecimal unit = manual.orElse(detectedUnitValue);
        return unit == null ? Optional.empty() : Optional.of(unit.multiply(BigDecimal.valueOf(amount)));
    }
    public boolean isManual() { return WorthParser.parseNumber(manualUnitValue).isPresent(); }
    public void start() { startNanos = System.nanoTime(); active = true; }
    public void stop() { active = false; }
    public long secondsLeft() {
        if (!active) return durationSeconds;
        long elapsed = (System.nanoTime() - startNanos) / 1_000_000_000L;
        return Math.max(0, durationSeconds - elapsed);
    }
}
