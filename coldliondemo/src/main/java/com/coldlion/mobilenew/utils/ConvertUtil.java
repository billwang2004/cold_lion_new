package com.coldlion.mobilenew.utils;

import android.graphics.Color;
import android.graphics.Point;

import com.ab.util.AbStrUtil;
import com.coldlion.mobilenew.type.CMProperty;
import com.coldlion.mobilenew.type.IDNamePair;
import com.coldlion.mobilenew.type.Size;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public final class ConvertUtil {

    public static Integer val2i(String str) {
        String tempStr = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == ' ' || c == ',') {
                continue;
            }
            if (c >= '0' && c <= '9') {
                tempStr += c;
            } else if ((c == '+' || c == '-') && tempStr.equals("")) {
                tempStr += c;
            } else if (c == '.' && tempStr.indexOf(c) < 0) {
                tempStr +=c;
            } else {
                break;
            }
        }
        if (tempStr.equals("")) {
            tempStr = "0";
        } else if (tempStr.equals("-")) {
            tempStr = "-0";
        }

        int val = 0;
        try {
            val = (int) Float.parseFloat(tempStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;
    }

    public static Date str2Date(String str, String format) {
        Date ltRetVal = new GregorianCalendar(1900, 1, 1).getTime();

        if (!AbStrUtil.isEmpty(str)) {
            try {
                if (!AbStrUtil.isEmpty(format)) {
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                    ltRetVal = sdf.parse(str);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    ltRetVal = sdf.parse(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ltRetVal;
    }

    public static boolean str2bool(String str) {
        boolean llRetVal = false;

        str = upperAndTrim(str);
        if (str.equals("TRUE") || str.equals("YES") || str.equals("Y")) {
            llRetVal = true;
        }

        return llRetVal;
    }

    public static ArrayList<String> str2Array(String str) {
        return str2ArrayEx(str, ",");
    }

    public static ArrayList<String> str2ArrayEx(String str, String separator) {
        ArrayList<String> result = new ArrayList<String>();

        str = (str == null ? "" : str.trim());
        int sepLength = separator.length();
        while (str.length() > 0) {
            int pos = str.indexOf(separator);
            int leftTokenPos = str.indexOf("[");
            if (leftTokenPos >= 0 && leftTokenPos < pos) {
                int tokenCount = 1;
                int len = str.length();
                int nextPos = leftTokenPos + 1;
                for (; nextPos < len; nextPos++) {
                    if (str.charAt(nextPos) == '[') {
                        tokenCount++;
                    } else if (str.charAt(nextPos) == ']') {
                        tokenCount--;
                    }
                    if (tokenCount == 0) {
                        break;
                    }
                }
                pos = str.indexOf(separator, nextPos);
            }

            if (pos < 0) {
                pos = str.length();
            }

            String tempStr = str.substring(0, pos).trim();
            int liTempLen = tempStr.length();
            if (liTempLen > 0 && tempStr.charAt(0) == '[' && tempStr.charAt(liTempLen - 1) == ']') {
                tempStr = tempStr.substring(1, 1 + liTempLen - 2);
            }

            result.add(tempStr.trim());

            if (pos + sepLength == str.length()) {
                result.add("");
                str = "";
            } else if (pos + sepLength > str.length()) {
                str = "";
            } else {
                str = str.substring(pos + sepLength).trim();
            }
        }

        return result;
    }

    public static String getDelimitedStr(Object object) {
        return "[" + object.toString() + "]";
    }

    public static String joinString(Object o1, Object o2) {
        return String.format("[%1$s],[%2$s]", o1.toString().trim(), o2.toString().trim());
    }

    public static String addStr(String s1, Object obj) {
        String retVal = getDelimitedStr(obj);

        s1 = s1.trim();
        if (!s1.equals("")) {
            if (s1.charAt(0) == '[' && s1.charAt(s1.length() - 1) == ']') {
                retVal = s1 + String.format(",[%1$s]", obj.toString().trim());
            } else {
                retVal = ConvertUtil.joinString(s1, obj);
            }
        }

        return retVal;
    }

    public static int colorFromStr(String str) {
        int color = Color.BLACK;

        if (AbStrUtil.isEmpty(str) || str.equals("SystemColors.Control")) {
            return color;
        }

        try {
            color = Color.parseColor(str);
        } catch (Exception e) {
            ArrayList<String> aa = str2Array(str);

            if (aa.size() >= 3) {
                int r = val2i(aa.get(0));
                int g = val2i(aa.get(1));
                int b = val2i(aa.get(2));
                color = Color.argb(255, r, g, b);
            } else {
                color = Color.GRAY;
            }
        }

        return color;
    }

    public static Size sizeFromStr(String str) {

        ArrayList<String> arrayList = ConvertUtil.str2ArrayEx(str, ",");
        Size size = new Size(0, 0);
        if (arrayList.size() == 2) {
            size.setWidth(ConvertUtil.val2i(arrayList.get(0)));
            size.setHeight(ConvertUtil.val2i(arrayList.get(1)));
        }
        return size;
    }

     public static CMProperty.TextAlignment contentAlignmentFromStr(String pcAlignment) {
        CMProperty.TextAlignment textAlignment = CMProperty.TextAlignment.left;

        pcAlignment = upperAndTrim(pcAlignment);
        if (pcAlignment.contains("LEFT")) {
            textAlignment = CMProperty.TextAlignment.left;
        } else if (pcAlignment.contains("CENTER")) {
            textAlignment = CMProperty.TextAlignment.center;
        } else if (pcAlignment.contains("RIGHT")) {
            textAlignment = CMProperty.TextAlignment.right;
        }

        return textAlignment;
    }

    public static String mappingFromArrayList(ArrayList<IDNamePair> idNamePairs)
    {
        String retVal = "";

        for (IDNamePair loIdName : idNamePairs)
        {
            String lcCurrent = String.format("[%1$s]=[%2$s]", loIdName.getId(), loIdName.getName());
            retVal = ConvertUtil.addStr(retVal, lcCurrent);
        }

        return retVal;
    }

    public static ArrayList<IDNamePair> mappingToIdCollection(String str)
    {
        return mappingToIdCollection(str, ",");
    }

    public static ArrayList<IDNamePair> mappingToIdCollection(String mapStr, String sep)
    {
        ArrayList<IDNamePair> loRetVal = new ArrayList<IDNamePair>();

        ArrayList<String> loArr = ConvertUtil.str2ArrayEx(mapStr, sep);
        for (String lcStr : loArr)
        {
            ArrayList<String> loCur = ConvertUtil.str2ArrayEx(lcStr, "=");
            if (loCur.size() == 2)
            {
                loRetVal.add(new IDNamePair(loCur.get(0), loCur.get(1)));
            }
        }
        return loRetVal;
    }

    public static Point ptFromStr(String s)
    {

        java.util.ArrayList<String> arrayList = ConvertUtil.str2ArrayEx(s, ",");
        Point point = new Point(0, 0);

        if (arrayList.size() == 2)
        {
            point.x = ConvertUtil.val2i(arrayList.get(0));
            point.y = ConvertUtil.val2i(arrayList.get(1));
        }
        return point;
    }

    public static String upperAndTrim(String str)
    {
        if (str == null)
        {
            str = "";
        }
        return str.toUpperCase().trim();
    }

    public static String trimEnd(String string, char[] charsToTrim)
    {
        if (string == null || charsToTrim == null)
            return string;

        int lengthToKeep = string.length();
        for (int index = string.length() - 1; index >= 0; index--)
        {
            boolean removeChar = false;
            if (charsToTrim.length == 0)
            {
                if (Character.isWhitespace(string.charAt(index)))
                {
                    lengthToKeep = index;
                    removeChar = true;
                }
            }
            else
            {
                for (char c: charsToTrim)
                {
                    if (string.charAt(index) == c)
                    {
                        lengthToKeep = index;
                        removeChar = true;
                        break;
                    }
                }
            }
            if ( ! removeChar)
                break;
        }
        return string.substring(0, lengthToKeep);
    }

}