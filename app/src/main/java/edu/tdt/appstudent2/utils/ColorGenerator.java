package edu.tdt.appstudent2.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Bichan on 7/27/2016.
 */
public class ColorGenerator {

    public static ColorGenerator DEFAULT;

    public static ColorGenerator MATERIAL;

    public static ColorGenerator FLAT;

    public static ColorGenerator TKB;
    static {
        DEFAULT = create(Arrays.asList(
                0xfff16364,
                0xfff58559,
                0xfff9a43e,
                0xffe4c62e,
                0xff67bf74,
                0xff59a2be,
                0xff2093cd,
                0xffad62a7,
                0xff805781
        ));
        MATERIAL = create(Arrays.asList(
                0xff00BFA5,
                0xff6200EA,
                0xffFFD600,
                0xff64DD17,
                0xffAA00FF,
                0xffC51162,
                0xff2962FF,
                0xff00C853,
                0xffFFAB00,
                0xff0091EA,
                0xff3E2723,
                0xff304FFE,
                0xffD50000,
                0xffAEEA00,
                0xff263238,
                0xffDD2C00,
                0xff212121,
                0xffFF6D00,
                0xff00B8D4,
                0xff00BFA5,
                0xff6200EA,
                0xffFFD600,
                0xff64DD17,
                0xffAA00FF,
                0xffC51162,
                0xff2962FF
        ));

        FLAT = create(Arrays.asList(
                0xff34495e,
                0xff16a085,
                0xff27ae60,
                0xff2980b9,
                0xff1abc9c,
                0xff2ecc71,
                0xff3498db,
                0xff9b59b6,
                0xff8e44ad,
                0xff2c3e50,
                0xfff1c40f,
                0xffe67e22,
                0xffe74c3c,
                0xff95a5a6,
                0xfff39c12,
                0xffd35400,
                0xffc0392b,
                0xff7f8c8d
        ));

        TKB = create(Arrays.asList(
                0xffD50000,
                0xffF4511E,
                0xffF6BF26,
                0xff0B8043,
                0xff33B679,
                0xff039BE5,
                0xff3F51B5,
                0xff7986CB,
                0xff8E24AA,
                0xffE67C73,
                0xff616161,
                0xff4285F4
        ));
    }

    private final List<Integer> mColors;
    private final Random mRandom;

    public static ColorGenerator create(List<Integer> colorList) {
        return new ColorGenerator(colorList);
    }

    private ColorGenerator(List<Integer> colorList) {
        mColors = colorList;
        mRandom = new Random(System.currentTimeMillis());
    }

    public int getRandomColor() {
        return mColors.get(mRandom.nextInt(mColors.size()));
    }

    public int getColor(Object key) {
        return mColors.get(Math.abs(key.hashCode()) % mColors.size());
    }
}
