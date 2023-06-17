package br.com.stenox.cxc.utils;

public class StringUtils {

    public static String createArgs(final int index, final String[] args, final String defaultArgs, final boolean color) {
        final StringBuilder sb = new StringBuilder();
        for (int i = index; i < args.length; ++i) {
            sb.append(args[i]).append((i + 1 >= args.length) ? "" : " ");
        }
        if (sb.length() == 0) {
            sb.append(defaultArgs);
        }
        return color ? sb.toString().replace("&", "§") : sb.toString();
    }
}
