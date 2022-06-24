package com.audiocutter.soundfile;

import java.util.*;
import java.io.*;

public class CheapAAC extends CheapSoundFile
{
    public static final int kDINF = 1684631142;
    public static final int kFTYP = 1718909296;
    public static final int kHDLR = 1751411826;
    public static final int kMDAT = 1835295092;
    public static final int kMDHD = 1835296868;
    public static final int kMDIA = 1835297121;
    public static final int kMINF = 1835626086;
    public static final int kMOOV = 1836019574;
    public static final int kMP4A = 1836069985;
    public static final int kMVHD = 1836476516;
    public static final int[] kRequiredAtoms;
    public static final int kSMHD = 1936549988;
    public static final int kSTBL = 1937007212;
    public static final int kSTCO = 1937007471;
    public static final int kSTSC = 1937011555;
    public static final int kSTSD = 1937011556;
    public static final int kSTSZ = 1937011578;
    public static final int kSTTS = 1937011827;
    public static final int[] kSaveDataAtoms;
    public static final int kTKHD = 1953196132;
    public static final int kTRAK = 1953653099;
    private HashMap<Integer, Atom> mAtomMap;
    private int mBitrate;
    private int mChannels;
    private int mFileSize;
    private int[] mFrameGains;
    private int[] mFrameLens;
    private int[] mFrameOffsets;
    private int mMaxGain;
    private int mMdatLength;
    private int mMdatOffset;
    private int mMinGain;
    private int mNumFrames;
    private int mOffset;
    private int mSampleRate;
    private int mSamplesPerFrame;

    static {
        kRequiredAtoms = new int[] { 1684631142, 1751411826, 1835296868, 1835297121, 1835626086, 1836019574, 1836476516, 1936549988, 1937007212, 1937011556, 1937011578, 1937011827, 1953196132, 1953653099 };
        kSaveDataAtoms = new int[] { 1684631142, 1751411826, 1835296868, 1836476516, 1936549988, 1953196132, 1937011556 };
    }

    public static Factory getFactory() {
        return new Factory() {
            @Override
            public CheapSoundFile create() {
                return new CheapAAC();
            }

            @Override
            public String[] getSupportedExtensions() {
                return new String[] { "aac", "m4a" };
            }
        };
    }

