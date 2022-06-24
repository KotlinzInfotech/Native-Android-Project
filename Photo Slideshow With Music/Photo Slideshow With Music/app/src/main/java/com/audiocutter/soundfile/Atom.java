package com.audiocutter.soundfile;

class Atom
{
    private Atom[] mChildren;
    private byte[] mData;
    private int mFlags;
    private int mSize;
    private int mType;
    private byte mVersion;

    public Atom(final String s) {
        this.mSize = 8;
        this.mType = this.getTypeInt(s);
        this.mData = null;
        this.mChildren = null;
        this.mVersion = -1;
        this.mFlags = 0;
    }

    public Atom(final String s, final byte mVersion, final int mFlags) {
        this.mSize = 12;
        this.mType = this.getTypeInt(s);
        this.mData = null;
        this.mChildren = null;
        this.mVersion = mVersion;
        this.mFlags = mFlags;
    }

    private int getTypeInt(final String s) {
        return (byte)s.charAt(3) | (0x0 | (byte)s.charAt(0) << 24 | (byte)s.charAt(1) << 16 | (byte)s.charAt(2) << 8);
    }

    private void setSize() {
        int n;
        if (this.mVersion >= 0) {
            n = 12;
        }
        else {
            n = 8;
        }
        int mSize;
        if (this.mData != null) {
            mSize = n + this.mData.length;
        }
        else {
            mSize = n;
            if (this.mChildren != null) {
                final Atom[] mChildren = this.mChildren;
                final int length = mChildren.length;
                int n2 = 0;
                while (true) {
                    mSize = n;
                    if (n2 >= length) {
                        break;
                    }
                    n += mChildren[n2].getSize();
                    ++n2;
                }
            }
        }
        this.mSize = mSize;
    }

    public boolean addChild(final Atom atom) {
        if (this.mData != null) {
            return false;
        }
        if (atom == null) {
            return false;
        }
        int n;
        if (this.mChildren != null) {
            n = this.mChildren.length + 1;
        }
        else {
            n = 1;
        }
        final Atom[] mChildren = new Atom[n];
        if (this.mChildren != null) {
            System.arraycopy(this.mChildren, 0, mChildren, 0, this.mChildren.length);
        }
        mChildren[n - 1] = atom;
        this.mChildren = mChildren;
        this.setSize();
        return true;
    }

    public byte[] getBytes() {
        final byte[] array = new byte[this.mSize];
        array[0] = (byte)(this.mSize >> 24 & 0xFF);
        array[1] = (byte)(this.mSize >> 16 & 0xFF);
        final int mSize = this.mSize;
        int n = 8;
        array[2] = (byte)(mSize >> 8 & 0xFF);
        array[3] = (byte)(this.mSize & 0xFF);
        array[4] = (byte)(this.mType >> 24 & 0xFF);
        array[5] = (byte)(this.mType >> 16 & 0xFF);
        array[6] = (byte)(this.mType >> 8 & 0xFF);
        array[7] = (byte)(this.mType & 0xFF);
        if (this.mVersion >= 0) {
            array[8] = this.mVersion;
            array[9] = (byte)(this.mFlags >> 16 & 0xFF);
            array[10] = (byte)(this.mFlags >> 8 & 0xFF);
            n = 12;
            array[11] = (byte)(this.mFlags & 0xFF);
        }
        if (this.mData != null) {
            System.arraycopy(this.mData, 0, array, n, this.mData.length);
            return array;
        }
        if (this.mChildren != null) {
            final Atom[] mChildren = this.mChildren;
            final int length = mChildren.length;
            final int n2 = 0;
            int n3 = n;
            for (int i = n2; i < length; ++i) {
                final byte[] bytes = mChildren[i].getBytes();
                System.arraycopy(bytes, 0, array, n3, bytes.length);
                n3 += bytes.length;
            }
        }
        return array;
    }

    public Atom getChild(final String s) {
        if (this.mChildren == null) {
            return null;
        }
        final String[] split = s.split("\\.", 2);
        final Atom[] mChildren = this.mChildren;
        final int length = mChildren.length;
        int i = 0;
        while (i < length) {
            final Atom atom = mChildren[i];
            if (atom.getTypeStr().equals(split[0])) {
                if (split.length == 1) {
                    return atom;
                }
                return atom.getChild(split[1]);
            }
            else {
                ++i;
            }
        }
        return null;
    }

    public byte[] getData() {
        return this.mData;
    }

    public int getSize() {
        return this.mSize;
    }

    public int getTypeInt() {
        return this.mType;
    }

    public String getTypeStr() {
        final StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append((char)(byte)(this.mType >> 24 & 0xFF));
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(string);
        sb2.append((char)(byte)(this.mType >> 16 & 0xFF));
        final String string2 = sb2.toString();
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(string2);
        sb3.append((char)(byte)(this.mType >> 8 & 0xFF));
        final String string3 = sb3.toString();
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(string3);
        sb4.append((char)(byte)(this.mType & 0xFF));
        return sb4.toString();
    }

    public boolean setData(final byte[] mData) {
        if (this.mChildren == null && mData != null) {
            this.mData = mData;
            this.setSize();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        final byte[] bytes = this.getBytes();
        String s = "";
        for (int i = 0; i < bytes.length; ++i) {
            final int n = i % 8;
            String string = s;
            if (n == 0) {
                string = s;
                if (i > 0) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(s);
                    sb.append('\n');
                    string = sb.toString();
                }
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(string);
            sb2.append(String.format("0x%02X", bytes[i]));
            final String s2 = s = sb2.toString();
            if (i < bytes.length - 1) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(s2);
                sb3.append(',');
                final String s3 = s = sb3.toString();
                if (n < 7) {
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append(s3);
                    sb4.append(' ');
                    s = sb4.toString();
                }
            }
        }
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(s);
        sb5.append('\n');
        return sb5.toString();
    }
}
