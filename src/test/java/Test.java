public class Test {
    public static void main(String[] args) {
        char[] c1 = "0Survival".toCharArray();
        String msg1 = "0Survival".substring(1).trim();
        char[] c2 = "1Survival".toCharArray();
        String msg2 = "1Survival".substring(1).trim();
        System.out.println(c1[0] == 48);
        System.out.println(msg1);
        System.out.println(c2[0] == 49);
        System.out.println(msg2);
    }
}
