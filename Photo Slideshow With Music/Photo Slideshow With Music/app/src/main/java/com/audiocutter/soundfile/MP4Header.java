package com.audiocutter.soundfile;

public class MP4Header
{
    private int mBitrate;
    private int mChannels;
    private byte[] mDurationMS;
    private int[] mFrameSize;
    private byte[] mHeader;
    private int mMaxFrameSize;
    private byte[] mNumSamples;
    private int mSampleRate;
    private byte[] mTime;
    private int mTotSize;

    public MP4Header(int i, int mChannels, final int[] mFrameSize, int mBitrate) {
        if (mFrameSize == null || mFrameSize.length < 2) {
            return;
        }
        if (mFrameSize[0] != 2) {
            return;
        }
        this.mSampleRate = i;
        this.mChannels = mChannels;
        this.mFrameSize = mFrameSize;
        this.mBitrate = mBitrate;
        this.mMaxFrameSize = this.mFrameSize[0];
        this.mTotSize = this.mFrameSize[0];
        for (i = 1; i < this.mFrameSize.length; ++i) {
            if (this.mMaxFrameSize < this.mFrameSize[i]) {
                this.mMaxFrameSize = this.mFrameSize[i];
            }
            this.mTotSize += this.mFrameSize[i];
        }
        final long n = System.currentTimeMillis() / 1000L + 2082758400L;
        (this.mTime = new byte[4])[0] = (byte)(n >> 24 & 0xFFL);
        this.mTime[1] = (byte)(n >> 16 & 0xFFL);
        this.mTime[2] = (byte)(n >> 8 & 0xFFL);
        this.mTime[3] = (byte)(n & 0xFFL);
        mBitrate = (mFrameSize.length - 1) * 1024;
        final int n2 = mBitrate * 1000;
        mChannels = (i = n2 / this.mSampleRate);
        if (n2 % this.mSampleRate > 0) {
            i = mChannels + 1;
        }
        this.mNumSamples = new byte[] { (byte)(mBitrate >> 26 & 0xFF), (byte)(mBitrate >> 16 & 0xFF), (byte)(mBitrate >> 8 & 0xFF), (byte)(mBitrate & 0xFF) };
        this.mDurationMS = new byte[] { (byte)(i >> 26 & 0xFF), (byte)(i >> 16 & 0xFF), (byte)(i >> 8 & 0xFF), (byte)(i & 0xFF) };
        this.setHeader();
    }

    private Atom getDINFAtom() {
        final Atom atom = new Atom("dinf");
        atom.addChild(this.getDREFAtom());
        return atom;
    }

    private Atom getDREFAtom() {
        final Atom atom = new Atom("dref", (byte)0, 0);
        final byte[] bytes = this.getURLAtom().getBytes();
        final byte[] data = new byte[bytes.length + 4];
        data[3] = 1;
        System.arraycopy(bytes, 0, data, 4, bytes.length);
        atom.setData(data);
        return atom;
    }

    private Atom getESDSAtom() {
        final Atom atom = new Atom("esds", (byte)0, 0);
        atom.setData(this.getESDescriptor());
        return atom;
    }

