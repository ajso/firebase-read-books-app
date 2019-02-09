package net.icraftsystems.app.auth.android.comicfirebase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer;

import net.icraftsystems.app.auth.android.comicfirebase.Adapter.MyViewPagerAdapter;
import net.icraftsystems.app.auth.android.comicfirebase.Common.Common;
import net.icraftsystems.app.auth.android.comicfirebase.Model.Chapter;

public class ViewComicActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView txt_chapter_name;
    View back, next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comic);

        viewPager= (ViewPager)findViewById(R.id.view_pager);
        txt_chapter_name =(TextView)findViewById(R.id.txt_chapter_name);
        back =findViewById(R.id.chapter_back);
        next =findViewById(R.id.chapter_next);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.chapterIndex == 0)
                    Toast.makeText(ViewComicActivity.this, "You are reading the first chapter", Toast.LENGTH_LONG).show();
                else{
                    Common.chapterIndex--;
                    fetchLinks(Common.chapterList.get(Common.chapterIndex));
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.chapterIndex == Common.chapterList.size()-1)
                    Toast.makeText(ViewComicActivity.this, "You are reading the last chapter", Toast.LENGTH_LONG).show();
                else{
                    Common.chapterIndex++;
                    fetchLinks(Common.chapterList.get(Common.chapterIndex));
                }
            }
        });
        fetchLinks(Common.chapterSelected);
    }

    private void fetchLinks(Chapter chapter) {

        if (chapter.Links != null){
                if (chapter.Links.size() >0){
                    MyViewPagerAdapter adapter = new MyViewPagerAdapter(getBaseContext(),chapter.Links);
                    viewPager.setAdapter(adapter);

                    txt_chapter_name.setText(Common.formatString(Common.chapterSelected.Name));

                    //Animation
                    BookFlipPageTransformer bookFlipPageTransformer = new BookFlipPageTransformer();
                    bookFlipPageTransformer.setScaleAmountPercent(10f);
                    viewPager.setPageTransformer(true,bookFlipPageTransformer);

                }else{
                    Toast.makeText(ViewComicActivity.this, "No Image here", Toast.LENGTH_LONG).show();
                }

        }else{
            Toast.makeText(ViewComicActivity.this, "This Chapter is Translating...", Toast.LENGTH_LONG).show();

        }
    }
}
