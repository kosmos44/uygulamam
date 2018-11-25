package com.example.kosmos.netkpss;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.kosmos.netkpss.Adapter.AnswerSheetAdapter;
import com.example.kosmos.netkpss.Common.Common;
import com.example.kosmos.netkpss.DBHelper.DBHelper;
import com.example.kosmos.netkpss.Model.CurrentQuestion;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import java.util.concurrent.TimeUnit;


public class QuestionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int time_play = Common.TOTAL_TIME;
    boolean isAnswerModeView = false;

    RecyclerView answer_sheet_view;
    AnswerSheetAdapter answerSheetAdapter;
    TextView txt_right_answer,txt_timer;
    @Override
    protected void onDestroy() {
        if(Common.countDownTimer != null)
            Common.countDownTimer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Common.selectedCategory.getName());
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        takeQuestion();
        if (Common.question1List.size()>0) {
            txt_right_answer = (TextView)findViewById(R.id.txt_question_right);
            txt_timer = (TextView)findViewById(R.id.txt_timer);
            txt_timer.setVisibility(View.VISIBLE);
            txt_right_answer.setVisibility(View.VISIBLE);
            txt_right_answer.setText(new StringBuilder(String.format("%d/%d",Common.right_answer_count,Common.question1List.size())));

            countTimer();
            answer_sheet_view = (RecyclerView) findViewById(R.id.grid_answer);
            answer_sheet_view.setHasFixedSize(true);
            if (Common.question1List.size() > 5)
                answer_sheet_view.setLayoutManager(new GridLayoutManager(this, Common.question1List.size() / 2));
            answerSheetAdapter = new AnswerSheetAdapter(this, Common.answerSheetList);
            answer_sheet_view.setAdapter(answerSheetAdapter);

        }
    }



    private void countTimer() {
        if (Common.countDownTimer == null)
        {
            Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txt_timer.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(1) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(1))));
                    time_play-=1000;
                }
                @Override
                public void onFinish() {
                }
            }.start();
        }
else
        {
            Common.countDownTimer.cancel();
            Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txt_timer.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(1) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(1))));
                    time_play-=1000;

                }

                @Override
                public void onFinish() {

                }
            }.start();
        }
    }

    private void takeQuestion() {
        Common.question1List = DBHelper.getInstance(this).getQuestionByCategory(Common.selectedCategory.getId());
        if (Common.question1List.size()==0){
            new MaterialStyledDialog.Builder(this)
                     .setTitle("Hata!")
                    .setIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                    .setDescription("Bu konuda Hiç bir sorumuz yok" +Common.selectedCategory.getName()+"category")
                    .setPositiveText("TAMAM")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                        }
                    }).show();
        }
        else
            {
                if (Common.answerSheetList.size() > 0 )
                    Common.answerSheetList.clear();
                for (int i = 0; i<Common.question1List.size();i++)
                {
                    Common.answerSheetList.add(new CurrentQuestion(i,Common.ANSWER_TYPE.NO_ANSWER));
                }
            }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
