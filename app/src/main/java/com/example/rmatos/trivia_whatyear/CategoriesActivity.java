package com.example.rmatos.trivia_whatyear;

import android.support.annotation.Nullable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RMatos on 03/07/2017.
 */

public class CategoriesActivity extends Activity {

    private ExpandableListView expandableListView;
    private ExListAdapter listAdapter;
    private List<String> themes;                                                                            //Parent
    private Map<String, List<String>> categories;                                                           //Parent + child
    private CbCategoriesState cbStates;
    private boolean[] groupLongClickState = new boolean[CbCategoriesState.parentSize];



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Setsup view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        //Gets data passed from previous activity
        Intent activityThatCalled = getIntent();
        cbStates = (CbCategoriesState) activityThatCalled.getSerializableExtra("cbStates");

        //Sets up expandable list view
        expandableListView = (ExpandableListView) findViewById(R.id.expandListView_categories);

        populateListView();
        resetView();
        setListeners();
    }

    public void resetView() {
        listAdapter = new ExListAdapter(this, themes, categories, cbStates);
        expandableListView.setAdapter(listAdapter);
    }

    //Enables all choiceboxes and resets UI
    public void onEnableAll(View view) {
        cbStates =  new CbCategoriesState(true);
        resetView();
    }

    //Disables all choiceboxes and resets UI
    public void onDisableAll(View view) {
        cbStates =  new CbCategoriesState(false);
        resetView();
    }

    private void setListeners() {

/*        //Parent
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                        System.out.println("OUT :: "+ i + ", "+l);
                        return false;
                    }
                });*/

        //Long click listener. Disables/Enables group
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //Below gets group position from
                long packedPosition = expandableListView.getExpandableListPosition(position);
                //int itemType = expandableListView.getPackedPositionType(packedPosition);
                //int childPosition = expandableListView.getPackedPositionChild(packedPosition);
                int groupPosition = expandableListView.getPackedPositionGroup(packedPosition);


                //Below for only when parent is selected. Used to differentiate between parent and child
                CheckBox cb = (CheckBox) view.findViewById(R.id.categories_list_child_checkbox);
                cbStates = listAdapter.getCbStates();
                if (cb == null) {
                    //Resets checkboxes in group
                    boolean changedState = (groupLongClickState[groupPosition]) ? false : true;
                    cbStates.setGroup(groupPosition, changedState);
                    groupLongClickState[groupPosition] = changedState;

                    resetView();
                }

                return true;
            }
        });

        //Child. Enables and disables checkboxes when clicked
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

                        CheckBox cb = (CheckBox) view.findViewById(R.id.categories_list_child_checkbox);

                        boolean changedState = (cb.isChecked()) ? false : true;

                        cb.setChecked(changedState);
                        cbStates.setValue(groupPosition, childPosition, changedState);
                        listAdapter.setCbState(cbStates);


                        return false;
                    }
                });
        //TODO: Add long click listener = http://www.vogella.com/tutorials/AndroidListView/article.html#listadvanced_interactive

    }



    @Override
    public void onBackPressed() {
        Intent goingBack = new Intent();

        goingBack.putExtra("cbStates", listAdapter.getCbStates());

        setResult(RESULT_OK, goingBack);
        finish();
    }

    private void populateListView()
    {
        themes = new ArrayList<>();
        categories = new HashMap<>();

        //Populate parents
        themes.add("Conflict & War");
        themes.add("Crime & Punishment");
        themes.add("Science & Technology");
        themes.add("Politics & Protest (UK)");
        themes.add("Disaster & Tragedy");
        themes.add("Lifestyle, Sport & Entertainment");
        themes.add("World Politics");
        themes.add("Royalty");
        themes.add("Society");

        //Create childs
        List<String> conflictWar = new ArrayList<>();
        conflictWar.add("Northern Ireland");
        conflictWar.add("Africa");
        conflictWar.add("Americas");
        conflictWar.add("Asia-Pacific");
        conflictWar.add("Europe");
        conflictWar.add("South Asia");
        conflictWar.add("Middle East");
        conflictWar.add("World War II");
        List<String> crimePunishment = new ArrayList<>();
        crimePunishment.add("Police");
        crimePunishment.add("Crime");
        crimePunishment.add("Murders");
        crimePunishment.add("Bombings");
        crimePunishment.add("Drugs");
        crimePunishment.add("Kidnapping");
        crimePunishment.add("Hijacking");
        crimePunishment.add("Assassinations");
        crimePunishment.add("Trails & Inquests");
        crimePunishment.add("Miscarriages of Justice");
        crimePunishment.add("Prison");
        List<String> scienceTechnology = new ArrayList<>();
        scienceTechnology.add("Space");
        scienceTechnology.add("Discoveries & Inventions");
        List<String> politicsProtest = new ArrayList<>();
        politicsProtest.add("Elections");
        politicsProtest.add("Political Issues");
        politicsProtest.add("Politicians");
        politicsProtest.add("Protest & Violence");
        politicsProtest.add("Scandal");
        politicsProtest.add("Industrial Disputes");
        politicsProtest.add("Political Parties");
        List<String> disasterTragedy = new ArrayList<>();
        disasterTragedy.add("Natural");
        disasterTragedy.add("Rail");
        disasterTragedy.add("Air");
        disasterTragedy.add("Sea");
        disasterTragedy.add("Disease");
        disasterTragedy.add("Fire");
        disasterTragedy.add("Sporting");
        List<String> lifestyleSportEntertainment = new ArrayList<>();
        lifestyleSportEntertainment.add("Sport");
        lifestyleSportEntertainment.add("Football (soccer)");
        lifestyleSportEntertainment.add("Olympics");
        lifestyleSportEntertainment.add("TV & Radio");
        lifestyleSportEntertainment.add("Film");
        lifestyleSportEntertainment.add("Music");
        lifestyleSportEntertainment.add("Art & Literature");
        lifestyleSportEntertainment.add("Celebrities");
        lifestyleSportEntertainment.add("Achievements");
        lifestyleSportEntertainment.add("Silly Season");
        lifestyleSportEntertainment.add("Tourism");
        List<String> worldPolitics = new ArrayList<>();
        worldPolitics.add("Summits & Agreements");
        worldPolitics.add("Elections");
        worldPolitics.add("Communism");
        worldPolitics.add("Independence");
        worldPolitics.add("Movements");
        worldPolitics.add("Cold War");
        worldPolitics.add("Trade & Industry");
        worldPolitics.add("Politicians");
        worldPolitics.add("Protest & Violence");
        worldPolitics.add("Crises");
        worldPolitics.add("Arms Race");
        worldPolitics.add("US Presidents");
        List<String> royalty = new ArrayList<>();
        royalty.add("Births, Marriages & Deaths");
        royalty.add("Events & Visits");
        royalty.add("Scandal");
        royalty.add("Death of Diana");
        List<String> society = new ArrayList<>();
        society.add("Race");
        society.add("UK Race Relations");
        society.add("Religion");
        society.add("Ceremonies");
        society.add("Education");
        society.add("Health");
        society.add("Jobs");
        society.add("Business");
        society.add("Food & Agriculture");
        society.add("Trade");
        society.add("Transport");
        society.add("Environment");
        society.add("Press");

        //Populate categories
        categories.put(themes.get(0), conflictWar);
        categories.put(themes.get(1), crimePunishment);
        categories.put(themes.get(2), scienceTechnology);
        categories.put(themes.get(3), politicsProtest);
        categories.put(themes.get(4), disasterTragedy);
        categories.put(themes.get(5), lifestyleSportEntertainment);
        categories.put(themes.get(6), worldPolitics);
        categories.put(themes.get(7), royalty);
        categories.put(themes.get(8), society);

        //Initialise groupLongClickState array
        for (int i = 0; i < groupLongClickState.length; i++) {
            groupLongClickState[i] = true;
        }
    }



}
