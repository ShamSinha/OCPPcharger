package com.example.chargergui;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

final class SimpleQrCode {
    private static final int VERSION = 8;
    private static final int SIZE = VERSION * 4 + 17;
    private static final int DATA_CODEWORDS = 194;
    private static final int ECC_CODEWORDS_PER_BLOCK = 24;
    private static final int NUM_BLOCKS = 2;
    private static final int MASK = 0;

    private final int[][] modules = new int[SIZE][SIZE];
    private final boolean[][] function = new boolean[SIZE][SIZE];

    private SimpleQrCode() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                modules[y][x] = -1;
            }
        }
    }

    static Bitmap bitmap(String text, int scale, int border) {
        SimpleQrCode qr = encode(text);
        int bitmapSize = (SIZE + border * 2) * scale;
        Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (qr.modules[y][x] == 1) {
                    fillModule(bitmap, (x + border) * scale, (y + border) * scale, scale);
                }
            }
        }
        return bitmap;
    }

    private static void fillModule(Bitmap bitmap, int left, int top, int scale) {
        for (int y = top; y < top + scale; y++) {
            for (int x = left; x < left + scale; x++) {
                bitmap.setPixel(x, y, Color.BLACK);
            }
        }
    }

    private static SimpleQrCode encode(String text) {
        byte[] data;
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            data = text.getBytes();
        }
        if (data.length > 192) {
            throw new IllegalArgumentException("UPI QR payload is too long.");
        }

        SimpleQrCode qr = new SimpleQrCode();
        qr.drawFunctionPatterns();
        qr.drawFormatBits();
        qr.drawVersionBits();
        byte[] codewords = addErrorCorrection(dataCodewords(data));
        qr.drawCodewords(codewords);
        qr.drawFormatBits();
        qr.drawVersionBits();
        qr.fillUnsetModules();
        return qr;
    }

    private static byte[] dataCodewords(byte[] data) {
        BitBuffer bits = new BitBuffer();
        bits.append(0x4, 4);
        bits.append(data.length, 8);
        for (byte value : data) {
            bits.append(value & 0xFF, 8);
        }
        bits.append(0, Math.min(4, DATA_CODEWORDS * 8 - bits.size()));
        while (bits.size() % 8 != 0) {
            bits.append(0, 1);
        }

        List<Integer> bytes = bits.toBytes();
        for (int pad = 0xEC; bytes.size() < DATA_CODEWORDS; pad ^= 0xEC ^ 0x11) {
            bytes.add(pad);
        }

        byte[] result = new byte[DATA_CODEWORDS];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (bytes.get(i) & 0xFF);
        }
        return result;
    }

    private static byte[] addErrorCorrection(byte[] data) {
        byte[] result = new byte[242];
        byte[] divisor = reedSolomonDivisor(ECC_CODEWORDS_PER_BLOCK);
        byte[][] blocks = new byte[NUM_BLOCKS][];
        byte[][] eccBlocks = new byte[NUM_BLOCKS][];
        for (int block = 0; block < NUM_BLOCKS; block++) {
            blocks[block] = new byte[DATA_CODEWORDS / NUM_BLOCKS];
            System.arraycopy(data, block * blocks[block].length, blocks[block], 0, blocks[block].length);
            eccBlocks[block] = reedSolomonRemainder(blocks[block], divisor);
        }

        int index = 0;
        for (int i = 0; i < blocks[0].length; i++) {
            for (int block = 0; block < NUM_BLOCKS; block++) {
                result[index++] = blocks[block][i];
            }
        }
        for (int i = 0; i < ECC_CODEWORDS_PER_BLOCK; i++) {
            for (int block = 0; block < NUM_BLOCKS; block++) {
                result[index++] = eccBlocks[block][i];
            }
        }
        return result;
    }

    private void drawFunctionPatterns() {
        drawFinderPattern(0, 0);
        drawFinderPattern(SIZE - 7, 0);
        drawFinderPattern(0, SIZE - 7);

        int[] align = {6, 24, 42};
        for (int y : align) {
            for (int x : align) {
                if ((x == 6 && y == 6) || (x == 6 && y == 42) || (x == 42 && y == 6)) {
                    continue;
                }
                drawAlignmentPattern(x, y);
            }
        }

        for (int i = 8; i < SIZE - 8; i++) {
            setFunction(i, 6, i % 2 == 0);
            setFunction(6, i, i % 2 == 0);
        }
        setFunction(8, 4 * VERSION + 9, true);
    }

    private void drawFinderPattern(int left, int top) {
        for (int y = -1; y <= 7; y++) {
            for (int x = -1; x <= 7; x++) {
                int xx = left + x;
                int yy = top + y;
                if (xx < 0 || yy < 0 || xx >= SIZE || yy >= SIZE) {
                    continue;
                }
                boolean black = x >= 0 && x <= 6 && y >= 0 && y <= 6
                        && (x == 0 || x == 6 || y == 0 || y == 6 || (x >= 2 && x <= 4 && y >= 2 && y <= 4));
                setFunction(xx, yy, black);
            }
        }
    }

    private void drawAlignmentPattern(int centerX, int centerY) {
        for (int y = -2; y <= 2; y++) {
            for (int x = -2; x <= 2; x++) {
                boolean black = Math.max(Math.abs(x), Math.abs(y)) != 1;
                setFunction(centerX + x, centerY + y, black);
            }
        }
    }

    private void drawCodewords(byte[] codewords) {
        int bitIndex = 0;
        boolean upward = true;
        for (int right = SIZE - 1; right >= 1; right -= 2) {
            if (right == 6) {
                right--;
            }
            for (int vert = 0; vert < SIZE; vert++) {
                int y = upward ? SIZE - 1 - vert : vert;
                for (int j = 0; j < 2; j++) {
                    int x = right - j;
                    if (function[y][x]) {
                        continue;
                    }
                    boolean bit = false;
                    if (bitIndex < codewords.length * 8) {
                        bit = ((codewords[bitIndex >>> 3] >>> (7 - (bitIndex & 7))) & 1) != 0;
                    }
                    modules[y][x] = bit ^ mask(x, y) ? 1 : 0;
                    bitIndex++;
                }
            }
            upward = !upward;
        }
    }

    private void drawFormatBits() {
        int errorCorrectionLow = 1;
        int data = (errorCorrectionLow << 3) | MASK;
        int bits = (data << 10) | bchRemainder(data, 0x537);
        bits ^= 0x5412;

        for (int i = 0; i <= 5; i++) {
            setFunction(8, i, bit(bits, i));
        }
        setFunction(8, 7, bit(bits, 6));
        setFunction(8, 8, bit(bits, 7));
        setFunction(7, 8, bit(bits, 8));
        for (int i = 9; i < 15; i++) {
            setFunction(14 - i, 8, bit(bits, i));
        }

        for (int i = 0; i < 8; i++) {
            setFunction(SIZE - 1 - i, 8, bit(bits, i));
        }
        for (int i = 8; i < 15; i++) {
            setFunction(8, SIZE - 15 + i, bit(bits, i));
        }
        setFunction(8, SIZE - 8, true);
    }

    private void drawVersionBits() {
        int bits = (VERSION << 12) | bchRemainder(VERSION, 0x1F25);
        for (int i = 0; i < 18; i++) {
            boolean bit = bit(bits, i);
            setFunction(SIZE - 11 + i % 3, i / 3, bit);
            setFunction(i / 3, SIZE - 11 + i % 3, bit);
        }
    }

    private void fillUnsetModules() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (modules[y][x] < 0) {
                    modules[y][x] = 0;
                }
            }
        }
    }

    private void setFunction(int x, int y, boolean black) {
        modules[y][x] = black ? 1 : 0;
        function[y][x] = true;
    }

    private static boolean mask(int x, int y) {
        return (x + y) % 2 == 0;
    }

    private static boolean bit(int value, int index) {
        return ((value >>> index) & 1) != 0;
    }

    private static int bchRemainder(int value, int polynomial) {
        value <<= degree(polynomial) - 1;
        while (degree(value) >= degree(polynomial)) {
            value ^= polynomial << (degree(value) - degree(polynomial));
        }
        return value;
    }

    private static int degree(int value) {
        int result = -1;
        while (value != 0) {
            value >>>= 1;
            result++;
        }
        return result;
    }

    private static byte[] reedSolomonDivisor(int degree) {
        byte[] result = new byte[degree];
        result[degree - 1] = 1;
        int root = 1;
        for (int i = 0; i < degree; i++) {
            for (int j = 0; j < result.length; j++) {
                result[j] = (byte) multiply(result[j] & 0xFF, root);
                if (j + 1 < result.length) {
                    result[j] ^= result[j + 1];
                }
            }
            root = multiply(root, 0x02);
        }
        return result;
    }

    private static byte[] reedSolomonRemainder(byte[] data, byte[] divisor) {
        byte[] result = new byte[divisor.length];
        for (byte value : data) {
            int factor = (value ^ result[0]) & 0xFF;
            System.arraycopy(result, 1, result, 0, result.length - 1);
            result[result.length - 1] = 0;
            for (int i = 0; i < result.length; i++) {
                result[i] ^= (byte) multiply(divisor[i] & 0xFF, factor);
            }
        }
        return result;
    }

    private static int multiply(int x, int y) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            if ((y & 1) != 0) {
                result ^= x;
            }
            boolean carry = (x & 0x80) != 0;
            x = (x << 1) & 0xFF;
            if (carry) {
                x ^= 0x1D;
            }
            y >>>= 1;
        }
        return result;
    }

    private static final class BitBuffer {
        private final List<Integer> bits = new ArrayList<>();

        void append(int value, int length) {
            for (int i = length - 1; i >= 0; i--) {
                bits.add((value >>> i) & 1);
            }
        }

        int size() {
            return bits.size();
        }

        List<Integer> toBytes() {
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < bits.size(); i += 8) {
                int value = 0;
                for (int j = 0; j < 8; j++) {
                    value = (value << 1) | bits.get(i + j);
                }
                result.add(value);
            }
            return result;
        }
    }
}
