package com.update.updatelib.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kim on 2018/1/26.
 */

public class StringUtils {

    public static Map<Integer, String> readAssetsTxt(Context context, String fileName){
        BufferedReader br ;
        Map<Integer, String> map = new HashMap<Integer, String>();
        InputStreamReader inputStreamReader;
        String lineTxt;
        try {
            int count=0;
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName+".txt");
            inputStreamReader = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(inputStreamReader);

            while ((lineTxt = br.readLine()) != null) {     //
                if (!"".equals(lineTxt)) {
                    map.put(count,lineTxt);
                    count++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
