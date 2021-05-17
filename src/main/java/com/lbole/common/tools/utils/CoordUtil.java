/*
 *  The lBole licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lbole.common.tools.utils;

/**
 * @Author 马嘉祺
 * @Date 2019/12/10 0010 10 26
 * @Description <p></p>
 */
public final class CoordUtil {
    
    /**
     * 圆周率
     */
    private static final double PI = Math.PI;
    
    /**
     * 地球的半径
     */
    private static final double R = 6378245.0;
    
    /**
     * 中国地理坐标范围
     */
    private static final double CHINA_MIN_LON = 72.004, CHINA_MAX_LON = 137.8347;
    
    private static final double CHINA_MIN_LAT = 0.8293, CHINA_MAX_LAT = 55.8271;
    
    private CoordUtil() {
    }
    
    /**
     * 判断坐标点是否在中国
     *
     * @param lon
     * @param lat
     * @return
     */
    public static boolean outOfChina(double lon, double lat) {
        return lon < CHINA_MIN_LON || lon > CHINA_MAX_LON || lat < CHINA_MIN_LAT || lat > CHINA_MAX_LAT;
    }
    
    /**
     * 两点间距离
     *
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return
     */
    public static double distance(double lon1, double lat1, double lon2, double lat2) {

//        int earthR = 6371000;
//        double x = Math.cos(latA * Math.PI / 180) * Math.cos(latB * Math.PI / 180) * Math.cos((logA - logB) * Math.PI / 180);
//        double y = Math.sin(latA * Math.PI / 180) * Math.sin(latB * Math.PI / 180);
//        double s = x + y;
//        if (s > 1) s = 1;
//        if (s < -1) s = -1;
//        double alpha = Math.acos(s);
//        double distance = alpha * earthR;
        
        
        lon1 = Math.toRadians(lon1);
        lat1 = Math.toRadians(lat1);
        lon2 = Math.toRadians(lon2);
        lat2 = Math.toRadians(lat2);
        double d1 = Math.abs(lon1 - lon2);
        double d2 = Math.abs(lat1 - lat2);
        double p = Math.pow(Math.sin(d2 / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(d1 / 2), 2);
        
        return R * 2 * Math.asin(Math.sqrt(p));
    }
    
    /**
     * 地球坐标系转火星坐标系
     *
     * @param lon
     * @param lat
     * @return
     */
    public static double[] wgs84ToGcj02(double lon, double lat) {
        
        return null;
    }
    
    /**
     * 火星坐标系转地球坐标系
     *
     * @param lon
     * @param lat
     * @return
     */
    public static double[] gcj02ToWgs84(double lon, double lat) {
        
        return null;
    }
    
    /**
     * 地球坐标系转百度坐标系
     *
     * @param lon
     * @param lat
     * @return
     */
    public static double[] wgs84ToBd09(double lon, double lat) {
        return null;
    }
    
    /**
     * 百度坐标系转地球坐标系
     *
     * @param lon
     * @param lat
     * @return
     */
    public static double[] bd09ToWgs84(double lon, double lat) {
        return null;
    }
    
    /**
     * 火星坐标系转百度坐标系
     *
     * @param lon
     * @param lat
     * @return
     */
    public static double[] gcj09ToBd09(double lon, double lat) {
        return null;
    }
    
    /**
     * 百度坐标系转火星坐标系
     *
     * @param lon
     * @param lat
     * @return
     */
    public static double[] bd09ToGcj09(double lon, double lat) {
        return null;
    }
    
}
