package com.egorvaskon.paranoid.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.Utils;
import com.egorvaskon.paranoid.ui.adapter.MainFragmentPagerAdapter;
import com.egorvaskon.paranoid.ui.fragment.EditKeyDialogFragment;
import com.egorvaskon.paranoid.ui.fragment.MessageDialog;
import com.egorvaskon.paranoid.ui.viewmodel.KeysViewModel;
import com.egorvaskon.paranoid.ui.viewmodel.SecretsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
public class MainActivity extends AppCompatActivity {

    public static final String CREATE_QUESTION_FRAGMENT_TAG = "create_question_fm_tag_1231";
    public static final String EDIT_QUESTION_FRAGMENT_TAG = "edit_question_fm_tag_556";
    public static final String MESSAGE_DIALOG_TAG = "msg_dialog_tag_165";

    public static final String EXTRA_SHOW_MESSAGE_DIALOG = "extra_flag_show_message_dialog";

    private SecretsViewModel mSecretsViewModel;
    private KeysViewModel mKeysViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewModelProvider vmProvider = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()));
        mSecretsViewModel = vmProvider.get(SecretsViewModel.class);
        mKeysViewModel = vmProvider.get(KeysViewModel.class);

        FloatingActionButton mainFab = findViewById(R.id.fab);
        TabLayout mainTabs = findViewById(R.id.mainTabs);
        ViewPager mainPager = findViewById(R.id.mainViewPager);

        mainPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(),0));
        mainTabs.setupWithViewPager(mainPager);

        mainFab.setOnClickListener(v -> {
            if(mainPager.getCurrentItem() == 0)
                startActivity(new Intent(this, EditSecretActivity.class));
            else if(mainPager.getCurrentItem() == 1)
                Utils.showDialog(getSupportFragmentManager(),new EditKeyDialogFragment(),CREATE_QUESTION_FRAGMENT_TAG);
        });

        if(getIntent() != null && getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey(EXTRA_SHOW_MESSAGE_DIALOG)){
                Utils.showDialog(getSupportFragmentManager(),
                        MessageDialog.newInstance(getIntent().getIntExtra(EXTRA_SHOW_MESSAGE_DIALOG,0)),
                        MESSAGE_DIALOG_TAG);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(getIntent() != null && getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey(EXTRA_SHOW_MESSAGE_DIALOG)){
                Utils.showDialog(getSupportFragmentManager(),
                        MessageDialog.newInstance(getIntent().getIntExtra(EXTRA_SHOW_MESSAGE_DIALOG,0)),
                        MESSAGE_DIALOG_TAG);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();

        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}