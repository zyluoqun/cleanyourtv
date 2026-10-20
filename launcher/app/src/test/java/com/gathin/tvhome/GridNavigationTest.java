package com.gathin.tvhome;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class GridNavigationTest {
    @Test public void horizontalRowReachesFifthAppWithoutWrapping() {
        assertEquals(4, GridNavigation.horizontalTarget(3, 5, false, false));
        assertEquals(4, GridNavigation.horizontalTarget(4, 5, false, false));
        assertEquals(3, GridNavigation.horizontalTarget(4, 5, false, true));
        assertEquals(0, GridNavigation.horizontalTarget(0, 5, false, true));
    }

    @Test public void horizontalRowRespectsReadingDirectionAndSingleApp() {
        assertEquals(1, GridNavigation.horizontalTarget(0, 5, true, true));
        assertEquals(0, GridNavigation.horizontalTarget(0, 5, true, false));
        assertEquals(0, GridNavigation.horizontalTarget(0, 1, false, false));
        assertEquals(0, GridNavigation.horizontalTarget(0, 1, false, true));
    }
    @Test public void hebrewDockStartsOnTheRightAndStopsAtTheLeft() {
        assertTrue(GridNavigation.atHorizontalEdge(0, 11, 4, true, false));
        assertFalse(GridNavigation.atHorizontalEdge(0, 11, 4, true, true));
        assertTrue(GridNavigation.atHorizontalEdge(3, 11, 4, true, true));
        assertFalse(GridNavigation.atHorizontalEdge(3, 11, 4, true, false));
    }

    @Test public void incompleteHebrewLastRowDoesNotWrapIntoEmptySpace() {
        assertTrue(GridNavigation.atHorizontalEdge(8, 11, 4, true, false));
        assertTrue(GridNavigation.atHorizontalEdge(10, 11, 4, true, true));
        assertFalse(GridNavigation.atHorizontalEdge(9, 11, 4, true, true));
        assertFalse(GridNavigation.atHorizontalEdge(9, 11, 4, true, false));
    }

    @Test public void originalLeftToRightBoundariesArePreserved() {
        assertTrue(GridNavigation.atHorizontalEdge(0, 11, 4, false, true));
        assertTrue(GridNavigation.atHorizontalEdge(3, 11, 4, false, false));
        assertTrue(GridNavigation.atHorizontalEdge(10, 11, 4, false, false));
        assertFalse(GridNavigation.atHorizontalEdge(1, 11, 4, false, true));
        assertFalse(GridNavigation.atHorizontalEdge(1, 11, 4, false, false));
    }

    @Test public void singleColumnHasBothHorizontalEdges() {
        assertTrue(GridNavigation.atHorizontalEdge(1, 3, 1, true, true));
        assertTrue(GridNavigation.atHorizontalEdge(1, 3, 1, true, false));
        assertTrue(GridNavigation.atHorizontalEdge(1, 3, 1, false, true));
        assertTrue(GridNavigation.atHorizontalEdge(1, 3, 1, false, false));
    }
}
