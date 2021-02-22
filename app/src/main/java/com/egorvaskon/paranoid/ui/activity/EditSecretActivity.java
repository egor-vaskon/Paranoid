package com.egorvaskon.paranoid.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import com.egorvaskon.paranoid.DecodedSecret;
import com.egorvaskon.paranoid.DisposableManager;
import com.egorvaskon.paranoid.Key;
import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.Secret;
import com.egorvaskon.paranoid.Utils;
import com.egorvaskon.paranoid.ui.fragment.ContentLoadingFragment;
import com.egorvaskon.paranoid.ui.fragment.EditSecretFragment;
import com.egorvaskon.paranoid.ui.fragment.QuizDialog;
import com.egorvaskon.paranoid.ui.viewmodel.EditSecretViewModel;
import com.egorvaskon.paranoid.ui.viewmodel.KeysViewModel;
import com.egorvaskon.paranoid.ui.viewmodel.QuizViewModel;
import com.egorvaskon.paranoid.ui.viewmodel.SecretsViewModel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EditSecretActivity extends AppCompatActivity {

    public static final String EXTRA_SECRET_ID = "extra_secret_id";
    public static final String EXTRA_ANSWERS = "extra_answers";

    public static final String QUIZ_VIEW_MODEL = "quiz_view_model";
    public static final String ENCODING_DIALOG_TAG = "encoding_secret";

    private long mSecretId;
    private boolean mCreate = true;

    private boolean mSaveButtonEnabled = false;
    private boolean mSaveButtonVisible = true;

    private DecodedSecret mDecodedSecret;

    private KeysViewModel mKeysViewModel;
    private SecretsViewModel mSecretsViewModel;

    private EditSecretViewModel mEditSecretViewModel;

    private QuizViewModel mQuizViewModel;
    private Handler mUiHandler;

    private final DisposableManager mDisposableManager = new DisposableManager();

    private List<Key> mKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_secret);

        Toolbar toolbar = findViewById(R.id.edit_secret_toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mDisposableManager.dispose();
        mKeysViewModel = null;
        mSecretsViewModel = null;
        mEditSecretViewModel = null;
        mQuizViewModel = null;
        mUiHandler = null;
        mKeys = null;
        mDecodedSecret = null;
        mSaveButtonEnabled = false;
        mSaveButtonVisible = true;
        mCreate = true;
        mSecretId = -1;

        invalidateOptionsMenu();
        init();
    }

    private void init(){
        mUiHandler = new Handler();

        if(getIntent() != null){
            if(getIntent().getExtras() != null){
                if(getIntent().getExtras().containsKey(EXTRA_SECRET_ID)){
                    mCreate = false;
                    mSecretId = getIntent().getExtras().getLong(EXTRA_SECRET_ID);
                }
            }
        }

        ViewModelProvider androidVmProvider = new ViewModelProvider(this
                ,new ViewModelProvider.AndroidViewModelFactory(this.getApplication()));

        ViewModelProvider vmProvider = new ViewModelProvider(this);

        mKeysViewModel = androidVmProvider.get(KeysViewModel.class);
        mSecretsViewModel = androidVmProvider.get(SecretsViewModel.class);

        mQuizViewModel = vmProvider.get(QUIZ_VIEW_MODEL,QuizViewModel.class);

        mEditSecretViewModel = vmProvider.get(EditSecretViewModel.class);

        if(mCreate)
            initAsCreateSecretActivity();
        else
            initAsEditSecretActivity();
    }

    private void initAsCreateSecretActivity(){
        mEditSecretViewModel.getDecodedSecret().observe(this,secret -> {
            if(secret.getName() != null && !mSaveButtonEnabled){
                mSaveButtonEnabled = true;
                mDecodedSecret = secret;
                invalidateOptionsMenu();
            }
            else if(secret.getName() == null && mSaveButtonEnabled){
                mSaveButtonEnabled = false;
                invalidateOptionsMenu();
            }
        });

        mKeysViewModel.getKeysLiveData().observe(this,keys -> mKeys = keys);
        mQuizViewModel.getAnswers().observe(this,this::onAnswersAvailable);

        EditSecretFragment fragment = new EditSecretFragment();

        Utils.addOrReplaceFragment(this,fragment,R.id.fragment_container);
    }

    private void initAsEditSecretActivity(){
        ArrayList<String> answers = getIntent().getExtras().getStringArrayList(EXTRA_ANSWERS);

        Utils.addOrReplaceFragment(this,new ContentLoadingFragment(),R.id.fragment_container);

        Disposable d = decodeSecret(mSecretId,answers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(secret -> {
                    EditSecretFragment fragment = EditSecretFragment.newInstance(secret);

                    Utils.addOrReplaceFragment(this,fragment,R.id.fragment_container);
                },err -> {
                    Intent intent = new Intent(this,MainActivity.class);
                    intent.putExtra(MainActivity.EXTRA_SHOW_MESSAGE_DIALOG,R.string.decoding_failed);

                    startActivity(intent);
                });

        mEditSecretViewModel.getDecodedSecret().observe(this,secret -> {
            if(secret.getName() != null && !mSaveButtonEnabled){
                mSaveButtonEnabled = true;
                mDecodedSecret = secret;
                invalidateOptionsMenu();
            }
            else if(secret.getName() == null && mSaveButtonEnabled){
                mSaveButtonEnabled = false;
                invalidateOptionsMenu();
            }
        });

        mKeysViewModel.getKeysLiveData().observe(this,keys -> mKeys = keys);
        mQuizViewModel.getAnswers().observe(this,this::onAnswersAvailable);

        mDisposableManager.pushDisposable(d);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_secret,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_next){
            onDecodedSecretCreated();
            return true;
        }

        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);

        int colorId = mSaveButtonEnabled ? R.color.colorPrimary : R.color.disabled_color;

        SpannableString text = new SpannableString(item.getTitle());
        text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,colorId)),0,text.length(),0);
        item.setTitle(text);

        item.setEnabled(mSaveButtonEnabled);
        item.setVisible(mSaveButtonVisible);

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDisposableManager.dispose();
    }

    private void onDecodedSecretCreated(){
        Disposable d = mKeysViewModel.getQuestions(new ArrayList<>(mDecodedSecret.getKeys()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(questions -> {
                    QuizDialog dialog
                            = QuizDialog.newInstance(QUIZ_VIEW_MODEL,questions);

                    dialog.show(getSupportFragmentManager(),ENCODING_DIALOG_TAG);
                });

        mDisposableManager.pushDisposable(d);
    }

    @NonNull
    private Single<DecodedSecret> decodeSecret(long id,@NonNull List<String> answers){
        return mSecretsViewModel.getSecret(id)
                .flatMap(secret -> decodeSecret(secret,answers));
    }

    @NonNull
    private Single<DecodedSecret> decodeSecret(@NonNull Secret secret,@NonNull List<String> answers){
        return secret.decode(answers)
                .map(rawData -> new DecodedSecret(secret.getName(),
                        new String(rawData,StandardCharsets.UTF_8),
                        secret.getKeys()));
    }

    private void onAnswersAvailable(@NonNull List<String> answers){
        if(mDecodedSecret == null || answers.isEmpty() || mDecodedSecret.getKeys().size() != answers.size())
            return;

        mSaveButtonEnabled = false;
        mSaveButtonVisible = false;
        invalidateOptionsMenu();

        List<Long> keys = new ArrayList<>(mDecodedSecret.getKeys());
        Secret secret = new Secret(mDecodedSecret.getName(),keys);

        byte[] data = mDecodedSecret.getSecret().getBytes(StandardCharsets.UTF_8);

        Disposable d = secret.encode(data,answers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    ViewModelProvider.Factory vmFactory
                            = new ViewModelProvider.AndroidViewModelFactory(getApplication());
                    ViewModelProvider vmProvider = new ViewModelProvider(this,vmFactory);

                    SecretsViewModel secretsVm = vmProvider.get(SecretsViewModel.class);
                    secretsVm.addSecret(secret);

                    startActivity(new Intent(this,MainActivity.class));
                },err -> {
                    Intent intent = new Intent(this,MainActivity.class);
                    intent.putExtra(MainActivity.EXTRA_SHOW_MESSAGE_DIALOG,R.string.encoding_failed);

                    startActivity(intent);
                });

        mDisposableManager.pushDisposable(d);

        Utils.addOrReplaceFragment(this,new ContentLoadingFragment(),R.id.fragment_container);
    }
}