package com.egorvaskon.paranoid.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egorvaskon.paranoid.DecodedSecret;
import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.TextChangedListener;
import com.egorvaskon.paranoid.ui.adapter.KeysAdapter;
import com.egorvaskon.paranoid.ui.viewmodel.EditSecretViewModel;
import com.egorvaskon.paranoid.ui.viewmodel.KeysViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class EditSecretFragment extends Fragment {

    public static final String ARG_SECRET = "arg_secret";

    private static final int MIN_KEY_COUNT = 2;

    private boolean mCreate = true;

    private CharSequence mName;
    private CharSequence mSecret;

    private KeysAdapter mKeysAdapter;
    private EditSecretViewModel mSharedViewModel;

    private DecodedSecret mDecodedSecret;

    private Disposable mSelectionDisposable;

    @NonNull
    public static EditSecretFragment newInstance(@NonNull DecodedSecret secret){
        Bundle args = new Bundle();
        args.putParcelable(ARG_SECRET,secret);

        EditSecretFragment fragment = new EditSecretFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public EditSecretFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(getArguments() != null){
            if(getArguments().containsKey(ARG_SECRET)){
                mCreate = false;
                mDecodedSecret = getArguments().getParcelable(ARG_SECRET);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_secret,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText name = view.findViewById(R.id.new_secret_name);
        TextInputEditText secret = view.findViewById(R.id.new_secret_information);
        RecyclerView keys = view.findViewById(R.id.key_list);

        name.addTextChangedListener(new TextChangedListener() {
            @Override
            public void onTextChanged(@NonNull CharSequence text) {
                if(mSharedViewModel != null){
                    mName = text;

                    if(mSecret != null
                            && !mName.toString().isEmpty()
                            && !mSecret.toString().isEmpty()
                            && mKeysAdapter.getCurrentSelection().size() >= MIN_KEY_COUNT){
                        DecodedSecret decodedSecret = new DecodedSecret(mName.toString(),mSecret.toString(),mKeysAdapter.getCurrentSelection());
                        mSharedViewModel.setDecodedSecret(decodedSecret);
                    }
                    else
                        mSharedViewModel.setDecodedSecret(new DecodedSecret());
                }
            }
        });

        secret.addTextChangedListener(new TextChangedListener() {
            @Override
            public void onTextChanged(@NonNull CharSequence text) {
                if(mSharedViewModel != null){
                    mSecret = text;

                    if(mName != null
                            && !mName.toString().isEmpty()
                            && !mSecret.toString().isEmpty()
                            && mKeysAdapter.getCurrentSelection().size() >= MIN_KEY_COUNT){
                        DecodedSecret decodedSecret = new DecodedSecret(mName.toString(),mSecret.toString(),mKeysAdapter.getCurrentSelection());
                        mSharedViewModel.setDecodedSecret(decodedSecret);
                    }
                    else
                        mSharedViewModel.setDecodedSecret(new DecodedSecret());
                }
            }
        });

        mKeysAdapter = new KeysAdapter(view.getContext().getApplicationContext(),false,true);
        LinearLayoutManager lManager = new LinearLayoutManager(view.getContext());

        mSelectionDisposable = mKeysAdapter.getSelection()
                .subscribe(selection -> {
                    if(mSharedViewModel != null){
                        if(mName != null && mSecret != null
                                && !mName.toString().isEmpty()
                                && !mSecret.toString().isEmpty()
                                && selection.size() >= 2){
                            DecodedSecret decodedSecret = new DecodedSecret(mName.toString(),mSecret.toString(),selection);
                            mSharedViewModel.setDecodedSecret(decodedSecret);
                        }
                        else
                            mSharedViewModel.setDecodedSecret(new DecodedSecret());
                    }
                });

        keys.setLayoutManager(lManager);
        keys.addItemDecoration(new DividerItemDecoration(view.getContext(),lManager.getOrientation()));
        keys.setAdapter(mKeysAdapter);

        if(!mCreate){
            name.setText(mDecodedSecret.getName());
            secret.setText(mDecodedSecret.getSecret());
            mName = mDecodedSecret.getName();
            mSecret = mDecodedSecret.getSecret();
            mKeysAdapter.setSelection(mDecodedSecret.getKeys());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() != null){
            ViewModelProvider vmProvider = new ViewModelProvider(getActivity(),
                    new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()));

            KeysViewModel keysVm = vmProvider.get(KeysViewModel.class);
            keysVm.getKeysLiveData().observe(getViewLifecycleOwner(),keys -> mKeysAdapter.update(keys));

            mSharedViewModel = new ViewModelProvider(requireActivity()).get(EditSecretViewModel.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mSelectionDisposable.dispose();
        mSharedViewModel = null;
        mKeysAdapter = null;
    }
}
