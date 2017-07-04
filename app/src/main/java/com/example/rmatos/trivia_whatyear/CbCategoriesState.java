package com.example.rmatos.trivia_whatyear;

import java.io.Serializable;

/**
 * Created by RMatos on 04/07/2017.
 */

public class CbCategoriesState implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int parentSize = 9;
    private static int childSize = 13;
    private boolean[][] array;



    public CbCategoriesState() {

        array = new boolean[parentSize][childSize];

        for (int i = 0; i < parentSize; i++) {
            for (int j = 0; j < childSize; j++) {
                this.array[i][j] = true;
            }
        }
    }

    public void setValue(int parent, int child, boolean state) {
        this.array[parent][child] = state;
    }

    public boolean getValue(int parent, int child) {
        return this.array[parent][child];
    }

    @Override
    public String toString() {
        String str = "";

        for (int i = 0; i < parentSize; i++) {
            for (int j = 0; j < childSize; j++) {
                str += ", " + array[i][j];
            }
        }

        return str;
    }


}
