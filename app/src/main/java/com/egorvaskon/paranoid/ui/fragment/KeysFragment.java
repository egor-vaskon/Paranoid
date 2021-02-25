package com.egorvaskon.paranoid.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egorvaskon.paranoid.DisposableManager;
import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.ui.activity.MainActivity;
import com.egorvaskon.paranoid.ui.adapter.BaseRecyclerViewAdapterWithSelectableItems;
import com.egorvaskon.paranoid.ui.adapter.ItemTouchHelperCallbackImpl;
import com.egorvaskon.paranoid.ui.adapter.KeysAdapter;
import com.egorvaskon.paranoid.ui.adapter.view_holder.SecretViewHolder;
import com.egorvaskon.paranoid.ui.viewmodel.KeysViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class KeysFragment extends Fragment {

    private static final String ARG_DO_ENABLE_DELETION = "do_enable_deletion";
    private static final String ARG_DO_ENABLE_SELECTION = "do_enable_selection";

    private KeysAdapter mKeysAdapter;
    private RecyclerView mRecyclerView;
    private boolean mDoEnableDeletion = false;
    private boolean mDoEnableSelection = false;

    private KeysViewModel mKeysViewModel;

    private final DisposableManager mDisposableManager = new DisposableManager();

    private Disposable mKeyDeleteDisposable;
    private Disposable mClickDisposable;

    private Handler mUiHandler;

    @NonNull
    public static KeysFragment newInstance(boolean doEnableDeletion,boolean doEnableSelection){
        Bundle args = new Bundle();
        args.putBoolean(ARG_DO_ENABLE_DELETION,doEnableDeletion);
        args.putBoolean(ARG_DO_ENABLE_SELECTION,doEnableSelection);

        KeysFragment keysFragment = new KeysFragment();
        keysFragment.setArguments(args);

        return keysFragment;
    }

    public KeysFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUiHandler = new Handler();

        if(getArguments() != null){
            mDoEnableDeletion = getArguments().getBoolean(ARG_DO_ENABLE_DELETION,false);
            mDoEnableSelection = getArguments().getBoolean(ARG_DO_ENABLE_SELECTION,false);
        }

        if(mDoEnableDeletion)
            setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_keys,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(view instanceof RecyclerView){
            mRecyclerView = (RecyclerView) view;
            mKeysAdapter = new KeysAdapter(view.getContext(), mDoEnableDeletion,mDoEnableSelection);
            LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());

            if(mDoEnableDeletion){
                ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelperCallbackImpl(mRecyclerView));

                touchHelper.attachToRecyclerView(mRecyclerView);
            }

            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),layoutManager.getOrientation()));
            mRecyclerView.setAdapter(mKeysAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() != null){
            ViewModelProvider vmProvider = new ViewModelProvider(this,
                    new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()));

            mKeysViewModel = vmProvider.get(KeysViewModel.class);
            mKeysViewModel.getKeysLiveData().observe(getViewLifecycleOwner(),keys -> {
                if(mKeysAdapter != null)
                    mKeysAdapter.update(keys);
            });

            mKeyDeleteDisposable
                    = mKeysAdapter.getEventStream().filter(e -> e.code == BaseRecyclerViewAdapterWithSelectableItems.Message.REMOVE_ITEM)
                    .subscribe(e -> mDisposableManager.pushDisposable(deleteKey(e.itemId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()));

            mClickDisposable
                    = mKeysAdapter.getEventStream().filter(e -> e.code == BaseRecyclerViewAdapterWithSelectableItems.Message.ITEM_CLICK)
                    .subscribe(e -> onItemClick(e.itemId));
        }
    }

    private void onItemClick(long id){
        mDisposableManager.pushDisposable(mKeysViewModel
                .getKey(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(key -> {
            if(getFragmentManager() != null){
                EditKeyDialogFragment dialog = EditKeyDialogFragment.newInstance(key.getId(),key.getName(),key.getQuestion());
                dialog.show(getFragmentManager(), MainActivity.EDIT_QUESTION_FRAGMENT_TAG);
            }
        }));
    }

    @NonNull
    private Completable deleteKey(long keyId){
        return mKeysViewModel.getKey(keyId)
                .flatMapCompletable(key -> Completable.fromRunnable(() -> {
                    if(key.getRefCount() == 0)
                        mKeysViewModel.deleteKey(keyId);
                    else{
                        mUiHandler.post(() -> Toast.makeText(requireContext(),R.string.cannot_delete_key,Toast.LENGTH_LONG).show());
                    }
                }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mKeysViewModel = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mDisposableManager.dispose();
        mRecyclerView = null;
        mKeysAdapter = null;
    }
}
