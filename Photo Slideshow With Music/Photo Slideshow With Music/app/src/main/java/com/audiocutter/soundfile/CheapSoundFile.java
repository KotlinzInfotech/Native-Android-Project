package com.audiocutter.soundfile;

import java.util.*;
import java.io.*;
import java.security.*;

public class CheapSoundFile
{
    private static final char[] HEX_CHARS;
    static HashMap<String, Factory> sExtensionMap;
    static Factory[] sSubclassFactories;
    static ArrayList<String> sSupportedExtensions;
    protected File mInputFile;
    protected ProgressListener mProgressListener;

    static {
        CheapSoundFile.sSubclassFactories = new Factory[] { CheapAAC.getFactory(), CheapAMR.getFactory(), CheapMP3.getFactory(), CheapWAV.getFactory() };
        CheapSoundFile.sSupportedExtensions = new ArrayList<String>();
        CheapSoundFile.sExtensionMap = new HashMap<String, Factory>();
        final Factory[] sSubclassFactories = CheapSoundFile.sSubclassFactories;
        for (int length = sSubclassFactories.length, i = 0; i < length; ++i) {
            final Factory factory = sSubclassFactories[i];
            final String[] supportedExtensions = factory.getSupportedExtensions();
            for (int length2 = supportedExtensions.length, j = 0; j < length2; ++j) {
                final String s = supportedExtensions[j];
                CheapSoundFile.sSupportedExtensions.add(s);
                CheapSoundFile.sExtensionMap.put(s, factory);
            }
        }
        HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }

    protected CheapSoundFile() {
        this.mProgressListener = null;
        this.mInputFile = null;
    }

    public static String bytesToHex(final byte[] array) {
        final char[] array2 = new char[array.length * 2];
        int i = 0;
        int n = 0;
        while (i < array.length) {
            final int n2 = n + 1;
            array2[n] = CheapSoundFile.HEX_CHARS[array[i] >>> 4 & 0xF];
            n = n2 + 1;
            array2[n2] = CheapSoundFile.HEX_CHARS[array[i] & 0xF];
            ++i;
        }
        return new String(array2);
    }

    public static CheapSoundFile create(final String s, final ProgressListener progressListener) throws FileNotFoundException, IOException {
        final File file = new File(s);
        if (!file.exists()) {
            throw new FileNotFoundException(s);
        }
        final String[] split = file.getName().toLowerCase(Locale.ENGLISH).split("\\.");
        if (split.length < 2) {
            return null;
        }
        final Factory factory = CheapSoundFile.sExtensionMap.get(split[split.length - 1]);
        if (factory == null) {
            return null;
        }
        final CheapSoundFile create = factory.create();
        create.setProgressListener(progressListener);
        create.ReadFile(file);
        return create;
    }

    public static String[] getSupportedExtensions() {
        return CheapSoundFile.sSupportedExtensions.toArray(new String[CheapSoundFile.sSupportedExtensions.size()]);
    }

    public static boolean isFilenameSupported(final String s) {
        final String[] split = s.toLowerCase().split("\\.");
        return split.length >= 2 && CheapSoundFile.sExtensionMap.containsKey(split[split.length - 1]);
    }

    public void ReadFile(final File mInputFile) throws FileNotFoundException, IOException {
        this.mInputFile = mInputFile;
    }

    public void WriteFile(final File file, final int n, final int n2) throws IOException {
    }

    public String computeMd5OfFirst10Frames() throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        final int[] frameOffsets = this.getFrameOffsets();
        final int[] frameLens = this.getFrameLens();
        int length;
        if ((length = frameLens.length) > 10) {
            length = 10;
        }
        final MessageDigest instance = MessageDigest.getInstance("MD5");
        final FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        int i = 0;
        int n = 0;
        while (i < length) {
            final int n2 = frameOffsets[i] - n;
            final int n3 = frameLens[i];
            int n4 = n;
            if (n2 > 0) {
                fileInputStream.skip(n2);
                n4 = n + n2;
            }
            final byte[] array = new byte[n3];
            fileInputStream.read(array, 0, n3);
            instance.update(array);
            n = n4 + n3;
            ++i;
        }
        fileInputStream.close();
        return bytesToHex(instance.digest());
    }

    public int getAvgBitrateKbps() {
        return 0;
    }

    public int getChannels() {
        return 0;
    }

    public int getFileSizeBytes() {
        return 0;
    }

    public String getFiletype() {
        return "Unknown";
    }

    public int[] getFrameGains() {
        return null;
    }

    public int[] getFrameLens() {
        return null;
    }

    public int[] getFrameOffsets() {
        return null;
    }

    public int getNumFrames() {
        return 0;
    }

    public int getSampleRate() {
        return 0;
    }

    public int getSamplesPerFrame() {
        return 0;
    }

    public int getSeekableFrameOffset(final int n) {
        return -1;
    }

    public void setProgressListener(final ProgressListener mProgressListener) {
        this.mProgressListener = mProgressListener;
    }

    public interface Factory
    {
        CheapSoundFile create();

        String[] getSupportedExtensions();
    }

    public interface ProgressListener
    {
        boolean reportProgress(final double p0);
    }
}
