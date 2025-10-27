package com.tourism.gui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * A custom layout manager that extends FlowLayout.
 * This layout manager makes components "wrap" to the next line
 * when they hit the width of the container, which is perfect
 * for our destination cards panel inside a JScrollPane.
 */
public class WrapLayout extends FlowLayout {

    /**
     * Constructs a new WrapLayout.
     */
    public WrapLayout() {
        super();
    }

    /**
     * Constructs a new WrapLayout with the specified alignment.
     * @param align the alignment value
     */
    public WrapLayout(int align) {
        super(align);
    }

    /**
     * Constructs a new WrapLayout with the specified alignment and gaps.
     * @param align the alignment value
     * @param hgap  the horizontal gap
     * @param vgap  the vertical gap
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Returns the preferred dimensions for this layout.
     * @param target the container in which to do the layout
     * @return the preferred dimensions to lay out the subcomponents
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Returns the minimum dimensions for this layout.
     * @param target the container in which to do the layout
     * @return the minimum dimensions to lay out the subcomponents
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension min = layoutSize(target, false);
        min.width -= (getHgap() + 1); // Adjust for gaps
        return min;
    }

    /**
     * Calculates the dimensions for the layout.
     * @param target the container
     * @param preferred whether to calculate preferred or minimum size
     * @return the dimensions
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            // Get the container's width (or a large number if no width)
            int targetWidth = target.getSize().width;
            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGaps = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGaps;

            // Internal dimensions
            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    // Check if component fits in the current row
                    if (rowWidth + d.width > maxWidth && rowWidth > 0) {
                        // Doesn't fit, start a new row
                        dim.width = Math.max(dim.width, rowWidth);
                        dim.height += rowHeight + vgap;
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    // Add component to the current row
                    rowWidth += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            // Add the last row's dimensions
            dim.width = Math.max(dim.width, rowWidth);
            dim.height += rowHeight + vgap;

            // Add insets
            dim.width += horizontalInsetsAndGaps;
            dim.height += insets.top + insets.bottom + vgap;

            return dim;
        }
    }
}