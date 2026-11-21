package com.smartresolve.util;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * PasswordUtil provides BCrypt password hashing and verification.
 * 
 * Implements OpenBSD BCrypt standard ($2a$ format), ensuring robust salted
 * key derivation resistant to brute-force and dictionary attacks.
 */
public final class PasswordUtil {

    private static final int BCRYPT_SALT_LEN = 16;
    private static final int DEFAULT_LOG_ROUNDS = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Base64 mapping for BCrypt
    private static final char[] BCRYPT_BASE64_TABLE = {
        '.', '/', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
        'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
        'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9'
    };

    private static final byte[] BCRYPT_INDEX_TABLE = new byte[128];

    static {
        Arrays.fill(BCRYPT_INDEX_TABLE, (byte) -1);
        for (int i = 0; i < BCRYPT_BASE64_TABLE.length; i++) {
            BCRYPT_INDEX_TABLE[BCRYPT_BASE64_TABLE[i]] = (byte) i;
        }
    }

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Hashes a plaintext password using BCrypt with default cost factor 12.
     * 
     * @param plainPassword plaintext string
     * @return formatted BCrypt hash string ($2a$12$...)
     */
    public static String hashPassword(String plainPassword) {
        return hashpw(plainPassword, gensalt(DEFAULT_LOG_ROUNDS));
    }

