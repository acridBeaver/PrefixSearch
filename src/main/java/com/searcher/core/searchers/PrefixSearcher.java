package com.searcher.core.searchers;

import java.util.Arrays;
import java.util.Comparator;

public class PrefixSearcher {
    private final PrefixComparator comparator = new PrefixComparator();

    public String[] Search(String[] target, String prefix){
        prefix = prefix.toLowerCase();
        var left = GetLeftIndex(target, prefix);
        if (left == -1)
            return new String[0];
        var right = GetRightIndex(target, prefix, left);
        return Arrays.copyOfRange(target, left, right + 1);
    }

    private int GetLeftIndex(String[] target, String prefix) {
        var left = 0;
        var right = target.length - 1;
        while (right - 1 > left){
            var mid = (left + right) / 2;
            if (comparator.compare(prefix, target[mid]) > 0)
                left = mid;
            else
                right = mid;
        }

        if (comparator.compare(prefix, target[right]) != 0) {
            return -1;
        }
        return right;
    }

    private int GetRightIndex(String[] target, String prefix, int left) {
        var right = target.length - 1;
        while (right - 1 > left){
            var mid = (left + right) / 2;
            if (comparator.compare(prefix, target[mid]) >= 0)
                left = mid;
            else
                right = mid;
        }

        return left;
    }

    private static class PrefixComparator implements Comparator<String> {
        @Override
        public int compare(String prefix, String str) {
            var index = Math.min(prefix.length(), str.length());
            var comparable = str.substring(0, index).toLowerCase();
            return prefix.compareTo(comparable);
        }
    }
}
