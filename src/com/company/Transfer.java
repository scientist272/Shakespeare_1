package com.company;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Transfer {
    public static final BlockingDeque<Map<String, List<Integer>>> buffer = new LinkedBlockingDeque<>();
}