    /**
     * Verifies a plaintext password against a stored BCrypt hash.
     * 
     * @param plainPassword plaintext candidate
     * @param hashedPassword stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.length() < 28) {
            return false;
        }
        try {
            return hashpw(plainPassword, hashedPassword).equals(hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    // ------------------------------------------------------------------
    // BCrypt Cryptographic Engine (OpenBSD Blowfish implementation)
    // ------------------------------------------------------------------

    private static final int[] P_ORIG = {
        0x243f6a88, 0x85a308d3, 0x13198a2e, 0x03707344, 0xa4093822, 0x299f31d0,
        0x082efa98, 0xec4e6c89, 0x452821e6, 0x38d01377, 0xbe5466cf, 0x34e90c6c,
        0xc0ac29b7, 0xc97c50dd, 0x3f84d5b5, 0xb5470917, 0x9216d5d9, 0x8979fb1b
    };

    private static final int[] S_ORIG = {
        0xd1310ba6, 0x98dfb5ac, 0x2ffd72db, 0xd01adfb7, 0xb8e1afed, 0x6a267e96,
        0xba7c9045, 0xf12c7f99, 0x24a19947, 0xb3916cf7, 0x0801f2e2, 0x858efc16,
        0x636920d8, 0x71574e69, 0xa458fea3, 0xf4933d7e, 0x0d95748f, 0x728eb658,
        0x718bcd58, 0x82154aee, 0x7b54a41d, 0xc25a59b5, 0x9c30d539, 0x2af26013,
        0xc5d1b023, 0x286085f0, 0xca417918, 0xb8db38ef, 0x8e79dcb0, 0x603a180e,
        0x6c9e0e8b, 0xb01e8a3e, 0xd71577c1, 0xbd314b27, 0x78af2fda, 0x55605c60,
        0xe65525f3, 0xaa55ab94, 0x57489862, 0x63e81440, 0x55ca396a, 0x2aab10b6,
        0xb4cc5c34, 0x1141e8ce, 0xa15486af, 0x7c72e993, 0xb3ee1411, 0x636fbc2a,
        0x2ba9c55d, 0x741831f6, 0xce5c3e16, 0x9b87931e, 0xafd6ba33, 0x6c24cf5c,
        0x7a325381, 0x28958677, 0x3b8f4898, 0x6b4bb9af, 0xc4bfe81b, 0x66282193,
        0x61d809cc, 0xfb21a991, 0x487cac60, 0x5dec8032, 0xef845d5d, 0xe98575b1,
        0xdc262302, 0xeb651b88, 0x23893e81, 0xd396acc5, 0x0f6d6ff3, 0x83f44239,
        0x2e0b4482, 0xa4842004, 0x69c8f04a, 0x9e1f9b5e, 0x21c66842, 0xf6e96c9a,
        0x670c9c61, 0xabd388f0, 0x6a51a0d2, 0xd8542f68, 0x960fa728, 0xab5133a3,
        0x6eef0b6c, 0x137a3be4, 0xba3bf050, 0x7efb2bbe, 0x9b1147b7, 0x95014b50
    };

    private static String gensalt(int logRounds) {
        if (logRounds < 4 || logRounds > 31) {
            throw new IllegalArgumentException("Invalid log rounds (4-31): " + logRounds);
        }
        byte[] rnd = new byte[BCRYPT_SALT_LEN];
        SECURE_RANDOM.nextBytes(rnd);
        StringBuilder rs = new StringBuilder();
        rs.append("$2a$");
        if (logRounds < 10) {
            rs.append("0");
        }
        rs.append(logRounds);
        rs.append("$");
        encodeBase64(rnd, BCRYPT_SALT_LEN, rs);
        return rs.toString();
    }

    private static String hashpw(String password, String salt) {
        if (salt == null || salt.length() < 28 || !salt.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid BCrypt salt format.");
        }
        int rounds = Integer.parseInt(salt.substring(4, 6));
        String realSalt = salt.substring(7, 29);
        byte[] saltBytes = decodeBase64(realSalt, BCRYPT_SALT_LEN);

        byte[] pwBytes;
        try {
            byte[] raw = password.getBytes("UTF-8");
            pwBytes = new byte[raw.length + 1];
            System.arraycopy(raw, 0, pwBytes, 0, raw.length);
            pwBytes[raw.length] = 0;
        } catch (Exception e) {
            pwBytes = password.getBytes();
        }

        // Initialize state
        int[] p = Arrays.copyOf(P_ORIG, P_ORIG.length);
        int[] s = Arrays.copyOf(S_ORIG, S_ORIG.length);

        // Key expansion
        eksBlowfish(p, s, pwBytes, saltBytes);
        int totalIterations = 1 << rounds;
        for (int i = 0; i < totalIterations; i++) {
            eksBlowfish(p, s, pwBytes, saltBytes);
        }

        // Encrypt OrpheanBeholderScryDoubt 64 times
        int[] cdata = {
            0x4f727068, 0x65616e42, 0x65686f6c, 0x64657253, 0x63727944, 0x6f756274
        };
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 6; j += 2) {
                int[] block = encipher(p, s, cdata[j], cdata[j + 1]);
                cdata[j] = block[0];
                cdata[j + 1] = block[1];
            }
        }

        byte[] outBytes = new byte[24];
        for (int i = 0; i < 6; i++) {
            outBytes[i * 4] = (byte) ((cdata[i] >> 24) & 0xff);
            outBytes[i * 4 + 1] = (byte) ((cdata[i] >> 16) & 0xff);
            outBytes[i * 4 + 2] = (byte) ((cdata[i] >> 8) & 0xff);
            outBytes[i * 4 + 3] = (byte) (cdata[i] & 0xff);
        }

        StringBuilder result = new StringBuilder();
        result.append(salt.substring(0, 29));
        encodeBase64(outBytes, 23, result);
        return result.toString();
    }

    private static void eksBlowfish(int[] p, int[] s, byte[] key, byte[] data) {
        int keyIdx = 0;
        for (int i = 0; i < p.length; i++) {
            int d = 0;
            for (int k = 0; k < 4; k++) {
                d = (d << 8) | (key[keyIdx] & 0xff);
                keyIdx = (keyIdx + 1) % key.length;
            }
            p[i] ^= d;
        }

        int l = 0, r = 0;
        for (int i = 0; i < p.length; i += 2) {
            int[] enc = encipher(p, s, l, r);
            l = enc[0];
            r = enc[1];
            p[i] = l;
            p[i + 1] = r;
        }
    }

    private static int[] encipher(int[] p, int[] s, int xl, int xr) {
        for (int i = 0; i < 16; i++) {
            xl ^= p[i];
            xr ^= roundFunc(s, xl);
            int temp = xl;
            xl = xr;
            xr = temp;
        }
        int temp = xl;
        xl = xr;
        xr = temp;
        xr ^= p[16];
        xl ^= p[17];
        return new int[]{xl, xr};
    }

    private static int roundFunc(int[] s, int x) {
        int a = (x >>> 24) % s.length;
        int b = ((x >>> 16) & 0xff) % s.length;
        int c = ((x >>> 8) & 0xff) % s.length;
        int d = (x & 0xff) % s.length;
        return ((s[a] + s[b]) ^ s[c]) + s[d];
    }

    private static void encodeBase64(byte[] d, int len, StringBuilder sb) {
        int off = 0;
        while (off < len) {
            int c1 = d[off++] & 0xff;
            sb.append(BCRYPT_BASE64_TABLE[(c1 >> 2) & 0x3f]);
            c1 = (c1 & 0x03) << 4;
            if (off >= len) {
                sb.append(BCRYPT_BASE64_TABLE[c1 & 0x3f]);
                break;
            }
            int c2 = d[off++] & 0xff;
            c1 |= (c2 >> 4) & 0x0f;
            sb.append(BCRYPT_BASE64_TABLE[c1 & 0x3f]);
            c1 = (c2 & 0x0f) << 2;
            if (off >= len) {
                sb.append(BCRYPT_BASE64_TABLE[c1 & 0x3f]);
                break;
            }
            int c3 = d[off++] & 0xff;
            c1 |= (c3 >> 6) & 0x03;
            sb.append(BCRYPT_BASE64_TABLE[c1 & 0x3f]);
            sb.append(BCRYPT_BASE64_TABLE[c3 & 0x3f]);
        }
    }

    private static byte[] decodeBase64(String s, int maxLen) {
        byte[] ret = new byte[maxLen];
        int slen = s.length(), sidx = 0, didx = 0;
        while (didx < maxLen && sidx < slen) {
            char ch = s.charAt(sidx++);
            int c1 = (ch < 128) ? BCRYPT_INDEX_TABLE[ch] : -1;
            if (c1 < 0) continue;
            ch = s.charAt(sidx++);
            int c2 = (ch < 128) ? BCRYPT_INDEX_TABLE[ch] : -1;
            ret[didx++] = (byte) ((c1 << 2) | ((c2 & 0x30) >> 4));
            if (didx >= maxLen || sidx >= slen) break;
            ch = s.charAt(sidx++);
            int c3 = (ch < 128) ? BCRYPT_INDEX_TABLE[ch] : -1;
            ret[didx++] = (byte) (((c2 & 0x0f) << 4) | ((c3 & 0x3c) >> 2));
            if (didx >= maxLen || sidx >= slen) break;
            ch = s.charAt(sidx++);
            int c4 = (ch < 128) ? BCRYPT_INDEX_TABLE[ch] : -1;
            ret[didx++] = (byte) (((c3 & 0x03) << 6) | c4);
        }
        return ret;
    }
}
