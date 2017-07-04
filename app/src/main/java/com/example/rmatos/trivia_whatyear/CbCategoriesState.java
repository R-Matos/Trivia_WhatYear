package com.example.rmatos.trivia_whatyear;

import java.io.Serializable;

/**
 * Created by RMatos on 04/07/2017.
 */

public class CbCategoriesState implements Serializable {

    private static final long serialVersionUID = 1L;
    public final static int parentSize = 9;
    public final static int childSize = 13;
    private boolean[][] array;



    public CbCategoriesState(boolean defaultState) {

        array = new boolean[parentSize][childSize];

        for (int i = 0; i < parentSize; i++) {
            for (int j = 0; j < childSize; j++) {
                this.array[i][j] = defaultState;
            }
        }
    }



    public void setValue(int parent, int child, boolean state) {
        this.array[parent][child] = state;
    }

    public boolean getValue(int parent, int child) {
        return this.array[parent][child];
    }

    public void setGroup(int groupPosition, boolean value) {

        for (int j = 0; j < childSize; j++) {
            array[groupPosition][j] = value;
        }
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


    public static String Serialize(CbCategoriesState obj) {

        String str = "";

        for (int i = 0; i < parentSize; i++) {
            for (int j = 0; j < childSize; j++) {
                str += ", " + obj.getValue(i,j);
            }
        }
        //System.out.println(str);
        return str;
    }

    public static CbCategoriesState Deserialize(String serializedString) {

        CbCategoriesState obj = new CbCategoriesState(true);

        //Default

        for (int i = 0; i < parentSize; i++) {
            for (int j = 0; j < childSize; j++) {
                //Below checks whether its going to be true or false
                String booleanCheck = serializedString.substring(2, 3);
                //System.out.println(booleanCheck);

                int subStringLength;

                if (booleanCheck.equals("t")) {
                    subStringLength = 4;
                } else {
                    subStringLength = 5;
                }

                //Below gets value and reduces string
                //System.out.println(serializedString.substring(2,subStringLength+2));
                Boolean value = Boolean.parseBoolean(serializedString.substring(2,subStringLength+2));
                serializedString = serializedString.substring(subStringLength+2);
                //System.out.println(serializedString);

                obj.setValue(i, j, value);
            }
        }

        return obj;
    }


}
