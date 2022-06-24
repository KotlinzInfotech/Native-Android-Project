package com.audiocutter.soundfile;

public class WAVHeader
{
    private int mChannels;
    private byte[] mHeader;
    private int mNumBytesPerSample;
    private int mNumSamples;
    private int mSampleRate;

    public WAVHeader(final int mSampleRate, final int mChannels, final int mNumSamples) {
        this.mSampleRate = mSampleRate;
        this.mChannels = mChannels;
        this.mNumSamples = mNumSamples;
        this.mNumBytesPerSample = this.mChannels * 2;
        this.mHeader = null;
        this.setHeader();
    }

    public static byte[] getWAVHeader(final int n, final int n2, final int n3) {
        return new WAVHeader(n, n2, n3).mHeader;
    }

    private void setHeader() {
        final byte[] mHeader = new byte[46];
        System.arraycopy(new byte[] { 82, 73, 70, 70 }, 0, mHeader, 0, 4);
        final int n = this.mNumSamples * this.mNumBytesPerSample + 36;
        mHeader[4] = (byte)(n & 0xFF);
        mHeader[5] = (byte)(n >> 8 & 0xFF);
        mHeader[6] = (byte)(n >> 16 & 0xFF);
        mHeader[7] = (byte)(n >> 24 & 0xFF);
        System.arraycopy(new byte[] { 87, 65, 86, 69 }, 0, mHeader, 8, 4);
        System.arraycopy(new byte[] { 102, 109, 116, 32 }, 0, mHeader, 12, 4);
        System.arraycopy(new byte[] { 16, 0, 0, 0 }, 0, mHeader, 16, 4);
        System.arraycopy(new byte[] { 1, 0 }, 0, mHeader, 20, 2);
        mHeader[22] = (byte)(this.mChannels & 0xFF);
        mHeader[23] = (byte)(this.mChannels >> 8 & 0xFF);
        mHeader[24] = (byte)(this.mSampleRate & 0xFF);
        mHeader[25] = (byte)(this.mSampleRate >> 8 & 0xFF);
        mHeader[26] = (byte)(this.mSampleRate >> 16 & 0xFF);
        mHeader[27] = (byte)(this.mSampleRate >> 24 & 0xFF);
        final int n2 = this.mSampleRate * this.mNumBytesPerSample;
        mHeader[28] = (byte)(n2 & 0xFF);
        mHeader[29] = (byte)(n2 >> 8 & 0xFF);
        mHeader[30] = (byte)(n2 >> 16 & 0xFF);
        mHeader[31] = (byte)(n2 >> 24 & 0xFF);
        mHeader[32] = (byte)(this.mNumBytesPerSample & 0xFF);
        mHeader[33] = (byte)(this.mNumBytesPerSample >> 8 & 0xFF);
        System.arraycopy(new byte[] { 16, 0 }, 0, mHeader, 34, 2);
        System.arraycopy(new byte[] { 100, 97, 116, 97 }, 0, mHeader, 36, 4);
        final int n3 = this.mNumSamples * this.mNumBytesPerSample;
        mHeader[40] = (byte)(n3 & 0xFF);
        mHeader[41] = (byte)(n3 >> 8 & 0xFF);
        mHeader[42] = (byte)(n3 >> 16 & 0xFF);
        mHeader[43] = (byte)(n3 >> 24 & 0xFF);
        this.mHeader = mHeader;
    }

    public byte[] getWAVHeader() {
        return this.mHeader;
    }

    @Override
    public String toString() {
        if (this.mHeader == null) {
            return "";
        }
        final byte[] mHeader = this.mHeader;
        final int length = mHeader.length;
        String string = "";
        int i = 0;
        int n = 0;
        while (i < length) {
            final byte b = mHeader[i];
            final boolean b2 = n > 0 && n % 32 == 0;
            final boolean b3 = n > 0 && n % 4 == 0 && !b2;
            String string2 = string;
            if (b2) {
                final StringBuilder sb = new StringBuilder();
                sb.append(string);
                sb.append('\n');
                string2 = sb.toString();
            }
            String string3 = string2;
            if (b3) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(string2);
                sb2.append(' ');
                string3 = sb2.toString();
            }
            final StringBuilder sb3 = new StringBuilder();
            sb3.append(string3);
            sb3.append(String.format("%02X", b));
            string = sb3.toString();
            ++n;
            ++i;
        }
        return string;
    }
}
