/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle;

public enum CacheType {
    NULL(0, R.drawable.cache_default, R.drawable.cache_default_big, 
            R.drawable.pin_default, "null"), 
    MULTI(2, R.drawable.cache_multi, R.drawable.cache_multi_big, 
            R.drawable.pin_multi, "multi"), 
    TRADITIONAL(1, R.drawable.cache_tradi, R.drawable.cache_tradi_big, 
            R.drawable.pin_tradi, "traditional"), 
    UNKNOWN(3, R.drawable.cache_mystery, R.drawable.cache_mystery_big, 
            R.drawable.pin_mystery, "unknown"), 
    MY_LOCATION(4, R.drawable.blue_dot, R.drawable.blue_dot, 
            R.drawable.pin_default, "my location"),

    //Caches without unique icons
    EARTHCACHE(5, R.drawable.cache_earth, R.drawable.cache_earth_big, 
            R.drawable.pin_earth, "earth"),
    VIRTUAL(6, R.drawable.cache_virtual, R.drawable.cache_virtual_big,
            R.drawable.pin_virtual, "virtual"),
    LETTERBOX_HYBRID(7, R.drawable.cache_letterbox, R.drawable.cache_letterbox_big,
            R.drawable.pin_letter, "letterbox"),
    EVENT(8, R.drawable.cache_event, R.drawable.cache_event_big,
            R.drawable.pin_event, "event"),
    WEBCAM(9, R.drawable.cache_webcam, R.drawable.cache_webcam_big,
            R.drawable.pin_webcam, "webcam"),

    //Caches without unique icons
    CITO(10, R.drawable.cache_default, R.drawable.cache_default_big,
            R.drawable.pin_default, "cache in trash out"),
    LOCATIONLESS(11, R.drawable.cache_default, R.drawable.cache_default_big,
            R.drawable.pin_default, "reverse"),
    APE(12, R.drawable.cache_default, R.drawable.cache_default_big,
            R.drawable.pin_default, "project ape"),
    MEGA(13, R.drawable.cache_mega, R.drawable.cache_mega_big,
            R.drawable.pin_mega, "mega-event"),
    WHERIGO(14, R.drawable.cache_default, R.drawable.cache_default_big,
            R.drawable.pin_default, "wherigo"),
            
    //Waypoint types
    WAYPOINT(20, R.drawable.cache_default, R.drawable.cache_default_big, 
            R.drawable.pin_default, "waypoint"),   //Not actually seen in GPX...
    WAYPOINT_PARKING(21, R.drawable.cache_waypoint_p, R.drawable.cache_waypoint_p_big, 
            R.drawable.map_pin2_wp_p, "waypoint|parking area"),
    WAYPOINT_REFERENCE(22, R.drawable.cache_waypoint_r, R.drawable.cache_waypoint_r_big, 
            R.drawable.map_pin2_wp_r, "waypoint|reference point"),
    WAYPOINT_STAGES(23, R.drawable.cache_waypoint_s, R.drawable.cache_waypoint_s_big, 
            R.drawable.map_pin2_wp_s, "waypoint|stages of a multicache"),
    WAYPOINT_TRAILHEAD(24, R.drawable.cache_waypoint_t, R.drawable.cache_waypoint_t_big, 
            R.drawable.map_pin2_wp_t, "waypoint|trailhead"),
    WAYPOINT_FINAL(25, R.drawable.cache_waypoint_r, R.drawable.cache_waypoint_r_big, 
            R.drawable.map_pin2_wp_r, "waypoint|final location");  //TODO: Doesn't have unique graphics yet

    private final int mIconId;
    private final int mIconIdBig;
    private final int mIx;
    private final int mIconIdMap;
    private final String mTag;

    CacheType(int ix, int drawableId, int drawableIdBig, int drawableIdMap, 
            String tag) {
        mIx = ix;
        mIconId = drawableId;
        mIconIdBig = drawableIdBig;
        mIconIdMap = drawableIdMap;
        mTag = tag;
    }

    public int icon() {
        return mIconId;
    }

    public int iconBig() {
        return mIconIdBig;
    }

    public int toInt() {
        return mIx;
    }

    public int iconMap() {
        return mIconIdMap;
    }
    
    public String getTag() {
        return mTag;
    }
}
