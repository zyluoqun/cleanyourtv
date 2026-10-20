package com.gathin.tvhome;

/** Physical left/right boundaries for a grid whose first column follows its reading direction. */
final class GridNavigation {
    private GridNavigation() { }

    static int horizontalTarget(int position, int count, boolean rtl, boolean left) {
        return Math.max(0, Math.min(count - 1, position + (left != rtl ? -1 : 1)));
    }

    static boolean atHorizontalEdge(int position, int count, int columns, boolean rtl, boolean left) {
        boolean towardStart = left != rtl;
        if (towardStart) return position % columns == 0;
        return position % columns == columns - 1 || position == count - 1;
    }
}
