package com.audiocutter.soundfile;

import java.io.*;

public class CheapMP3 extends CheapSoundFile
{
    private static int[] BITRATES_MPEG1_L3;
    private static int[] BITRATES_MPEG2_L3;
    private static int[] SAMPLERATES_MPEG1_L3;
    private static int[] SAMPLERATES_MPEG2_L3;
    private int mAvgBitRate;
    private int mBitrateSum;
    private int mFileSize;
    private int[] mFrameGains;
    private int[] mFrameLens;
    private int[] mFrameOffsets;
    private int mGlobalChannels;
    private int mGlobalSampleRate;
    private int mMaxFrames;
    private int mMaxGain;
    private int mMinGain;
    private int mNumFrames;

    static {
        CheapMP3.BITRATES_MPEG1_L3 = new int[] { 0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 0 };
        CheapMP3.BITRATES_MPEG2_L3 = new int[] { 0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, 0 };
        CheapMP3.SAMPLERATES_MPEG1_L3 = new int[] { 44100, 48000, 32000, 0 };
        CheapMP3.SAMPLERATES_MPEG2_L3 = new int[] { 22050, 24000, 16000, 0 };
    }

    public static Factory getFactory() {
        return new Factory() {
            @Override
            public CheapSoundFile create() {
                return new CheapMP3();
            }

            @Override
            public String[] getSupportedExtensions() {
                return new String[] { "mp3" };
            }
        };
    }

    @Override
    public void ReadFile(final File file) throws FileNotFoundException, IOException {
        super.ReadFile(file);
        this.mNumFrames = 0;
        this.mMaxFrames = 64;
        this.mFrameOffsets = new int[this.mMaxFrames];
        this.mFrameLens = new int[this.mMaxFrames];
        this.mFrameGains = new int[this.mMaxFrames];
        this.mBitrateSum = 0;
        this.mMinGain = 255;
        this.mMaxGain = 0;
        this.mFileSize = (int)this.mInputFile.length();
        final FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        final byte[] array = new byte[12];
        int i = 0;
        Label_0095:
        while (true) {
            int j = 0;
            while (i < this.mFileSize - 12) {
                while (j < 12) {
                    j += fileInputStream.read(array, j, 12 - j);
                }
                int n;
                for (n = 0; n < 12 && array[n] != -1; ++n) {}
                if (this.mProgressListener != null && !this.mProgressListener.reportProgress(i * 1.0 / this.mFileSize)) {
                    break;
                }
                if (n > 0) {
                    int n2 = 0;
                    int n3;
                    while (true) {
                        n3 = 12 - n;
                        if (n2 >= n3) {
                            break;
                        }
                        array[n2] = array[n + n2];
                        ++n2;
                    }
                    i += n;
                    j = n3;
                }
                else {
                    int n5;
                    if (array[1] != -6 && array[1] != -5) {
                        if (array[1] != -14 && array[1] != -13) {
                            int n4;
                            for (int k = 0; k < 11; k = n4) {
                                n4 = 1 + k;
                                array[k] = array[n4];
                            }
                            ++i;
                            j = 11;
                            continue;
                        }
                        n5 = 2;
                    }
                    else {
                        n5 = 1;
                    }
                    int n6;
                    int mGlobalSampleRate;
                    if (n5 == 1) {
                        n6 = CheapMP3.BITRATES_MPEG1_L3[(array[2] & 0xF0) >> 4];
                        mGlobalSampleRate = CheapMP3.SAMPLERATES_MPEG1_L3[(array[2] & 0xC) >> 2];
                    }
                    else {
                        n6 = CheapMP3.BITRATES_MPEG2_L3[(array[2] & 0xF0) >> 4];
                        mGlobalSampleRate = CheapMP3.SAMPLERATES_MPEG2_L3[(array[2] & 0xC) >> 2];
                    }
                    if (n6 != 0 && mGlobalSampleRate != 0) {
                        this.mGlobalSampleRate = mGlobalSampleRate;
                        final int n7 = n6 * 144 * 1000 / mGlobalSampleRate + ((array[2] & 0x2) >> 1);
                        int n8;
                        if ((array[3] & 0xC0) == 0xC0) {
                            this.mGlobalChannels = 1;
                            if (n5 == 1) {
                                n8 = ((array[10] & 0x1) << 7) + ((array[11] & 0xFE) >> 1);
                            }
                            else {
                                n8 = ((array[9] & 0x3) << 6) + ((array[10] & 0xFC) >> 2);
                            }
                        }
                        else {
                            this.mGlobalChannels = 2;
                            if (n5 == 1) {
                                n8 = ((array[9] & 0x7F) << 1) + ((array[10] & 0x80) >> 7);
                            }
                            else {
                                n8 = 0;
                            }
                        }
                        this.mBitrateSum += n6;
                        this.mFrameOffsets[this.mNumFrames] = i;
                        this.mFrameLens[this.mNumFrames] = n7;
                        this.mFrameGains[this.mNumFrames] = n8;
                        if (n8 < this.mMinGain) {
                            this.mMinGain = n8;
                        }
                        if (n8 > this.mMaxGain) {
                            this.mMaxGain = n8;
                        }
                        ++this.mNumFrames;
                        if (this.mNumFrames == this.mMaxFrames) {
                            this.mAvgBitRate = this.mBitrateSum / this.mNumFrames;
                            int mMaxFrames;
                            if ((mMaxFrames = this.mFileSize / this.mAvgBitRate * mGlobalSampleRate / 144000 * 11 / 10) < this.mMaxFrames * 2) {
                                mMaxFrames = this.mMaxFrames * 2;
                            }
                            final int[] mFrameOffsets = new int[mMaxFrames];
                            final int[] mFrameLens = new int[mMaxFrames];
                            final int[] mFrameGains = new int[mMaxFrames];
                            for (int l = 0; l < this.mNumFrames; ++l) {
                                mFrameOffsets[l] = this.mFrameOffsets[l];
                                mFrameLens[l] = this.mFrameLens[l];
                                mFrameGains[l] = this.mFrameGains[l];
                            }
                            this.mFrameOffsets = mFrameOffsets;
                            this.mFrameLens = mFrameLens;
                            this.mFrameGains = mFrameGains;
                            this.mMaxFrames = mMaxFrames;
                        }
                        fileInputStream.skip(n7 - 12);
                        i += n7;
                        continue Label_0095;
                    }
                    for (int n9 = 0; n9 < 10; ++n9) {
                        array[n9] = array[2 + n9];
                    }
                    i += 2;
                    j = 10;
                }
            }
            break;
        }
        if (this.mNumFrames > 0) {
            this.mAvgBitRate = this.mBitrateSum / this.mNumFrames;
            return;
        }
        this.mAvgBitRate = 0;
    }

