package com.coldlion.mobilenew.type;

/**
 * Description:
 * ===========================
 * Author：           Bill Wang
 * Create Date：  2014/12/15
 */
public class GridLayoutColumn {

    public enum UnitType {
        weight, auto
    };

    static public class GridLength {

        private final double width;
        private final UnitType type;

        public GridLength(double width, UnitType type) {
            this.width = width;
            this.type = type;
        }
    }

    public GridLength Width;
}
