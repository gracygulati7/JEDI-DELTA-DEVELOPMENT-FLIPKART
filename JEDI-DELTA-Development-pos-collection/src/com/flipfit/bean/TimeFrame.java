package com.flipfit.bean;

/**
 * Represents a time frame for gym slots
 */
public class TimeFrame {
    private int startHour;
    private int endHour;

    public TimeFrame(int startHour, int endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    @Override
    public String toString() {
        return startHour + ":00 - " + endHour + ":00";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TimeFrame timeFrame = (TimeFrame) obj;
        return startHour == timeFrame.startHour && endHour == timeFrame.endHour;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(startHour, endHour);
    }
}