    private void parseMp4(final InputStream inputStream, int i) throws IOException {
        while (i > 8) {
            final int mOffset = this.mOffset;
            final byte[] array = new byte[8];
            inputStream.read(array, 0, 8);
            int len;
            if ((len = ((array[0] & 0xFF) << 24 | (array[1] & 0xFF) << 16 | (array[2] & 0xFF) << 8 | (array[3] & 0xFF))) > i) {
                len = i;
            }
            final int n = (array[7] & 0xFF) | ((array[4] & 0xFF) << 24 | (array[5] & 0xFF) << 16 | (array[6] & 0xFF) << 8);
            final Atom atom = new Atom();
            atom.start = this.mOffset;
            atom.len = len;
            this.mAtomMap.put(n, atom);
            this.mOffset += 8;
            if (n != 1836019574 && n != 1953653099 && n != 1835297121 && n != 1835626086 && n != 1937007212) {
                if (n == 1937011578) {
                    this.parseStsz(inputStream, len - 8);
                }
                else if (n == 1937011827) {
                    this.parseStts(inputStream, len - 8);
                }
                else if (n == 1835295092) {
                    this.mMdatOffset = this.mOffset;
                    this.mMdatLength = len - 8;
                }
                else {
                    final int[] kSaveDataAtoms = CheapAAC.kSaveDataAtoms;
                    for (int length = kSaveDataAtoms.length, j = 0; j < length; ++j) {
                        if (kSaveDataAtoms[j] == n) {
                            final int n2 = len - 8;
                            final byte[] data = new byte[n2];
                            inputStream.read(data, 0, n2);
                            this.mOffset += n2;
                            this.mAtomMap.get(n).data = data;
                        }
                    }
                }
            }
            else {
                this.parseMp4(inputStream, len);
            }
            if (n == 1937011556) {
                this.parseMp4aFromStsd();
            }
            i -= len;
            final int n3 = len - (this.mOffset - mOffset);
            if (n3 < 0) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Went over by ");
                sb.append(-n3);
                sb.append(" bytes");
                throw new IOException(sb.toString());
            }
            inputStream.skip(n3);
            this.mOffset += n3;
        }
    }

    @Override
    public void ReadFile(final File file) throws FileNotFoundException, IOException {
        super.ReadFile(file);
        int i = 0;
        this.mChannels = 0;
        this.mSampleRate = 0;
        this.mBitrate = 0;
        this.mSamplesPerFrame = 0;
        this.mNumFrames = 0;
        this.mMinGain = 255;
        this.mMaxGain = 0;
        this.mOffset = 0;
        this.mMdatOffset = -1;
        this.mMdatLength = -1;
        this.mAtomMap = new HashMap<Integer, Atom>();
        this.mFileSize = (int)this.mInputFile.length();
        if (this.mFileSize < 128) {
            throw new IOException("File too small to parse");
        }
        final FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        final byte[] array = new byte[8];
        fileInputStream.read(array, 0, 8);
        if (array[0] != 0 || array[4] != 102 || array[5] != 116 || array[6] != 121 || array[7] != 112) {
            throw new IOException("Unknown file format");
        }
        this.parseMp4(new FileInputStream(this.mInputFile), this.mFileSize);
        if (this.mMdatOffset <= 0 || this.mMdatLength <= 0) {
            throw new IOException("Didn't find mdat");
        }
        final FileInputStream fileInputStream2 = new FileInputStream(this.mInputFile);
        fileInputStream2.skip(this.mMdatOffset);
        this.mOffset = this.mMdatOffset;
        this.parseMdat(fileInputStream2, this.mMdatLength);
        final int[] kRequiredAtoms = CheapAAC.kRequiredAtoms;
        final int length = kRequiredAtoms.length;
        boolean b = false;
        while (i < length) {
            final int n = kRequiredAtoms[i];
            if (!this.mAtomMap.containsKey(n)) {
                final PrintStream out = System.out;
                final StringBuilder sb = new StringBuilder();
                sb.append("Missing atom: ");
                sb.append(this.atomToString(n));
                out.println(sb.toString());
                b = true;
            }
            ++i;
        }
        if (b) {
            throw new IOException("Could not parse MP4 file");
        }
    }

    public void SetAtomData(final int n, final byte[] data) {
        Atom atom;
        if ((atom = this.mAtomMap.get(n)) == null) {
            atom = new Atom();
            this.mAtomMap.put(n, atom);
        }
        atom.len = data.length + 8;
        atom.data = data;
    }

    public void StartAtom(final FileOutputStream fileOutputStream, final int n) throws IOException {
        final int len = this.mAtomMap.get(n).len;
        fileOutputStream.write(new byte[] { (byte)(len >> 24 & 0xFF), (byte)(len >> 16 & 0xFF), (byte)(len >> 8 & 0xFF), (byte)(len & 0xFF), (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF) }, 0, 8);
    }

    public void WriteAtom(final FileOutputStream fileOutputStream, final int n) throws IOException {
        final Atom atom = this.mAtomMap.get(n);
        this.StartAtom(fileOutputStream, n);
        fileOutputStream.write(atom.data, 0, atom.len - 8);
    }

    @Override
    public void WriteFile(final File file, final int n, final int n2) throws IOException {
        file.createNewFile();
        final FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        this.SetAtomData(1718909296, new byte[] { 77, 52, 65, 32, 0, 0, 0, 0, 77, 52, 65, 32, 109, 112, 52, 50, 105, 115, 111, 109, 0, 0, 0, 0 });
        final byte b = (byte)(n2 >> 24 & 0xFF);
        int len = 8;
        final byte b2 = (byte)(n2 >> 16 & 0xFF);
        final byte b3 = (byte)(n2 >> 8 & 0xFF);
        final byte b4 = (byte)(n2 & 0xFF);
        this.SetAtomData(1937011827, new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, b, b2, b3, b4, (byte)(this.mSamplesPerFrame >> 24 & 0xFF), (byte)(this.mSamplesPerFrame >> 16 & 0xFF), (byte)(this.mSamplesPerFrame >> 8 & 0xFF), (byte)(this.mSamplesPerFrame & 0xFF) });
        this.SetAtomData(1937011555, new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, b, b2, b3, b4, 0, 0, 0, 1 });
        final int n3 = n2 * 4;
        final byte[] array = new byte[n3 + 12];
        array[8] = b;
        array[9] = b2;
        array[10] = b3;
        array[11] = b4;
        for (int i = 0; i < n2; ++i) {
            final int n4 = i * 4;
            final int[] mFrameLens = this.mFrameLens;
            final int n5 = n + i;
            array[n4 + 12] = (byte)(mFrameLens[n5] >> 24 & 0xFF);
            array[n4 + 13] = (byte)(this.mFrameLens[n5] >> 16 & 0xFF);
            array[n4 + 14] = (byte)(this.mFrameLens[n5] >> 8 & 0xFF);
            array[n4 + 15] = (byte)(this.mFrameLens[n5] & 0xFF);
        }
        this.SetAtomData(1937011578, array);
        final int n6 = n3 + 144 + this.mAtomMap.get(1937011556).len + this.mAtomMap.get(1937011555).len + this.mAtomMap.get(1836476516).len + this.mAtomMap.get(1953196132).len + this.mAtomMap.get(1835296868).len + this.mAtomMap.get(1751411826).len + this.mAtomMap.get(1936549988).len + this.mAtomMap.get(1684631142).len;
        this.SetAtomData(1937007471, new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, (byte)(n6 >> 24 & 0xFF), (byte)(n6 >> 16 & 0xFF), (byte)(n6 >> 8 & 0xFF), (byte)(n6 & 0xFF) });
        this.mAtomMap.get(1937007212).len = this.mAtomMap.get(1937011556).len + 8 + this.mAtomMap.get(1937011827).len + this.mAtomMap.get(1937011555).len + this.mAtomMap.get(1937011578).len + this.mAtomMap.get(1937007471).len;
        this.mAtomMap.get(1835626086).len = this.mAtomMap.get(1684631142).len + 8 + this.mAtomMap.get(1936549988).len + this.mAtomMap.get(1937007212).len;
        this.mAtomMap.get(1835297121).len = this.mAtomMap.get(1835296868).len + 8 + this.mAtomMap.get(1751411826).len + this.mAtomMap.get(1835626086).len;
        this.mAtomMap.get(1953653099).len = this.mAtomMap.get(1953196132).len + 8 + this.mAtomMap.get(1835297121).len;
        this.mAtomMap.get(1836019574).len = this.mAtomMap.get(1836476516).len + 8 + this.mAtomMap.get(1953653099).len;
        for (int j = 0; j < n2; ++j) {
            len += this.mFrameLens[n + j];
        }
        this.mAtomMap.get(1835295092).len = len;
        this.WriteAtom(fileOutputStream, 1718909296);
        this.StartAtom(fileOutputStream, 1836019574);
        this.WriteAtom(fileOutputStream, 1836476516);
        this.StartAtom(fileOutputStream, 1953653099);
        this.WriteAtom(fileOutputStream, 1953196132);
        this.StartAtom(fileOutputStream, 1835297121);
        this.WriteAtom(fileOutputStream, 1835296868);
        this.WriteAtom(fileOutputStream, 1751411826);
        this.StartAtom(fileOutputStream, 1835626086);
        this.WriteAtom(fileOutputStream, 1684631142);
        this.WriteAtom(fileOutputStream, 1936549988);
        this.StartAtom(fileOutputStream, 1937007212);
        this.WriteAtom(fileOutputStream, 1937011556);
        this.WriteAtom(fileOutputStream, 1937011827);
        this.WriteAtom(fileOutputStream, 1937011555);
        this.WriteAtom(fileOutputStream, 1937011578);
        this.WriteAtom(fileOutputStream, 1937007471);
        this.StartAtom(fileOutputStream, 1835295092);
        int k = 0;
        int n7 = 0;
        while (k < n2) {
            final int[] mFrameLens2 = this.mFrameLens;
            final int n8 = n + k;
            int n9;
            if (mFrameLens2[n8] > (n9 = n7)) {
                n9 = this.mFrameLens[n8];
            }
            ++k;
            n7 = n9;
        }
        final byte[] array2 = new byte[n7];
        int l = 0;
        int n10 = 0;
        while (l < n2) {
            final int[] mFrameOffsets = this.mFrameOffsets;
            final int n11 = n + l;
            final int n12 = mFrameOffsets[n11] - n10;
            final int n13 = this.mFrameLens[n11];
            if (n12 >= 0) {
                int n14 = n10;
                if (n12 > 0) {
                    fileInputStream.skip(n12);
                    n14 = n10 + n12;
                }
                fileInputStream.read(array2, 0, n13);
                fileOutputStream.write(array2, 0, n13);
                n10 = n14 + n13;
            }
            ++l;
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    public String atomToString(final int n) {
        final StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append((char)(n >> 24 & 0xFF));
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(string);
        sb2.append((char)(n >> 16 & 0xFF));
        final String string2 = sb2.toString();
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(string2);
        sb3.append((char)(n >> 8 & 0xFF));
        final String string3 = sb3.toString();
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(string3);
        sb4.append((char)(n & 0xFF));
        return sb4.toString();
    }

    @Override
    public int getAvgBitrateKbps() {
        return this.mFileSize / (this.mNumFrames * this.mSamplesPerFrame);
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
        return "AAC";
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
        return this.mSamplesPerFrame;
    }

    void parseMdat(final InputStream inputStream, final int n) throws IOException {
        final int mOffset = this.mOffset;
        for (int i = 0; i < this.mNumFrames; ++i) {
            this.mFrameOffsets[i] = this.mOffset;
            if (this.mOffset - mOffset + this.mFrameLens[i] > n - 8) {
                this.mFrameGains[i] = 0;
            }
            else {
                this.readFrameAndComputeGain(inputStream, i);
            }
            if (this.mFrameGains[i] < this.mMinGain) {
                this.mMinGain = this.mFrameGains[i];
            }
            if (this.mFrameGains[i] > this.mMaxGain) {
                this.mMaxGain = this.mFrameGains[i];
            }
            if (this.mProgressListener != null && !this.mProgressListener.reportProgress(this.mOffset * 1.0 / this.mFileSize)) {
                return;
            }
        }
    }

    void parseMp4aFromStsd() {
        final byte[] data = this.mAtomMap.get(1937011556).data;
        this.mChannels = ((data[32] & 0xFF) << 8 | (data[33] & 0xFF));
        this.mSampleRate = ((data[41] & 0xFF) | (data[40] & 0xFF) << 8);
    }

    void parseStsz(final InputStream inputStream, int i) throws IOException {
        final byte[] array = new byte[12];
        i = 0;
        inputStream.read(array, 0, 12);
        this.mOffset += 12;
        this.mNumFrames = ((array[11] & 0xFF) | ((array[8] & 0xFF) << 24 | (array[9] & 0xFF) << 16 | (array[10] & 0xFF) << 8));
        this.mFrameOffsets = new int[this.mNumFrames];
        this.mFrameLens = new int[this.mNumFrames];
        this.mFrameGains = new int[this.mNumFrames];
        final byte[] array2 = new byte[this.mNumFrames * 4];
        inputStream.read(array2, 0, this.mNumFrames * 4);
        this.mOffset += this.mNumFrames * 4;
        while (i < this.mNumFrames) {
            final int[] mFrameLens = this.mFrameLens;
            final int n = i * 4;
            mFrameLens[i] = ((array2[n + 3] & 0xFF) | ((array2[n + 0] & 0xFF) << 24 | (array2[n + 1] & 0xFF) << 16 | (array2[n + 2] & 0xFF) << 8));
            ++i;
        }
    }

    void parseStts(final InputStream inputStream, final int n) throws IOException {
        final byte[] array = new byte[16];
        inputStream.read(array, 0, 16);
        this.mOffset += 16;
        this.mSamplesPerFrame = ((array[12] & 0xFF) << 24 | (array[13] & 0xFF) << 16 | (array[14] & 0xFF) << 8 | (array[15] & 0xFF));
    }

    void readFrameAndComputeGain(final InputStream inputStream, int n) throws IOException {
        final int n2 = this.mFrameLens[n];
        final int n3 = 0;
        if (n2 < 4) {
            this.mFrameGains[n] = 0;
            inputStream.skip(this.mFrameLens[n]);
            return;
        }
        final int mOffset = this.mOffset;
        final byte[] array = new byte[4];
        inputStream.read(array, 0, 4);
        this.mOffset += 4;
        switch ((array[0] & 0xE0) >> 5) {
            default: {
                if (n > 0) {
                    this.mFrameGains[n] = this.mFrameGains[n - 1];
                    break;
                }
                this.mFrameGains[n] = 0;
                break;
            }
            case 1: {
                final byte b = array[1];
                final byte b2 = array[1];
                int n4;
                int n5;
                int n6;
                int n7;
                if ((b & 0x60) >> 5 == 2) {
                    n4 = (array[1] & 0xF);
                    n5 = (array[2] & 0xFE) >> 1;
                    n6 = ((array[2] & 0x1) << 1 | (array[3] & 0x80) >> 7);
                    n7 = 25;
                }
                else {
                    n4 = ((array[1] & 0xF) << 2 | (array[2] & 0xC0) >> 6);
                    n5 = -1;
                    n6 = (array[2] & 0x18) >> 3;
                    n7 = 21;
                }
                int n8 = n7;
                if (n6 == 1) {
                    int i = 0;
                    int n9 = 0;
                    while (i < 7) {
                        int n10 = n9;
                        if ((n5 & 1 << i) == 0x0) {
                            n10 = n9 + 1;
                        }
                        ++i;
                        n9 = n10;
                    }
                    n8 = n7 + n4 * (n9 + 1);
                }
                final int n11 = (n8 + 7) / 8 + 1;
                final byte[] array2 = new byte[n11];
                array2[0] = array[0];
                array2[1] = array[1];
                array2[2] = array[2];
                array2[3] = array[3];
                final int n12 = n11 - 4;
                inputStream.read(array2, 4, n12);
                this.mOffset += n12;
                int n13 = 0;
                for (int j = n3; j < 8; ++j) {
                    final int n14 = j + n8;
                    final int n15 = n14 / 8;
                    final int n16 = 7 - n14 % 8;
                    n13 += (array2[n15] & 1 << n16) >> n16 << 7 - j;
                }
                this.mFrameGains[n] = n13;
                break;
            }
            case 0: {
                this.mFrameGains[n] = ((array[0] & 0x1) << 7 | (array[1] & 0xFE) >> 1);
                break;
            }
        }
        n = this.mFrameLens[n] - (this.mOffset - mOffset);
        inputStream.skip(n);
        this.mOffset += n;
    }

    class Atom
    {
        public byte[] data;
        public int len;
        public int start;
    }
}
