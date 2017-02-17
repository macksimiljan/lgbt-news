package org.lgbt_news.collect.utils;


/**
 * @author max
 */
public class NytDate {

    private int year;
    private int month;
    private int day;

    private NytDate(Builder b) {
        year = b.y;
        month = b.m;
        day = b.d;
    }

    public static class Builder {
        private int y = 2017;
        private int m = 1;
        private int d = 1;

        public Builder year(int val) {
            if (val > 1000 && val < 9999)
                y = val;
            return this;
        }

        public Builder month(int val) {
            if (val > 0 && val <= 12)
                m = val;
            return this;
        }

        public Builder day(int val) {
            if (val > 0 && val <= 31)
                d = val;
            return this;
        }

        public NytDate createDate() {
            return new NytDate(this);
        }
    }

    public NytDate getEarliestDate() {
        return new Builder().year(1851).month(9).day(18).createDate();
    }

    public int getYear() {
        return year;
    }

    public String toString() {
        String date = String.valueOf(year);
        date += (month < 10) ? "0"+month : month;
        date += (day < 10) ? "0"+day : day;

        return date;
    }
}