    private byte[] getESDescriptor() {
        final int[] array2;
        final int[] array = array2 = new int[13];
        array2[0] = 96000;
        array2[1] = 88200;
        array2[2] = 64000;
        array2[3] = 48000;
        array2[4] = 44100;
        array2[5] = 32000;
        array2[6] = 24000;
        array2[7] = 22050;
        array2[8] = 16000;
        array2[9] = 12000;
        array2[10] = 11025;
        array2[11] = 8000;
        array2[12] = 7350;
        final byte[] array4;
        final byte[] array3 = array4 = new byte[5];
        array4[0] = 3;
        array4[1] = 25;
        array4[2] = 0;
        array4[4] = (array4[3] = 0);
        final int n = 4;
        final byte[] array6;
        final byte[] array5 = array6 = new byte[4];
        array6[0] = 4;
        array6[1] = 17;
        array6[2] = 64;
        array6[3] = 21;
        final byte[] array8;
        final byte[] array7 = array8 = new byte[4];
        array8[0] = 5;
        array8[1] = 2;
        array8[2] = 16;
        array8[3] = 0;
        final byte[] array10;
        final byte[] array9 = array10 = new byte[3];
        array10[0] = 6;
        array10[1] = 1;
        array10[2] = 2;
        int i;
        for (i = 768; i < this.mMaxFrameSize * 2; i += 256) {}
        final byte[] array11 = new byte[array5[1] + 2];
        System.arraycopy(array5, 0, array11, 0, array5.length);
        final int length = array5.length;
        final int n2 = length + 1;
        array11[length] = (byte)(i >> 16 & 0xFF);
        final int n3 = n2 + 1;
        array11[n2] = (byte)(i >> 8 & 0xFF);
        final int n4 = n3 + 1;
        array11[n3] = (byte)(i & 0xFF);
        final int n5 = n4 + 1;
        array11[n4] = (byte)(this.mBitrate >> 24 & 0xFF);
        final int n6 = n5 + 1;
        array11[n5] = (byte)(this.mBitrate >> 16 & 0xFF);
        final int n7 = n6 + 1;
        array11[n6] = (byte)(this.mBitrate >> 8 & 0xFF);
        final int n8 = n7 + 1;
        array11[n7] = (byte)(this.mBitrate & 0xFF);
        final int n9 = n8 + 1;
        array11[n8] = (byte)(this.mBitrate >> 24 & 0xFF);
        final int n10 = n9 + 1;
        array11[n9] = (byte)(this.mBitrate >> 16 & 0xFF);
        final int n11 = n10 + 1;
        array11[n10] = (byte)(this.mBitrate >> 8 & 0xFF);
        array11[n11] = (byte)(this.mBitrate & 0xFF);
        int n12;
        for (n12 = 0; n12 < array.length && array[n12] != this.mSampleRate; ++n12) {}
        if (n12 == array.length) {
            n12 = n;
        }
        array7[2] |= (byte)(n12 >> 1 & 0x7);
        array7[3] |= (byte)((n12 & 0x1) << 7 | (this.mChannels & 0xF) << 3);
        System.arraycopy(array7, 0, array11, n11 + 1, array7.length);
        final byte[] array12 = new byte[array3[1] + 2];
        System.arraycopy(array3, 0, array12, 0, array3.length);
        final int length2 = array3.length;
        System.arraycopy(array11, 0, array12, length2, array11.length);
        System.arraycopy(array9, 0, array12, length2 + array11.length, array9.length);
        return array12;
    }

    private Atom getFTYPAtom() {
        final Atom atom = new Atom("ftyp");
        atom.setData(new byte[] { 77, 52, 65, 32, 0, 0, 0, 0, 77, 52, 65, 32, 109, 112, 52, 50, 105, 115, 111, 109 });
        return atom;
    }

    private Atom getHDLRAtom() {
        final Atom atom = new Atom("hdlr", (byte)0, 0);
        atom.setData(new byte[] { 0, 0, 0, 0, 115, 111, 117, 110, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 111, 117, 110, 100, 72, 97, 110, 100, 108, 101, 0 });
        return atom;
    }

    private Atom getMDHDAtom() {
        final Atom atom = new Atom("mdhd", (byte)0, 0);
        atom.setData(new byte[] { this.mTime[0], this.mTime[1], this.mTime[2], this.mTime[3], this.mTime[0], this.mTime[1], this.mTime[2], this.mTime[3], (byte)(this.mSampleRate >> 24), (byte)(this.mSampleRate >> 16), (byte)(this.mSampleRate >> 8), (byte)this.mSampleRate, this.mNumSamples[0], this.mNumSamples[1], this.mNumSamples[2], this.mNumSamples[3], 0, 0, 0, 0 });
        return atom;
    }

