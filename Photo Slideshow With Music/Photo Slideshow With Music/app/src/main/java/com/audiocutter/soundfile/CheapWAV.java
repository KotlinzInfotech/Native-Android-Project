package com.audiocutter.soundfile;

import java.io.*;

public class CheapWAV extends CheapSoundFile
{
    private int mChannels;
    private int mFileSize;
    private int mFrameBytes;
    private int[] mFrameGains;
    private int[] mFrameLens;
    private int[] mFrameOffsets;
    private int mNumFrames;
    private int mOffset;
    private int mSampleRate;

    public static Factory getFactory() {
        return new Factory() {
            @Override
            public CheapSoundFile create() {
                return new CheapWAV();
            }

            @Override
            public String[] getSupportedExtensions() {
                return new String[] { "wav" };
            }
        };
    }

    @Override
    public void ReadFile(final File file) throws FileNotFoundException, IOException {
        super.ReadFile(file);
        this.mFileSize = (int)this.mInputFile.length();
        if (this.mFileSize < 128) {
            throw new IOException("File too small to parse");
        }
        final FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        final byte[] array = new byte[12];
        fileInputStream.read(array, 0, 12);
        this.mOffset += 12;
        if (array[0] == 82 && array[1] == 73 && array[2] == 70 && array[3] == 70 && array[8] == 87 && array[9] == 65 && array[10] == 86 && array[11] == 69) {
            this.mChannels = 0;
            this.mSampleRate = 0;
            while (this.mOffset + 8 <= this.mFileSize) {
                final byte[] array2 = new byte[8];
                fileInputStream.read(array2, 0, 8);
                this.mOffset += 8;
                final int n = (array2[7] & 0xFF) << 24 | (array2[6] & 0xFF) << 16 | (array2[5] & 0xFF) << 8 | (array2[4] & 0xFF);
                if (array2[0] == 102 && array2[1] == 109 && array2[2] == 116 && array2[3] == 32) {
                    if (n < 16 || n > 1024) {
                        throw new IOException("WAV file has bad fmt chunk");
                    }
                    final byte[] array3 = new byte[n];
                    fileInputStream.read(array3, 0, n);
                    this.mOffset += n;
                    final byte b = array3[1];
                    final byte b2 = array3[0];
                    this.mChannels = ((array3[3] & 0xFF) << 8 | (array3[2] & 0xFF));
                    this.mSampleRate = ((array3[4] & 0xFF) | ((array3[7] & 0xFF) << 24 | (array3[6] & 0xFF) << 16 | (array3[5] & 0xFF) << 8));
                    if (((b & 0xFF) << 8 | (b2 & 0xFF)) != 0x1) {
                        throw new IOException("Unsupported WAV file encoding");
                    }
                    continue;
                }
                else if (array2[0] == 100 && array2[1] == 97 && array2[2] == 116 && array2[3] == 97) {
                    if (this.mChannels == 0 || this.mSampleRate == 0) {
                        throw new IOException("Bad WAV file: data chunk before fmt chunk");
                    }
                    this.mFrameBytes = this.mSampleRate * this.mChannels / 50 * 2;
                    this.mNumFrames = (this.mFrameBytes - 1 + n) / this.mFrameBytes;
                    this.mFrameOffsets = new int[this.mNumFrames];
                    this.mFrameLens = new int[this.mNumFrames];
                    this.mFrameGains = new int[this.mNumFrames];
                    final byte[] array4 = new byte[this.mFrameBytes];
                    int i = 0;
                    int n2 = 0;
                    while (i < n) {
                        final int mFrameBytes = this.mFrameBytes;
                        int n3 = i;
                        if (i + mFrameBytes > n) {
                            n3 = n - mFrameBytes;
                        }
                        fileInputStream.read(array4, 0, mFrameBytes);
                        int j = 1;
                        int n4 = 0;
                        while (j < mFrameBytes) {
                            final int abs = Math.abs(array4[j]);
                            int n5;
                            if (abs > (n5 = n4)) {
                                n5 = abs;
                            }
                            j += this.mChannels * 4;
                            n4 = n5;
                        }
                        this.mFrameOffsets[n2] = this.mOffset;
                        this.mFrameLens[n2] = mFrameBytes;
                        this.mFrameGains[n2] = n4;
                        ++n2;
                        this.mOffset += mFrameBytes;
                        i = n3 + mFrameBytes;
                        if (this.mProgressListener != null && !this.mProgressListener.reportProgress(i * 1.0 / n)) {
                            break;
                        }
                    }
                }
                else {
                    fileInputStream.skip(n);
                    this.mOffset += n;
                }
            }
            return;
        }
        throw new IOException("Not a WAV file");
    }

    @Override
    public void WriteFile(final File file, final int n, final int n2) throws IOException {
        file.createNewFile();
        final FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        long n3 = 0L;
        long n4;
        for (int i = 0; i < n2; ++i, n3 += n4) {
            n4 = this.mFrameLens[n + i];
        }
        final long n5 = n3 + 36L;
        final long n6 = this.mSampleRate;
        final long n7 = this.mSampleRate * 2 * this.mChannels;
        fileOutputStream.write(new byte[] { 82, 73, 70, 70, (byte)(n5 & 0xFFL), (byte)(n5 >> 8 & 0xFFL), (byte)(n5 >> 16 & 0xFFL), (byte)(n5 >> 24 & 0xFFL), 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, (byte)this.mChannels, 0, (byte)(n6 & 0xFFL), (byte)(n6 >> 8 & 0xFFL), (byte)(n6 >> 16 & 0xFFL), (byte)(n6 >> 24 & 0xFFL), (byte)(n7 & 0xFFL), (byte)(n7 >> 8 & 0xFFL), (byte)(n7 >> 16 & 0xFFL), (byte)(n7 >> 24 & 0xFFL), (byte)(this.mChannels * 2), 0, 16, 0, 100, 97, 116, 97, (byte)(n3 & 0xFFL), (byte)(n3 >> 8 & 0xFFL), (byte)(n3 >> 16 & 0xFFL), (byte)(n3 >> 24 & 0xFFL) }, 0, 44);
        final byte[] array = new byte[this.mFrameBytes];
        int j = 0;
        int n8 = 0;
        while (j < n2) {
            final int[] mFrameOffsets = this.mFrameOffsets;
            final int n9 = n + j;
            final int n10 = mFrameOffsets[n9] - n8;
            final int n11 = this.mFrameLens[n9];
            if (n10 >= 0) {
                int n12 = n8;
                if (n10 > 0) {
                    fileInputStream.skip(n10);
                    n12 = n8 + n10;
                }
                fileInputStream.read(array, 0, n11);
                fileOutputStream.write(array, 0, n11);
                n8 = n12 + n11;
            }
            ++j;
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    @Override
    public int getAvgBitrateKbps() {
        return this.mSampleRate * this.mChannels * 2 / 1024;
    }

    @Override
    public int getChannels() {
        return this.mChannels;
    }

    @Override
    public int getFileSizeBytes() {
        return this.mFileSize;
    }

    @Override
    public String getFiletype() {
        return "WAV";
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
        return this.mSampleRate;
    }

    @Override
    public int getSamplesPerFrame() {
        return this.mSampleRate / 50;
    }
}
