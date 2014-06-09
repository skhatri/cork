package com.github.skhatri;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Utils {
    public static Function<Object, String> toStr = (o) -> o == null ? null : o.toString();
    public static Function<Object, List> toList = (o) -> o instanceof List ? List.class.cast(o) : null;
}