    private Atom getMDIAAtom() {
        final Atom atom = new Atom("mdia");
        atom.addChild(this.getMDHDAtom());
        atom.addChild(this.getHDLRAtom());
        atom.addChild(this.getMINFAtom());
        return atom;
    }

    private Atom getMINFAtom() {
        final Atom atom = new Atom("minf");
        atom.addChild(this.getSMHDAtom());
        atom.addChild(this.getDINFAtom());
        atom.addChild(this.getSTBLAtom());
        return atom;
    }

    private Atom getMOOVAtom() {
        final Atom atom = new Atom("moov");
        atom.addChild(this.getMVHDAtom());
        atom.addChild(this.getTRAKAtom());
        return atom;
    }

    private Atom getMP4AAtom() {
        final Atom atom = new Atom("mp4a");
        final byte[] array = { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, (byte)(this.mChannels >> 8), (byte)this.mChannels, 0, 16, 0, 0, 0, 0, (byte)(this.mSampleRate >> 8), (byte)this.mSampleRate, 0, 0 };
        final byte[] bytes = this.getESDSAtom().getBytes();
        final byte[] data = new byte[array.length + bytes.length];
        System.arraycopy(array, 0, data, 0, array.length);
        System.arraycopy(bytes, 0, data, array.length, bytes.length);
        atom.setData(data);
        return atom;
    }

    public static byte[] getMP4Header(final int n, final int n2, final int[] array, final int n3) {
        return new MP4Header(n, n2, array, n3).mHeader;
    }

    private Atom getMVHDAtom() {
        final Atom atom = new Atom("mvhd", (byte)0, 0);
        atom.setData(new byte[] { this.mTime[0], this.mTime[1], this.mTime[2], this.mTime[3], this.mTime[0], this.mTime[1], this.mTime[2], this.mTime[3], 0, 0, 3, -24, this.mDurationMS[0], this.mDurationMS[1], this.mDurationMS[2], this.mDurationMS[3], 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2 });
        return atom;
    }

    private Atom getSMHDAtom() {
        final Atom atom = new Atom("smhd", (byte)0, 0);
        atom.setData(new byte[] { 0, 0, 0, 0 });
        return atom;
    }

    private Atom getSTBLAtom() {
        final Atom atom = new Atom("stbl");
        atom.addChild(this.getSTSDAtom());
        atom.addChild(this.getSTTSAtom());
        atom.addChild(this.getSTSCAtom());
        atom.addChild(this.getSTSZAtom());
        atom.addChild(this.getSTCOAtom());
        return atom;
    }

    private Atom getSTCOAtom() {
        final Atom atom = new Atom("stco", (byte)0, 0);
        atom.setData(new byte[] { 0, 0, 0, 1, 0, 0, 0, 0 });
        return atom;
    }

    private Atom getSTSCAtom() {
        final Atom atom = new Atom("stsc", (byte)0, 0);
        final int length = this.mFrameSize.length;
        atom.setData(new byte[] { 0, 0, 0, 1, 0, 0, 0, 1, (byte)(length >> 24 & 0xFF), (byte)(length >> 16 & 0xFF), (byte)(length >> 8 & 0xFF), (byte)(length & 0xFF), 0, 0, 0, 1 });
        return atom;
    }

    private Atom getSTSDAtom() {
        final Atom atom = new Atom("stsd", (byte)0, 0);
        final byte[] bytes = this.getMP4AAtom().getBytes();
        final byte[] data = new byte[bytes.length + 4];
        data[3] = 1;
        System.arraycopy(bytes, 0, data, 4, bytes.length);
        atom.setData(data);
        return atom;
    }

