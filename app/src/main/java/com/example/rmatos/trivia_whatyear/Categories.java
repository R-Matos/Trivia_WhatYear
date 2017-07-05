package com.example.rmatos.trivia_whatyear;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by RMatos on 04/07/2017.
 */

public class Categories implements Serializable {

    private static final long serialVersionUID = 1L;
    public final static int parentSize = 9;
    public final static int childSize = 13;
    private boolean[][] array;



    public Categories(boolean defaultState) {

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


    public ArrayList<String> getSelected() {

        ArrayList<String> categories = new ArrayList<>();

        for (int i = 0; i < parentSize; i++) {
            for (int j = 0; j < childSize; j++) {

                if (array[i][j])
                {
                    if (i == 0)
                    {
                        switch (j)
                        {
                            case 0 : categories.add("Northern Ireland");    break;
                            case 1 : categories.add("Africa");              break;
                            case 2 : categories.add("Americas");            break;
                            case 3 : categories.add("Asia=Pacific");        break;
                            case 4 : categories.add("Europe");              break;
                            case 5 : categories.add("South Asia");          break;
                            case 6 : categories.add("Middle East");         break;
                            case 7 : categories.add("World War II");        break;
                        }
                    }
                    else if (i == 1)
                    {
                        switch (j)
                        {
                            case 0 : categories.add("Police");                      break;
                            case 1 : categories.add("Crime");                       break;
                            case 2 : categories.add("Murders");                     break;
                            case 3 : categories.add("Bombings");                    break;
                            case 4 : categories.add("Drugs");                       break;
                            case 5 : categories.add("Kidnapping");                  break;
                            case 6 : categories.add("Hijacking");                   break;
                            case 7 : categories.add("Assassinations");              break;
                            case 8 : categories.add("Trials & Inquests");           break;
                            case 9 : categories.add("Miscarriages of Justice");     break;
                            case 10 : categories.add("Prison");                     break;
                        }
                    }
                    else if (i == 2)
                    {
                        switch (j)
                        {
                            case 0 : categories.add("Space");                       break;
                            case 1 : categories.add("Discoveries & Inventions");    break;
                        }
                    }
                    else if (i == 3)
                    {
                        switch (j)
                        {
                            case 0 : categories.add("Elections");               break;
                            case 1 : categories.add("Political Issues");        break;
                            case 2 : categories.add("Politicians");             break;
                            case 3 : categories.add("Protest & Violence");      break;
                            case 4 : categories.add("Scandal");                 break;
                            case 5 : categories.add("Industrial Disputes");     break;
                            case 6 : categories.add("Political Parties");       break;
                        }
                    }
                    else if (i == 4)
                    {
                        switch (j)
                        {
                            case 0 : categories.add("Natural");     break;
                            case 1 : categories.add("Rail");        break;
                            case 2 : categories.add("Air");         break;
                            case 3 : categories.add("Sea");         break;
                            case 4 : categories.add("Disease");     break;
                            case 5 : categories.add("Fire");        break;
                            case 6 : categories.add("Sporting");    break;

                        }
                    }
                    else if (i == 5)
                    {
                        switch (j)
                        {
                            case 0  : categories.add("Sport");               break;
                            case 1  : categories.add("Football");            break;
                            case 2  : categories.add("Olympics");            break;
                            case 3  : categories.add("TV & Radio");          break;
                            case 4  : categories.add("Film");                break;
                            case 5  : categories.add("Music");               break;
                            case 6  : categories.add("Art & Literature");    break;
                            case 7  : categories.add("Celebrities");         break;
                            case 8  : categories.add("Achievements");        break;
                            case 9  : categories.add("Silly Season");        break;
                            case 10 : categories.add("Tourism");             break;

                        }
                    }
                    else if (i == 6)
                    {
                        switch (j)
                        {
                            case 0  : categories.add("Summits & Agreements");    break;
                            case 1  : categories.add("Elections");               break;
                            case 2  : categories.add("Communism");               break;
                            case 3  : categories.add("Independence");            break;
                            case 4  : categories.add("Movements");               break;
                            case 5  : categories.add("Cold War");                break;
                            case 6  : categories.add("Trade & Industry");        break;
                            case 7  : categories.add("Politicians");             break;
                            case 8  : categories.add("Protest & Violence");      break;
                            case 9  : categories.add("Crises");                  break;
                            case 10 : categories.add("Arms Race");               break;
                            case 11 : categories.add("US Presidents");           break;
                        }
                    }
                    else if (i == 7)
                    {
                        switch (j)
                        {
                            case 0  : categories.add("Births, Marriages & Deaths");     break;
                            case 1  : categories.add("Events & Visits");                break;
                            case 2  : categories.add("Scandal");                        break;
                            case 3  : categories.add("Death of Diana");                 break;
                        }
                    }
                    else if (i == 8)
                    {
                        switch (j)
                        {
                            case 0  : categories.add("Race");                   break;
                            case 1  : categories.add("UK Race Relations");      break;
                            case 2  : categories.add("Religion");               break;
                            case 4  : categories.add("Ceremonies");             break;
                            case 5  : categories.add("Education");              break;
                            case 6  : categories.add("Health");                 break;
                            case 7  : categories.add("Jobs");                   break;
                            case 8  : categories.add("Business");               break;
                            case 9  : categories.add("Food & Agriculture");     break;
                            case 10 : categories.add("Trade");                  break;
                            case 11 : categories.add("Transport");              break;
                            case 12 : categories.add("Environment");            break;
                            case 13 : categories.add("Press");                  break;
                        }
                    }
                }
            }
        }

        return categories;
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


    public static String Serialize(Categories obj) {

        String str = "";

        for (int i = 0; i < parentSize; i++) {
            for (int j = 0; j < childSize; j++) {
                str += ", " + obj.getValue(i,j);
            }
        }
        //System.out.println(str);
        return str;
    }

    public static Categories Deserialize(String serializedString) {

        Categories obj = new Categories(true);

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
