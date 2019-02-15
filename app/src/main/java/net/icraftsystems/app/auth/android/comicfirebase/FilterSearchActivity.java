package net.icraftsystems.app.auth.android.comicfirebase;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.icraftsystems.app.auth.android.comicfirebase.Adapter.MyComicAdapter;
import net.icraftsystems.app.auth.android.comicfirebase.Common.Common;
import net.icraftsystems.app.auth.android.comicfirebase.Model.Comic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterSearchActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recycler_search_filter;
    AlertDialog.Builder alertDialog;

    LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        recycler_search_filter=findViewById(R.id.recycler_filter_search);
        recycler_search_filter.setHasFixedSize(true);
        recycler_search_filter.setLayoutManager(new GridLayoutManager(this,2));

        bottomNavigationView=findViewById(R.id.btn_nav);
        bottomNavigationView.inflateMenu(R.menu.main_menu);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.action_filter:
                        showFilterDiaog();
                    break;
                    case R.id.action_search:
                        showSearchDiaog();
                        break;
                    default:
                        break;

                }

            }
        });
    }

    private void showSearchDiaog() {

        alertDialog=new AlertDialog.Builder(FilterSearchActivity.this);
        alertDialog.setTitle("Search");

        inflater = this.getLayoutInflater();
        View search_layout = inflater.inflate(R.layout.dialog_search, null);

        final EditText edit_search = search_layout.findViewById(R.id.edit_search);
        alertDialog.setView(search_layout);

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                fetchSearchedComic(edit_search.getText().toString());
            }
        });
        alertDialog.show();



    }

    private void fetchSearchedComic(String query) {

        List<Comic> search_comic = new ArrayList<>();
        for (Comic comic:Common.comicList){
            if(comic.Name.contains(query))
                search_comic.add(comic);
        }
        if (search_comic.size() > 0)
            recycler_search_filter.setAdapter(new MyComicAdapter(getBaseContext(),search_comic));
        else
            Toast.makeText(this,"No Result Found", Toast.LENGTH_LONG).show();
    }

    private void showFilterDiaog() {
        alertDialog=new AlertDialog.Builder(FilterSearchActivity.this);
        alertDialog.setTitle("Select Category");

        inflater = this.getLayoutInflater();
        View inflater_layout = inflater.inflate(R.layout.dialog_options, null);

        final AutoCompleteTextView txt_category = inflater_layout.findViewById(R.id.txt_category);
        final ChipGroup chipGroup = inflater_layout.findViewById(R.id.chip_group);

        //Creating an AutoComplete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, Common.categories);
        txt_category.setAdapter(adapter);
        txt_category.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //clear
                txt_category.setText("");

                //create tags
                Chip chip = (Chip) inflater.inflate(R.layout.chip_item, null, false);
                chip.setText(((TextView)view).getText());
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            chipGroup.removeView(view);
                    }
                });

                chipGroup.addView(chip);
            }
        });

        alertDialog.setView(inflater_layout);
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                List<String> filter_key = new ArrayList<>();
                StringBuilder filter_query = new StringBuilder("");
                for (int j = 0; j<chipGroup.getChildCount(); j++){

                    Chip chip = (Chip)chipGroup.getChildAt(j);
                    filter_key.add(chip.getText().toString());

                    Collections.sort(filter_key);
                    //convert list to string
                    for (String key:filter_key){

                        filter_query.append(key).append(",");
                    }
                    //remove last ","
                    filter_query.setLength(filter_query.length()-1);
                    //filter by this querry
                    fetchFilterCategory(filter_query.toString());
                }
            }
        });
        alertDialog.show();
    }

    private void fetchFilterCategory(String query) {
        List<Comic> comic_filtered = new ArrayList<>();
        for (Comic comic:Common.comicList) {

            if (comic.Category != null) {
                if (comic.Category.contains(query))
                    comic_filtered.add(comic);
            }
        }
        if (comic_filtered.size() > 0)
        recycler_search_filter.setAdapter(new MyComicAdapter(getBaseContext(),comic_filtered));
        else
            Toast.makeText(this,"No Comic Found", Toast.LENGTH_LONG).show();
    }
}
