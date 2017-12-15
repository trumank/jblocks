package jblocks;

public class Tests {
    public static void main(String[] args) {
        // string - string
        check(new Value("asdf").equals(new Value("asdf")));
        check(new Value("asdf").equals(new Value("ASDF")));

        // string - boolean
        check(new Value(true).equals(new Value("true")));
        check(new Value("true").equals(new Value(true)));
        check(!new Value("true").equals(new Value(false)));

        // string - number
        check(new Value("0.9").equals(new Value(0.9)));
        check(new Value(0.9).equals(new Value("0.9")));

        // number - boolean
        check(!new Value(1).equals(new Value(true)));
        check(!new Value(0).equals(new Value(false)));

        check(new Value(4).equals(new Value("4")));

        double val = 4325453654758970987354532d;
        System.out.println(Util.toString(val));
        System.out.println(Util.toString2(val));

        System.out.println("Done!");
    }

    public static void check(boolean flag) {
        if (!flag) {
            throw new RuntimeException("Failed!");
        }
    }
}
