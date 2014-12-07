public class MoveToFront {
    private static final int R = 256;
    
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] sequence = new char[R];
        for (char i = 0; i < R; i++) {
            sequence[i] = i;
        }
        
        char c, i, tmp1, tmp2;
        while (!BinaryStdIn.isEmpty()) {
            c = BinaryStdIn.readChar();
            // Shifting in 1 pass
            for (i = 0, tmp2 = sequence[0]; c != sequence[i]; i++) {
                tmp1 = sequence[i];
                sequence[i] = tmp2;
                tmp2 = tmp1;
            }
            sequence[i] = tmp2;
            sequence[0] = c;
            BinaryStdOut.write(i);
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] sequence = new char[R];
        for (char i = 0; i < R; i++) {
            sequence[i] = i;
        }
        
        char i, c;
        while (!BinaryStdIn.isEmpty()) {
            i = BinaryStdIn.readChar();
            c = sequence[i];
            // Shifting in 1 pass
            while (i > 0) {
                sequence[i] = sequence[--i];
            }
            sequence[0] = c;
            BinaryStdOut.write(c);
        }
        BinaryStdOut.flush();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            MoveToFront.encode();
        } else if (args[0].equals("+")) {
            MoveToFront.decode();
        } else {
            throw new IllegalArgumentException();
        }
    }
}