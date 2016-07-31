package com.superchat.utils;

public class DataEncryption {
    private final static int ADD_EXTRA = 0x9E3779B9;
    private final static int TOTAL_SPACE  = 32;
    private final static int REMOVE_EXTRA = 0xC6EF3720;

    private int[] S = new int[4];

    /**
    * Initialize the cipher for encryption or decryption.
    * @param key a 16 byte (128-bit) key
    */
    public DataEncryption(byte[] key) {
          if (key == null)
                throw new RuntimeException("Invalid key: Key was null");
          if (key.length < 16)
                throw new RuntimeException("Invalid key: Length was less than 16 bytes");
          for (int off=0, i=0; i<4; i++) {
                S[i] = ((key[off++] & 0xff)) |
                ((key[off++] & 0xff) <<  8) |
                ((key[off++] & 0xff) << 16) |
                ((key[off++] & 0xff) << 24);
          }
    }
   

    /**
    * Encrypt an array of bytes.
    * @param aData the clear text to encrypt
    * @return the encrypted text
    */
    public byte[] rockeTalkEncrypt(byte[] aData) {
          int paddedSize = ((aData.length/8) + (((aData.length%8)==0)?0:1)) * 2;
          int[] buffer = new int[paddedSize + 1];
          buffer[0] = aData.length;
          dataPack(aData, buffer, 1);
          rocketalk(buffer);
          return dataUnpack(buffer, 0, buffer.length * 4);
    }

    /**
    * Decrypt an array of bytes.
    * @param ciper the cipher text to decrypt
    * @return the decrypted text
    */
    public byte[] rockeTalkDecrypt(byte[] crypt) {
          int[] buffer = new int[crypt.length / 4];
          dataPack(crypt, buffer, 0);
          unRockeTalk(buffer);
          return dataUnpack(buffer, 1, buffer[0]);
    }

    private void rocketalk(int[] buf) {
          //assert buf.length % 2 == 1;
          int i, v0, v1, sum, n;
          i = 1;
          while (i<buf.length) {
                n = TOTAL_SPACE;
                v0 = buf[i];   
                v1 = buf[i+1];
                sum = 0;
                while (n-- > 0) {
                      sum += ADD_EXTRA;
                      v0  += ((v1 << 6 ) + S[0] ^ v1) + (sum ^ (v1 >>> 7)) + S[1];
                      v1  += ((v0 << 6 ) + S[2] ^ v0) + (sum ^ (v0 >>> 7)) + S[3];
                }
                buf[i] = v0;
                buf[i+1] = v1;
                i+=2;
          }
    }
   
    private void unRockeTalk(int[] buf) {
          //assert buf.length % 2 == 1;
          int i, v0, v1, sum, n;
          i = 1;
          while (i<buf.length) {
                n = TOTAL_SPACE;
                v0 = buf[i];
                v1 = buf[i+1];
                sum = REMOVE_EXTRA;
                while (n--> 0) {
                      v1  -= ((v0 << 6 ) + S[2] ^ v0) + (sum ^ (v0 >>> 7)) + S[3];
                      v0  -= ((v1 << 6 ) + S[0] ^ v1) + (sum ^ (v1 >>> 7)) + S[1];
                      sum -= ADD_EXTRA;
                }
                buf[i] = v0;
                buf[i+1] = v1;
                i+=2;
          }
    }
   
    private void dataPack(byte[] src, int[] dest, int destOffset) {
          //assert destOffset + (src.length / 4) <= dest.length;
          int i = 0, shift = 24;
          int j = destOffset;
          dest[j] = 0;
          while (i<src.length) {
                dest[j] |= ((src[i] & 0xff) << shift);
                if (shift==0) {
                      shift = 24;
                      j++;
                      if (j<dest.length) dest[j] = 0;
                }
                else {
                      shift -= 8;
                }
                i++;
          }
    }
   
    private byte[] dataUnpack(int[] src, int srcOffset, int destLength) {
          //assert destLength <= (src.length - srcOffset) * 4;
          byte[] dest = new byte[destLength];
          int i = srcOffset;
          int count = 0;
          for (int j = 0; j < destLength; j++) {
                dest[j] = (byte) ((src[i] >> (24 - (8*count))) & 0xff);
                count++;
                if (count == 4) {
                      count = 0;
                      i++;
                }
          }
          return dest;
    }
}