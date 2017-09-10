package edu.tdt.appstudent2.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bichan on 9/10/17.
 */

public class GradientGenerator {
    public static GradientGenerator COLOR;

    static {
        COLOR = create(Arrays.asList(
                "#2D266F",
                "#61258A",
                "#05974A",
                "#D20B54",
                "#303395",
                "#FF8359",
                "#54D169",
                "#2CBFC7",
                "#029CF5",
                "#1270E3",
                "#8739E5",
                "#B122E5",
                "#F5317F"
        ), Arrays.asList(
                "#7C2289",
                "#FD0F77",
                "#F2E51E",
                "#FFB849",
                "#27F0F0",
                "#FFDF40",
                "#AFF57A",
                "#46EEAA",
                "#15EDED",
                "#59C2FF",
                "#5496FF",
                "#FF63DE",
                "#FF7C6E"
        ));
    }

    private final List<String> mColors1;
    private final List<String> mColors2;

    public static GradientGenerator create(List<String> colorList1, List<String> colorList2) {
        return new GradientGenerator(colorList1, colorList2);
    }


    private GradientGenerator(List<String> colorList1, List<String> colorList2) {
        mColors1 = colorList1;
        mColors2 = colorList2;
    }

    public String[] getColor(Object key) {
        return new String[]{
                mColors1.get(Math.abs(key.hashCode()) % mColors1.size()),
                mColors2.get(Math.abs(key.hashCode()) % mColors2.size())
        };
    }
}
