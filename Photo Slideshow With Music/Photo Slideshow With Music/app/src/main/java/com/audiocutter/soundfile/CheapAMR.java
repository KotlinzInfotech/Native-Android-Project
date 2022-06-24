package com.audiocutter.soundfile;

import java.io.*;

public class CheapAMR extends CheapSoundFile
{
    private static int[] BLOCK_SIZES;
    private static int[] GAIN_FAC_MR475;
    private static int[] GAIN_FAC_MR515;
    private static int[] GRAY;
    private static int[] QUA_ENER_MR515;
    private static int[] QUA_GAIN_CODE;
    private static int[] QUA_GAIN_PITCH;
    private int mBitRate;
    private int mFileSize;
    private int[] mFrameGains;
    private int[] mFrameLens;
    private int[] mFrameOffsets;
    private int mMaxFrames;
    private int mMaxGain;
    private int mMinGain;
    private int mNumFrames;
    private int mOffset;

    static {
        CheapAMR.BLOCK_SIZES = new int[] { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
        CheapAMR.GAIN_FAC_MR515 = new int[] { 28753, 2785, 6594, 7413, 10444, 1269, 4423, 1556, 12820, 2498, 4833, 2498, 7864, 1884, 3153, 1802, 20193, 3031, 5857, 4014, 8970, 1392, 4096, 655, 13926, 3112, 4669, 2703, 6553, 901, 2662, 655, 23511, 2457, 5079, 4096, 8560, 737, 4259, 2088, 12288, 1474, 4628, 1433, 7004, 737, 2252, 1228, 17326, 2334, 5816, 3686, 8601, 778, 3809, 614, 9256, 1761, 3522, 1966, 5529, 737, 3194, 778 };
        CheapAMR.QUA_ENER_MR515 = new int[] { 17333, -3431, 4235, 5276, 8325, -10422, 683, -8609, 10148, -4398, 1472, -4398, 5802, -6907, -2327, -7303, 14189, -2678, 3181, -180, 6972, -9599, 0, -16305, 10884, -2444, 1165, -3697, 4180, -13468, -3833, -16305, 15543, -4546, 1913, 0, 6556, -15255, 347, -5993, 9771, -9090, 1086, -9341, 4772, -15255, -5321, -10714, 12827, -5002, 3118, -938, 6598, -14774, -646, -16879, 7251, -7508, -1343, -6529, 2668, -15255, -2212, -2454, -14774 };
        CheapAMR.QUA_GAIN_CODE = new int[] { 159, -3776, -22731, 206, -3394, -20428, 268, -3005, -18088, 349, -2615, -15739, 419, -2345, -14113, 482, -2138, -12867, 554, -1932, -11629, 637, -1726, -10387, 733, -1518, -9139, 842, -1314, -7906, 969, -1106, -6656, 1114, -900, -5416, 1281, -694, -4173, 1473, -487, -2931, 1694, -281, -1688, 1948, -75, -445, 2241, 133, 801, 2577, 339, 2044, 2963, 545, 3285, 3408, 752, 4530, 3919, 958, 5772, 4507, 1165, 7016, 5183, 1371, 8259, 5960, 1577, 9501, 6855, 1784, 10745, 7883, 1991, 11988, 9065, 2197, 13231, 10425, 2404, 14474, 12510, 2673, 16096, 16263, 3060, 18429, 21142, 3448, 20763, 27485, 3836, 23097 };
        CheapAMR.GAIN_FAC_MR475 = new int[] { 812, 128, 542, 140, 2873, 1135, 2266, 3402, 2067, 563, 12677, 647, 4132, 1798, 5601, 5285, 7689, 374, 3735, 441, 10912, 2638, 11807, 2494, 20490, 797, 5218, 675, 6724, 8354, 5282, 1696, 1488, 428, 5882, 452, 5332, 4072, 3583, 1268, 2469, 901, 15894, 1005, 14982, 3271, 10331, 4858, 3635, 2021, 2596, 835, 12360, 4892, 12206, 1704, 13432, 1604, 9118, 2341, 3968, 1538, 5479, 9936, 3795, 417, 1359, 414, 3640, 1569, 7995, 3541, 11405, 645, 8552, 635, 4056, 1377, 16608, 6124, 11420, 700, 2007, 607, 12415, 1578, 11119, 4654, 13680, 1708, 11990, 1229, 7996, 7297, 13231, 5715, 2428, 1159, 2073, 1941, 6218, 6121, 3546, 1804, 8925, 1802, 8679, 1580, 13935, 3576, 13313, 6237, 6142, 1130, 5994, 1734, 14141, 4662, 11271, 3321, 12226, 1551, 13931, 3015, 5081, 10464, 9444, 6706, 1689, 683, 1436, 1306, 7212, 3933, 4082, 2713, 7793, 704, 15070, 802, 6299, 5212, 4337, 5357, 6676, 541, 6062, 626, 13651, 3700, 11498, 2408, 16156, 716, 12177, 751, 8065, 11489, 6314, 2256, 4466, 496, 7293, 523, 10213, 3833, 8394, 3037, 8403, 966, 14228, 1880, 8703, 5409, 16395, 4863, 7420, 1979, 6089, 1230, 9371, 4398, 14558, 3363, 13559, 2873, 13163, 1465, 5534, 1678, 13138, 14771, 7338, 600, 1318, 548, 4252, 3539, 10044, 2364, 10587, 622, 13088, 669, 14126, 3526, 5039, 9784, 15338, 619, 3115, 590, 16442, 3013, 15542, 4168, 15537, 1611, 15405, 1228, 16023, 9299, 7534, 4976, 1990, 1213, 11447, 1157, 12512, 5519, 9475, 2644, 7716, 2034, 13280, 2239, 16011, 5093, 8066, 6761, 10083, 1413, 5002, 2347, 12523, 5975, 15126, 2899, 18264, 2289, 15827, 2527, 16265, 10254, 14651, 11319, 1797, 337, 3115, 397, 3510, 2928, 4592, 2670, 7519, 628, 11415, 656, 5946, 2435, 6544, 7367, 8238, 829, 4000, 863, 10032, 2492, 16057, 3551, 18204, 1054, 6103, 1454, 5884, 7900, 18752, 3468, 1864, 544, 9198, 683, 11623, 4160, 4594, 1644, 3158, 1157, 15953, 2560, 12349, 3733, 17420, 5260, 6106, 2004, 2917, 1742, 16467, 5257, 16787, 1680, 17205, 1759, 4773, 3231, 7386, 6035, 14342, 10012, 4035, 442, 4194, 458, 9214, 2242, 7427, 4217, 12860, 801, 11186, 825, 12648, 2084, 12956, 6554, 9505, 996, 6629, 985, 10537, 2502, 15289, 5006, 12602, 2055, 15484, 1653, 16194, 6921, 14231, 5790, 2626, 828, 5615, 1686, 13663, 5778, 3668, 1554, 11313, 2633, 9770, 1459, 14003, 4733, 15897, 6291, 6278, 1870, 7910, 2285, 16978, 4571, 16576, 3849, 15248, 2311, 16023, 3244, 14459, 17808, 11847, 2763, 1981, 1407, 1400, 876, 4335, 3547, 4391, 4210, 5405, 680, 17461, 781, 6501, 5118, 8091, 7677, 7355, 794, 8333, 1182, 15041, 3160, 14928, 3039, 20421, 880, 14545, 852, 12337, 14708, 6904, 1920, 4225, 933, 8218, 1087, 10659, 4084, 10082, 4533, 2735, 840, 20657, 1081, 16711, 5966, 15873, 4578, 10871, 2574, 3773, 1166, 14519, 4044, 20699, 2627, 15219, 2734, 15274, 2186, 6257, 3226, 13125, 19480, 7196, 930, 2462, 1618, 4515, 3092, 13852, 4277, 10460, 833, 17339, 810, 16891, 2289, 15546, 8217, 13603, 1684, 3197, 1834, 15948, 2820, 15812, 5327, 17006, 2438, 16788, 1326, 15671, 8156, 11726, 8556, 3762, 2053, 9563, 1317, 13561, 6790, 12227, 1936, 8180, 3550, 13287, 1778, 16299, 6599, 16291, 7758, 8521, 2551, 7225, 2645, 18269, 7489, 16885, 2248, 17882, 2884, 17265, 3328, 9417, 20162, 11042, 8320, 1286, 620, 1431, 583, 5993, 2289, 3978, 3626, 5144, 752, 13409, 830, 5553, 2860, 11764, 5908, 10737, 560, 5446, 564, 13321, 3008, 11946, 3683, 19887, 798, 9825, 728, 13663, 8748, 7391, 3053, 2515, 778, 6050, 833, 6469, 5074, 8305, 2463, 6141, 1865, 15308, 1262, 14408, 4547, 13663, 4515, 3137, 2983, 2479, 1259, 15088, 4647, 15382, 2607, 14492, 2392, 12462, 2537, 7539, 2949, 12909, 12060, 5468, 684, 3141, 722, 5081, 1274, 12732, 4200, 15302, 681, 7819, 592, 6534, 2021, 16478, 8737, 13364, 882, 5397, 899, 14656, 2178, 14741, 4227, 14270, 1298, 13929, 2029, 15477, 7482, 15815, 4572, 2521, 2013, 5062, 1804, 5159, 6582, 7130, 3597, 10920, 1611, 11729, 1708, 16903, 3455, 16268, 6640, 9306, 1007, 9369, 2106, 19182, 5037, 12441, 4269, 15919, 1332, 15357, 3512, 11898, 14141, 16101, 6854, 2010, 737, 3779, 861, 11454, 2880, 3564, 3540, 9057, 1241, 12391, 896, 8546, 4629, 11561, 5776, 8129, 589, 8218, 588, 18728, 3755, 12973, 3149, 15729, 758, 16634, 754, 15222, 11138, 15871, 2208, 4673, 610, 10218, 678, 15257, 4146, 5729, 3327, 8377, 1670, 19862, 2321, 15450, 5511, 14054, 5481, 5728, 2888, 7580, 1346, 14384, 5325, 16236, 3950, 15118, 3744, 15306, 1435, 14597, 4070, 12301, 15696, 7617, 1699, 2170, 884, 4459, 4567, 18094, 3306, 12742, 815, 14926, 907, 15016, 4281, 15518, 8368, 17994, 1087, 2358, 865, 16281, 3787, 15679, 4596, 16356, 1534, 16584, 2210, 16833, 9697, 15929, 4513, 3277, 1085, 9643, 2187, 11973, 6068, 9199, 4462, 8955, 1629, 10289, 3062, 16481, 5155, 15466, 7066, 13678, 2543, 5273, 2277, 16746, 6213, 16655, 3408, 20304, 3363, 18688, 1985, 14172, 12867, 15154, 15703, 4473, 1020, 1681, 886, 4311, 4301, 8952, 3657, 5893, 1147, 11647, 1452, 15886, 2227, 4582, 6644, 6929, 1205, 6220, 799, 12415, 3409, 15968, 3877, 19859, 2109, 9689, 2141, 14742, 8830, 14480, 2599, 1817, 1238, 7771, 813, 19079, 4410, 5554, 2064, 3687, 2844, 17435, 2256, 16697, 4486, 16199, 5388, 8028, 2763, 3405, 2119, 17426, 5477, 13698, 2786, 19879, 2720, 9098, 3880, 18172, 4833, 17336, 12207, 5116, 996, 4935, 988, 9888, 3081, 6014, 5371, 15881, 1667, 8405, 1183, 15087, 2366, 19777, 7002, 11963, 1562, 7279, 1128, 16859, 1532, 15762, 5381, 14708, 2065, 20105, 2155, 17158, 8245, 17911, 6318, 5467, 1504, 4100, 2574, 17421, 6810, 5673, 2888, 16636, 3382, 8975, 1831, 20159, 4737, 19550, 7294, 6658, 2781, 11472, 3321, 19397, 5054, 18878, 4722, 16439, 2373, 20430, 4386, 11353, 26526, 11593, 3068, 2866, 1566, 5108, 1070, 9614, 4915, 4939, 3536, 7541, 878, 20717, 851, 6938, 4395, 16799, 7733, 10137, 1019, 9845, 964, 15494, 3955, 15459, 3430, 18863, 982, 20120, 963, 16876, 12887, 14334, 4200, 6599, 1220, 9222, 814, 16942, 5134, 5661, 4898, 5488, 1798, 20258, 3962, 17005, 6178, 17929, 5929, 9365, 3420, 7474, 1971, 19537, 5177, 19003, 3006, 16454, 3788, 16070, 2367, 8664, 2743, 9445, 26358, 10856, 1287, 3555, 1009, 5606, 3622, 19453, 5512, 12453, 797, 20634, 911, 15427, 3066, 17037, 10275, 18883, 2633, 3913, 1268, 19519, 3371, 18052, 5230, 19291, 1678, 19508, 3172, 18072, 10754, 16625, 6845, 3134, 2298, 10869, 2437, 15580, 6913, 12597, 3381, 11116, 3297, 16762, 2424, 18853, 6715, 17171, 9887, 12743, 2605, 8937, 3140, 19033, 7764, 18347, 3880, 20475, 3682, 19602, 3380, 13044, 19373, 10526, 23124 };
        CheapAMR.GRAY = new int[] { 0, 1, 3, 2, 5, 6, 4, 7 };
        CheapAMR.QUA_GAIN_PITCH = new int[] { 0, 3277, 6556, 8192, 9830, 11469, 12288, 13107, 13926, 14746, 15565, 16384, 17203, 18022, 18842, 19661 };
    }

    public static Factory getFactory() {
        return new Factory() {
            @Override
            public CheapSoundFile create() {
                return new CheapAMR();
            }

            @Override
            public String[] getSupportedExtensions() {
                return new String[] { "3gpp", "3gp", "amr" };
            }
        };
    }

    private void parse3gpp(final InputStream inputStream, final int n) throws IOException {
        if (n < 8) {
            return;
        }
        final byte[] array = new byte[8];
        inputStream.read(array, 0, 8);
        this.mOffset += 8;
        final int n2 = (array[2] & 0xFF) << 8 | ((array[0] & 0xFF) << 24 | (array[1] & 0xFF) << 16) | (array[3] & 0xFF);
        if (n2 > n) {
            return;
        }
        if (n2 <= 0) {
            return;
        }
        if (array[4] == 109 && array[5] == 100 && array[6] == 97 && array[7] == 116) {
            this.parseAMR(inputStream, n2);
            return;
        }
        final int n3 = n2 - 8;
        inputStream.skip(n3);
        this.mOffset += n3;
        this.parse3gpp(inputStream, n - n2);
    }

    @Override
    public void ReadFile(final File file) throws FileNotFoundException, IOException {
        super.ReadFile(file);
        this.mNumFrames = 0;
        this.mMaxFrames = 64;
        this.mFrameOffsets = new int[this.mMaxFrames];
        this.mFrameLens = new int[this.mMaxFrames];
        this.mFrameGains = new int[this.mMaxFrames];
        this.mMinGain = 1000000000;
        this.mMaxGain = 0;
        this.mBitRate = 10;
        this.mOffset = 0;
        this.mFileSize = (int)this.mInputFile.length();
        if (this.mFileSize < 128) {
            throw new IOException("File too small to parse");
        }
        final FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        final byte[] array = new byte[12];
        fileInputStream.read(array, 0, 6);
        this.mOffset += 6;
        if (array[0] == 35 && array[1] == 33 && array[2] == 65 && array[3] == 77 && array[4] == 82 && array[5] == 10) {
            this.parseAMR(fileInputStream, this.mFileSize - 6);
        }
        fileInputStream.read(array, 6, 6);
        this.mOffset += 6;
        if (array[4] == 102 && array[5] == 116 && array[6] == 121 && array[7] == 112 && array[8] == 51 && array[9] == 103 && array[10] == 112 && array[11] == 52) {
            final int n = (array[0] & 0xFF) << 24 | (array[1] & 0xFF) << 16 | (array[2] & 0xFF) << 8 | (array[3] & 0xFF);
            if (n >= 4 && n <= this.mFileSize - 8) {
                final int n2 = n - 12;
                fileInputStream.skip(n2);
                this.mOffset += n2;
            }
            this.parse3gpp(fileInputStream, this.mFileSize - n);
        }
    }

    @Override
    public void WriteFile(final File file, final int n, final int n2) throws IOException {
        file.createNewFile();
        final FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(new byte[] { 35, 33, 65, 77, 82, 10 }, 0, 6);
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
            if (n8 >= 0) {
                int n10 = n6;
                if (n8 > 0) {
                    fileInputStream.skip(n8);
                    n10 = n6 + n8;
                }
                fileInputStream.read(array, 0, n9);
                fileOutputStream.write(array, 0, n9);
                n6 = n10 + n9;
            }
            ++j;
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    void addFrame(int i, int mMaxFrames, final int n) {
        this.mFrameOffsets[this.mNumFrames] = i;
        this.mFrameLens[this.mNumFrames] = mMaxFrames;
        this.mFrameGains[this.mNumFrames] = n;
        if (n < this.mMinGain) {
            this.mMinGain = n;
        }
        if (n > this.mMaxGain) {
            this.mMaxGain = n;
        }
        ++this.mNumFrames;
        if (this.mNumFrames == this.mMaxFrames) {
            mMaxFrames = this.mMaxFrames * 2;
            final int[] mFrameOffsets = new int[mMaxFrames];
            final int[] mFrameLens = new int[mMaxFrames];
            final int[] mFrameGains = new int[mMaxFrames];
            for (i = 0; i < this.mNumFrames; ++i) {
                mFrameOffsets[i] = this.mFrameOffsets[i];
                mFrameLens[i] = this.mFrameLens[i];
                mFrameGains[i] = this.mFrameGains[i];
            }
            this.mFrameOffsets = mFrameOffsets;
            this.mFrameLens = mFrameLens;
            this.mFrameGains = mFrameGains;
            this.mMaxFrames = mMaxFrames;
        }
    }

    @Override
    public int getAvgBitrateKbps() {
        return this.mBitRate;
    }

    @Override
    public int getChannels() {
        return 1;
    }

    @Override
    public int getFileSizeBytes() {
        return this.mFileSize;
    }

    @Override
    public String getFiletype() {
        return "AMR";
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

    void getMR122Params(final int[] array, final int[] array2, final int[] array3, final int[] array4, final int[][] array5) {
        array2[0] = array[45] * 1 + array[43] * 2 + array[41] * 4 + array[39] * 8 + array[37] * 16 + array[35] * 32 + array[33] * 64 + array[31] * 128 + array[29] * 256;
        array2[1] = array[242] * 1 + array[79] * 2 + array[77] * 4 + array[75] * 8 + array[73] * 16 + array[71] * 32;
        array2[2] = array[46] * 1 + array[44] * 2 + array[42] * 4 + array[40] * 8 + array[38] * 16 + array[36] * 32 + array[34] * 64 + array[32] * 128 + array[30] * 256;
        array2[3] = array[243] * 1 + array[80] * 2 + array[78] * 4 + array[76] * 8 + array[74] * 16 + array[72] * 32;
        array3[0] = array[88] * 1 + array[55] * 2 + array[51] * 4 + array[47] * 8;
        array3[1] = array[89] * 1 + array[56] * 2 + array[52] * 4 + array[48] * 8;
        array3[2] = array[90] * 1 + array[57] * 2 + array[53] * 4 + array[49] * 8;
        array3[3] = array[91] * 1 + array[58] * 2 + array[54] * 4 + array[50] * 8;
        array4[0] = array[104] * 1 + array[92] * 2 + array[67] * 4 + array[63] * 8 + array[59] * 16;
        array4[1] = array[105] * 1 + array[93] * 2 + array[68] * 4 + array[64] * 8 + array[60] * 16;
        array4[2] = array[106] * 1 + array[94] * 2 + array[69] * 4 + array[65] * 8 + array[61] * 16;
        array4[3] = array[107] * 1 + array[95] * 2 + array[70] * 4 + array[66] * 8 + array[62] * 16;
        array5[0][0] = array[122] * 1 + array[123] * 2 + array[124] * 4 + array[96] * 8;
        array5[0][1] = array[125] * 1 + array[126] * 2 + array[127] * 4 + array[100] * 8;
        array5[0][2] = array[128] * 1 + array[129] * 2 + array[130] * 4 + array[108] * 8;
        array5[0][3] = array[131] * 1 + array[132] * 2 + array[133] * 4 + array[112] * 8;
        array5[0][4] = array[134] * 1 + array[135] * 2 + array[136] * 4 + array[116] * 8;
        array5[0][5] = array[182] * 1 + array[183] * 2 + array[184] * 4;
        array5[0][6] = array[185] * 1 + array[186] * 2 + array[187] * 4;
        array5[0][7] = array[188] * 1 + array[189] * 2 + array[190] * 4;
        array5[0][8] = array[191] * 1 + array[192] * 2 + array[193] * 4;
        array5[0][9] = array[194] * 1 + array[195] * 2 + array[196] * 4;
        array5[1][0] = array[137] * 1 + array[138] * 2 + array[139] * 4 + array[97] * 8;
        array5[1][1] = array[140] * 1 + array[141] * 2 + array[142] * 4 + array[101] * 8;
        array5[1][2] = array[143] * 1 + array[144] * 2 + array[145] * 4 + array[109] * 8;
        array5[1][3] = array[146] * 1 + array[147] * 2 + array[148] * 4 + array[113] * 8;
        array5[1][4] = array[149] * 1 + array[150] * 2 + array[151] * 4 + array[117] * 8;
        array5[1][5] = array[197] * 1 + array[198] * 2 + array[199] * 4;
        array5[1][6] = array[200] * 1 + array[201] * 2 + array[202] * 4;
        array5[1][7] = array[203] * 1 + array[204] * 2 + array[205] * 4;
        array5[1][8] = array[206] * 1 + array[207] * 2 + array[208] * 4;
        array5[1][9] = array[209] * 1 + array[210] * 2 + array[211] * 4;
        array5[2][0] = array[152] * 1 + array[153] * 2 + array[154] * 4 + array[98] * 8;
        array5[2][1] = array[155] * 1 + array[156] * 2 + array[157] * 4 + array[102] * 8;
        array5[2][2] = array[158] * 1 + array[159] * 2 + array[160] * 4 + array[110] * 8;
        array5[2][3] = array[161] * 1 + array[162] * 2 + array[163] * 4 + array[114] * 8;
        array5[2][4] = array[164] * 1 + array[165] * 2 + array[166] * 4 + array[118] * 8;
        array5[2][5] = array[212] * 1 + array[213] * 2 + array[214] * 4;
        array5[2][6] = array[215] * 1 + array[216] * 2 + array[217] * 4;
        array5[2][7] = array[218] * 1 + array[219] * 2 + array[220] * 4;
        array5[2][8] = array[221] * 1 + array[222] * 2 + array[223] * 4;
        array5[2][9] = array[224] * 1 + array[225] * 2 + array[226] * 4;
        array5[3][0] = array[167] * 1 + array[168] * 2 + array[169] * 4 + array[99] * 8;
        array5[3][1] = array[170] * 1 + array[171] * 2 + array[172] * 4 + array[103] * 8;
        array5[3][2] = array[173] * 1 + array[174] * 2 + array[175] * 4 + array[111] * 8;
        array5[3][3] = array[176] * 1 + array[177] * 2 + array[178] * 4 + array[115] * 8;
        array5[3][4] = array[179] * 1 + array[180] * 2 + array[181] * 4 + array[119] * 8;
        array5[3][5] = array[227] * 1 + array[228] * 2 + array[229] * 4;
        array5[3][6] = array[230] * 1 + array[231] * 2 + array[232] * 4;
        array5[3][7] = array[233] * 1 + array[234] * 2 + array[235] * 4;
        array5[3][8] = array[236] * 1 + array[237] * 2 + array[238] * 4;
        array5[3][9] = array[239] * 1 + array[240] * 2 + array[241] * 4;
    }

    @Override
    public int getNumFrames() {
        return this.mNumFrames;
    }

    @Override
    public int getSampleRate() {
        return 8000;
    }

    @Override
    public int getSamplesPerFrame() {
        return 40;
    }

    void parseAMR(final InputStream inputStream, final int n) throws IOException {
        final int[] array = new int[4];
        final int n2 = 0;
        for (int i = 0; i < 4; ++i) {
            array[i] = 0;
        }
        final int[] array2 = new int[4];
        for (int j = 0; j < 4; ++j) {
            array2[j] = -2381;
        }
        int k = n;
        int n3 = n2;
        while (k > 0) {
            final int amrFrame = this.parseAMRFrame(inputStream, k, array);
            final int n4 = n3 + amrFrame;
            final int n5 = k -= amrFrame;
            n3 = n4;
            if (this.mProgressListener != null) {
                k = n5;
                n3 = n4;
                if (!this.mProgressListener.reportProgress(n4 * 1.0 / n)) {
                    break;
                }
                continue;
            }
        }
    }

    int parseAMRFrame(final InputStream inputStream, int i, final int[] array) throws IOException {
        final int mOffset = this.mOffset;
        final byte[] array2 = { 0 };
        inputStream.read(array2, 0, 1);
        ++this.mOffset;
        final int n = ((array2[0] & 0xFF) >> 3) % 15;
        final byte b = array2[0];
        final int n2 = CheapAMR.BLOCK_SIZES[n];
        final int n3 = n2 + 1;
        if (n3 > i) {
            return i;
        }
        if (n2 == 0) {
            return 1;
        }
        final byte[] array3 = new byte[n2];
        inputStream.read(array3, 0, n2);
        this.mOffset += n2;
        final int n4 = n2 * 8;
        final int[] array4 = new int[n4];
        i = (array3[0] & 0xFF);
        int j = 0;
        int n5 = 0;
        while (j < n4) {
            array4[j] = (i & 0x80) >> 7;
            final int n6 = i <<= 1;
            int n7 = n5;
            if ((j & 0x7) == 0x7) {
                i = n6;
                n7 = n5;
                if (j < n4 - 1) {
                    n7 = n5 + 1;
                    i = (array3[n7] & 0xFF);
                }
            }
            ++j;
            n5 = n7;
        }
        if (n != 7) {
            switch (n) {
                default: {
                    final PrintStream out = System.out;
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unsupported frame type: ");
                    sb.append(n);
                    out.println(sb.toString());
                    this.addFrame(mOffset, n3, 1);
                    return n3;
                }
                case 1: {
                    this.mBitRate = 5;
                    final int[] array5 = { array4[24] * 1 + array4[25] * 2 + array4[26] * 4 + array4[36] * 8 + array4[45] * 16 + array4[55] * 32, array4[27] * 1 + array4[28] * 2 + array4[29] * 4 + array4[37] * 8 + array4[46] * 16 + array4[56] * 32, array4[30] * 1 + array4[31] * 2 + array4[32] * 4 + array4[38] * 8 + array4[47] * 16 + array4[57] * 32, array4[33] * 1 + array4[34] * 2 + array4[35] * 4 + array4[39] * 8 + array4[48] * 16 + array4[58] * 32 };
                    int n8;
                    int n9;
                    int n10;
                    int n11;
                    int n12;
                    int n13;
                    for (i = 0; i < 4; ++i) {
                        n8 = array[0];
                        n9 = array[1];
                        n10 = array[2];
                        n11 = array[3];
                        n12 = CheapAMR.QUA_ENER_MR515[array5[i]];
                        n13 = CheapAMR.GAIN_FAC_MR515[array5[i]];
                        array[3] = array[2];
                        array[2] = array[1];
                        array[1] = array[0];
                        array[0] = n12;
                        this.addFrame(mOffset, n3, (n8 * 5571 + 385963008 + n9 * 4751 + n10 * 2785 + n11 * 1556 >> 15) * n13 >> 24);
                    }
                    break;
                }
                case 0: {
                    this.mBitRate = 5;
                    final int[] array6 = new int[4];
                    array6[1] = (array6[0] = array4[28] * 1 + array4[29] * 2 + array4[30] * 4 + array4[31] * 8 + array4[46] * 16 + array4[47] * 32 + array4[48] * 64 + array4[49] * 128);
                    array6[3] = (array6[2] = array4[32] * 1 + array4[33] * 2 + array4[34] * 4 + array4[35] * 8 + array4[40] * 16 + array4[41] * 32 + array4[42] * 64 + array4[43] * 128);
                    int n14;
                    double n15;
                    int n16;
                    int n17;
                    int n18;
                    int n19;
                    int n20;
                    int n21;
                    for (i = 0; i < 4; ++i) {
                        n14 = CheapAMR.GAIN_FAC_MR475[array6[i] * 4 + (i & 0x1) * 2 + 1];
                        n15 = Math.log(n14) / Math.log(2.0);
                        n16 = (int)n15;
                        n17 = (int)((n15 - n16) * 32768.0);
                        n18 = array[0];
                        n19 = array[1];
                        n20 = array[2];
                        n21 = array[3];
                        array[3] = array[2];
                        array[2] = array[1];
                        array[1] = array[0];
                        array[0] = ((n16 - 12) * 49320 + (n17 * 24660 >> 15) * 2) * 8192 + 32768 >> 16;
                        this.addFrame(mOffset, n3, (n18 * 5571 + 385963008 + n19 * 4751 + n20 * 2785 + n21 * 1556 >> 15) * n14 >> 24);
                    }
                    break;
                }
            }
        }
        else {
            this.mBitRate = 12;
            final int[] array7 = new int[4];
            final int[] array8 = new int[4];
            final int[] array9 = new int[4];
            final int[][] array10 = new int[4][];
            for (i = 0; i < 4; ++i) {
                array10[i] = new int[10];
            }
            this.getMR122Params(array4, array7, array8, array9, array10);
            int k = 0;
            int n22 = 0;
            while (k < 4) {
                final int[] array11 = new int[40];
                for (i = 0; i < 40; ++i) {
                    array11[i] = 0;
                }
                for (int l = 0; l < 5; ++l) {
                    if ((array10[k][l] >> 3 & 0x1) == 0x0) {
                        i = 4096;
                    }
                    else {
                        i = -4096;
                    }
                    final int n23 = CheapAMR.GRAY[array10[k][l] & 0x7] * 5 + l;
                    final int n24 = l + CheapAMR.GRAY[array10[k][l + 5] & 0x7] * 5;
                    array11[n23] = i;
                    int n25 = i;
                    if (n24 < n23) {
                        n25 = -i;
                    }
                    array11[n24] += n25;
                }
                final int n26 = array7[k];
                if (k != 0 && k != 2) {
                    if ((i = n22 - 5) < 18) {
                        i = 18;
                    }
                    int n27 = i;
                    if (i + 9 > 143) {
                        n27 = 134;
                    }
                    i = n27 + (n26 + 5) / 6 - 1;
                }
                else if (n26 < 463) {
                    i = (n26 + 5) / 6 + 17;
                }
                else {
                    i = n26 - 368;
                }
                final int n28 = CheapAMR.QUA_GAIN_PITCH[array8[k]] >> 2 << 2;
                int n29;
                if (n28 > 16383) {
                    n29 = 32767;
                }
                else {
                    n29 = n28 * 2;
                }
                for (int n30 = i; n30 < 40; ++n30) {
                    array11[n30] += array11[n30 - i] * n29 >> 15;
                }
                int n31 = 0;
                int n32 = 0;
                while (n31 < 40) {
                    n32 += array11[n31] * array11[n31];
                    ++n31;
                }
                int n33;
                if (1073741823 > n32 && n32 >= 0) {
                    n33 = n32 * 2;
                }
                else {
                    n33 = Integer.MAX_VALUE;
                }
                final double n34 = Math.log((n33 + 32768 >> 16) * 52428) / Math.log(2.0);
                final int n35 = (int)n34;
                final int n36 = ((array[0] * 44 + array[1] * 37 + array[2] * 22 + array[3] * 12) * 2 + 783741 - ((n35 - 30 << 16) + (int)((n34 - n35) * 32768.0) * 2)) / 2;
                final int n37 = n36 >> 16;
                final int n38 = (int)(Math.pow(2.0, n37 + ((n36 >> 1) - (n37 << 15)) / 32768.0) + 0.5);
                int n39;
                if (n38 <= 2047) {
                    n39 = n38 << 4;
                }
                else {
                    n39 = 32767;
                }
                final int n40 = array9[k];
                final int[] qua_GAIN_CODE = CheapAMR.QUA_GAIN_CODE;
                final int n41 = n40 * 3;
                int n42;
                if (((n42 = n39 * qua_GAIN_CODE[n41] >> 15 << 1) & 0xFFFF8000) != 0x0) {
                    n42 = 32767;
                }
                this.addFrame(mOffset, n3, n42);
                final int n43 = CheapAMR.QUA_GAIN_CODE[n41 + 1];
                array[3] = array[2];
                array[2] = array[1];
                array[1] = array[0];
                array[0] = n43;
                ++k;
                n22 = i;
            }
        }
        return n3;
    }
}
