package com.wuxia.liuxing.common;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Constant {
    public static final Prop config = PropKit.use("config.properties");

    public static Set<String> sendedSet = new HashSet<String>();

}