    @Override
    public void WriteFile(final File file, final int n, final int n2) throws IOException {
        file.createNewFile();
        final FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        int i = 0;
        int n3 = 0;
        while (i < n2) {
            final int[] mFrameLens = this.mFrameLens;
            final int n4 = n + i;
            int n5;
            if (mFrameLens[n4] > (n5 = n3)) {
                n5 = this.mFrameLens[n4];
            }
            ++i;
            n3 = n5;
        }
        final byte[] array = new byte[n3];
        int j = 0;
        int n6 = 0;
        while (j < n2) {
            final int[] mFrameOffsets = this.mFrameOffsets;
            final int n7 = n + j;
            final int n8 = mFrameOffsets[n7] - n6;
            final int n9 = this.mFrameLens[n7];
            int n10 = n6;
            if (n8 > 0) {
                fileInputStream.skip(n8);
                n10 = n6 + n8;
            }
            fileInputStream.read(array, 0, n9);
            fileOutputStream.write(array, 0, n9);
            n6 = n10 + n9;
            ++j;
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    @Override
    public int getAvgBitrateKbps() {
        return this.mAvgBitRate;
    }

    @Override
    public int getChannels() {
        return this.mGlobalChannels;
    }

    @Override
    public int getFileSizeBytes() {
        return this.mFileSize;
    }

    @Override
    public String getFiletype() {
        return "MP3";
    }

    @Override
    public int[] getFrameGains() {
        return this.mFrameGains;
    }

    @Override
    public int[] getFrameLens() {
        return this.mFrameLens;
    }

    @Override
    public int[] getFrameOffsets() {
        return this.mFrameOffsets;
    }

    @Override
    public int getNumFrames() {
        return this.mNumFrames;
    }

    @Override
    public int getSampleRate() {
        return this.mGlobalSampleRate;
    }

    @Override
    public int getSamplesPerFrame() {
        return 1152;
    }

    @Override
    public int getSeekableFrameOffset(final int n) {
        if (n <= 0) {
            return 0;
        }
        if (n >= this.mNumFrames) {
            return this.mFileSize;
        }
        return this.mFrameOffsets[n];
    }
}
