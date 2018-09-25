package com.bonaparte.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangmingquan on 2018/9/25.
 */
public class StringTools {

    public static boolean checkSpecialChar(String name, String extra) {
        if (name == null) {
            return false;
        }

        String regEx = "[^\\p{L}\\p{Nd}" + extra + "]";
        Pattern p     = Pattern.compile(regEx);
        Matcher m     = p.matcher(name.toLowerCase());

        return m.find();
    }

    public static String fomartFirstLetter2UpperCase(String name, String formatStr) {
        if ((name != null) &&!name.equals("") && name.replace(" ", "").matches(formatStr)) {

//          logger.info("albumName every word's first char toUpperCase.albumName = " + name);
            String[] albumNameWords = name.split(" ");

            name = "";

            for (int i = 0; i < albumNameWords.length; i++) {
                if (albumNameWords[i].startsWith("(") && (albumNameWords[i].length() >= 2)) {
                    name += albumNameWords[i].substring(0, 1) + albumNameWords[i].substring(1, 2).toUpperCase()
                            + albumNameWords[i].substring(2) + " ";
                } else {
                    name += albumNameWords[i].substring(0, 1).toUpperCase() + albumNameWords[i].substring(1) + " ";
                }
            }

            if (name.endsWith(" ")) {
                StringBuffer buffer = new StringBuffer(name);

                buffer.deleteCharAt(buffer.length() - 1);
                name = buffer.toString();
            }
        }

        return name;
    }

    public static String listToString(List<String> namelist) {
        if (namelist.size() <= 0) {
            return null;
        }

        // 对集合进行排序
        Collections.sort(namelist,
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

        // 规则化歌手字符串
        StringBuffer sb = new StringBuffer();

        for (String artist : namelist) {
            sb.append(artist + ";");
        }

        return sb.substring(0, sb.lastIndexOf(";"));
    }

    public static String stringDecode(String str) {
        if (str == null) {
            return null;
        }

        str = org.springframework.util.StringUtils.trimWhitespace(str);
        str = HtmlUtils.htmlUnescape(str);

        return str;
    }

    public static String toSearchUnicode(String str) {
        String result = "";

        str = str.toLowerCase();

        for (int i = 0; i < str.length(); i++) {
            char sChar = str.charAt(i);

            if (checkSpecialChar(String.valueOf(sChar), "")) {
                result += String.valueOf(sChar);
            } else {
                int    chr1     = (char) sChar;
                String hexStr   = Integer.toHexString(chr1);
                String startStr = "";

                for (int j = 0; j < (4 - hexStr.length()); j++) {
                    startStr += "0";
                }

                result += "u" + startStr + hexStr;
            }
        }

        return result;
    }

    public static String trimSpecialChar(String name, String extra) {
        if (name == null) {
            return null;
        }

        String regEx = "[^\\p{L}\\p{Nd}" + extra + "]";
        Pattern p     = Pattern.compile(regEx);
        Matcher m     = p.matcher(name.toLowerCase());
        String re    = m.replaceAll("").trim();

        if (re.equals("")) {
            return name;
        }

        return re;
    }

    public static List<String> dealNames4Catalog(String[] names, List<String> nameList){
        if(names.length > 0){
            for (String singer : names) {
                String singerTmpName = trimSpecialChar(singer,"");
                if (StringUtils.isEmpty(singerTmpName)) {
                    continue;
                }
//                if (singerTmpName.length() > 128) { // 长度限制
//                    singerTmpName = singerTmpName.substring(0, 128);
//                }
//                String temp = singerTmpName.replaceAll("　| ","").trim();
                String temp = singerTmpName.trim();
                if(!nameList.contains(temp))
                    nameList.add(temp);
            }
        }
        return nameList;
    }

    public static String spliceUrl(String url, Map<String, String> params){
        if(params == null ){
            return url;
        }
        url += "?";
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry)iter.next();
            String value = entry.getValue();
            url += entry.getKey() + "=" + value + "&";
        }
        url = url.substring(0, url.length() - 1);
        System.out.println("远程请求地址为"+url);
        return url;
    }

    public static Map<String,String> getParams(String bodyData){
        Map<String,String> map = new HashMap<String,String>();
        String[] paramStrs = bodyData.split("&");
        for(String param : paramStrs){
            String[] kv = param.split("=");
            map.put(kv[0],kv[1]);
        }
        return map;
    }
}
