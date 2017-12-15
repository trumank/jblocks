package jblocks;

import java.text.DecimalFormat;

import org.jsfml.window.Keyboard.Key;

public class Util {
    public static DecimalFormat df = new DecimalFormat("#.#####");
    public static int mod(int a, int b) {
        return (a % b + b) % b;
    }

    public static double mod(double a, double b) {
        return (a % b + b) % b;
    }

    public static String toString(double d) {
        return df.format(d);
    }

    private static final int POW10[] = new int[20];

    static {
        int j = 1;
        for (int i = 0; i < POW10.length; i++) {
            POW10[i] = j;
            j *= 10;
        }
    }

    public static String toString2(double val) {
        return new FloatingDecimal(val).toJavaFormatString();
        /*StringBuilder sb = new StringBuilder();
        if (val < 0) {
            sb.append('-');
            val = -val;
        }
        int precision = 0;
        for (int i = POW10.length - 1; i >= 0; i--) {
            if ((POW10[i] * val) % 1 != 0) {
                precision = i + 1;
                break;
            }
        }
        int exp = POW10[precision];
        long lval = (long)(val * exp + 0.5);
        sb.append(lval / exp).append('.');
        long fval = lval % exp;
        for (int p = precision - 1; p > 0 && fval < POW10[p]; p--) {
            sb.append('0');
        }
        sb.append(fval);
        return sb.toString();*/
    }

    public static String keyName(Key key) {
        switch (key) {
        case UP:
            return "up arrow";
        case DOWN:
            return "down arrow";
        case RIGHT:
            return "right arrow";
        case LEFT:
            return "left arrow";
        case SPACE:
            return "space";
        case A:
            return "a";
        case B:
            return "b";
        case C:
            return "c";
        case D:
            return "d";
        case E:
            return "e";
        case F:
            return "f";
        case G:
            return "g";
        case H:
            return "h";
        case I:
            return "i";
        case J:
            return "j";
        case K:
            return "k";
        case L:
            return "l";
        case M:
            return "m";
        case N:
            return "n";
        case O:
            return "o";
        case P:
            return "p";
        case Q:
            return "q";
        case R:
            return "r";
        case S:
            return "s";
        case T:
            return "t";
        case U:
            return "u";
        case V:
            return "v";
        case W:
            return "w";
        case X:
            return "x";
        case Y:
            return "y";
        case Z:
            return "z";
        case NUM0:
            return "0";
        case NUM1:
            return "1";
        case NUM2:
            return "2";
        case NUM3:
            return "3";
        case NUM4:
            return "4";
        case NUM5:
            return "5";
        case NUM6:
            return "6";
        case NUM7:
            return "7";
        case NUM8:
            return "8";
        case NUM9:
            return "9";
        default:
            return null;
        }
    }

    public static boolean equals(Value a, Value b) {
        return equals(a, b, true);
    }

    public static boolean equalsWithCase(Value a, Value b) {
        return equals(a, b, false);
    }

    public static boolean equals(Value a, Value b, boolean ignoreCase) {
        if (a == null && b == null) {
            return true;
        } else if (a == null || b == null) {
            return false;
        }
        return a.equals(b, ignoreCase);
    }
}
