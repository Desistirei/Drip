package br.com.stenox.cxc.utils;

public class Formatter {

    public static String toTime(int time) {
        int m = time / 60;
        int h = m / 60;
        int s = time % 60;

        StringBuilder builder = new StringBuilder();

        if (h > 0){
            builder.append(h).append(":");
        }

        if (m > 0) {
            if (h > 0) {
                m = m % 60;
            }
            builder.append(m).append(":");

        }
        if (s >= 0 && h <= 0) {
            if (m <= 0)
                builder.append("0:");

            builder.append(s <= 9 ? "0" : "").append(s);
        }

        return builder.toString();
    }

    public static String toTimeLong(int time) {
        int m = time / 60;
        int h = m / 60;
        int s = time % 60;

        StringBuilder builder = new StringBuilder();

        if (h > 0){
            builder.append(h);
            builder.append(h == 1 ? " hora" : " horas");
        }

        if (m > 0) {
            if (h > 0) {
                builder.append(" e ");

                m = m % 60;
            }
            builder.append(m);
            builder.append(m == 1 ? " minuto" : " minutos");

        }
        if (s > 0 && h <= 0) {
            if (m > 0)
                builder.append(" e ");

            builder.append(s);
            builder.append(s == 1 ? " segundo" : " segundos");
        }

        return builder.toString();
    }
}
