package com.sim.scar;

import java.util.List;
import java.util.Objects;

/**
 * Created by sun on 5/12/15.
 */
public class Utils {
    public static Boolean isBlank(List list) {
        return list == null || list.size() == 0;
    }

    public static Boolean isPresent(List list) {
        return !isBlank(list);
    }

    public static Boolean isBlank(Object[] list) {
        return list == null || list.length == 0;
    }

    public static Boolean isPresent(Object[] list) {
        return !isBlank(list);
    }
}