    private Atom getSTSZAtom() {
        int i = 0;
        final Atom atom = new Atom("stsz", (byte)0, 0);
        final int length = this.mFrameSize.length;
        int n = 8;
        final byte[] data = new byte[length * 4 + 8];
        data[1] = (data[0] = 0);
        data[3] = (data[2] = 0);
        data[4] = (byte)(length >> 24 & 0xFF);
        data[5] = (byte)(length >> 16 & 0xFF);
        data[6] = (byte)(length >> 8 & 0xFF);
        data[7] = (byte)(length & 0xFF);
        for (int[] mFrameSize = this.mFrameSize; i < mFrameSize.length; ++i) {
            final int n2 = mFrameSize[i];
            final int n3 = n + 1;
            data[n] = (byte)(n2 >> 24 & 0xFF);
            final int n4 = n3 + 1;
            data[n3] = (byte)(n2 >> 16 & 0xFF);
            final int n5 = n4 + 1;
            data[n4] = (byte)(n2 >> 8 & 0xFF);
            n = n5 + 1;
            data[n5] = (byte)(n2 & 0xFF);
        }
        atom.setData(data);
        return atom;
    }

    private Atom getSTTSAtom() {
        final Atom atom = new Atom("stts", (byte)0, 0);
        final int n = this.mFrameSize.length - 1;
        atom.setData(new byte[] { 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 0, (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF), 0, 0, 4, 0 });
        return atom;
    }

    private Atom getTKHDAtom() {
        final Atom atom = new Atom("tkhd", (byte)0, 7);
        atom.setData(new byte[] { this.mTime[0], this.mTime[1], this.mTime[2], this.mTime[3], this.mTime[0], this.mTime[1], this.mTime[2], this.mTime[3], 0, 0, 0, 1, 0, 0, 0, 0, this.mDurationMS[0], this.mDurationMS[1], this.mDurationMS[2], this.mDurationMS[3], 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        return atom;
    }

    private Atom getTRAKAtom() {
        final Atom atom = new Atom("trak");
        atom.addChild(this.getTKHDAtom());
        atom.addChild(this.getMDIAAtom());
        return atom;
    }

    private Atom getURLAtom() {
        return new Atom("url ", (byte)0, 1);
    }

    private void setHeader() {
        final Atom ftypAtom = this.getFTYPAtom();
        final Atom moovAtom = this.getMOOVAtom();
        final Atom atom = new Atom("mdat");
        final Atom child = moovAtom.getChild("trak.mdia.minf.stbl.stco");
        if (child == null) {
            this.mHeader = null;
            return;
        }
        final byte[] data = child.getData();
        final int n = ftypAtom.getSize() + moovAtom.getSize() + atom.getSize();
        final int n2 = data.length - 4;
        final int n3 = n2 + 1;
        data[n2] = (byte)(n >> 24 & 0xFF);
        final int n4 = n3 + 1;
        data[n3] = (byte)(n >> 16 & 0xFF);
        data[n4] = (byte)(n >> 8 & 0xFF);
        data[n4 + 1] = (byte)(n & 0xFF);
        final byte[] mHeader = new byte[n];
        final Atom[] array = { ftypAtom, moovAtom, atom };
        final int length = array.length;
        int i = 0;
        int n5 = 0;
        while (i < length) {
            final byte[] bytes = array[i].getBytes();
            System.arraycopy(bytes, 0, mHeader, n5, bytes.length);
            n5 += bytes.length;
            ++i;
        }
        final int n6 = this.mTotSize + 8;
        final int n7 = n5 - 8;
        final int n8 = n7 + 1;
        mHeader[n7] = (byte)(n6 >> 24 & 0xFF);
        final int n9 = n8 + 1;
        mHeader[n8] = (byte)(n6 >> 16 & 0xFF);
        mHeader[n9] = (byte)(n6 >> 8 & 0xFF);
        mHeader[n9 + 1] = (byte)(n6 & 0xFF);
        this.mHeader = mHeader;
    }

    public byte[] getMP4Header() {
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
