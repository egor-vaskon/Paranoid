package com.egorvaskon.paranoid.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egorvaskon.paranoid.Constants;
import com.egorvaskon.paranoid.DecodedSecret;
import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.Secret;
import com.egorvaskon.paranoid.TextChangedListener;
import com.egorvaskon.paranoid.Utils;
import com.egorvaskon.paranoid.ui.adapter.BaseRecyclerViewAdapterWithSelectableItems;
import com.egorvaskon.paranoid.ui.adapter.KeysAdapter;
import com.egorvaskon.paranoid.ui.adapter.SecretsAdapter;
import com.egorvaskon.paranoid.ui.viewmodel.EditSecretViewModel;
import com.egorvaskon.paranoid.ui.viewmodel.KeysViewModel;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.rxjava3.disposables.Disposable;

public class EditSecretFragment extends Fragment {

    public static final String ARG_SECRET = "arg_secret";

    private KeysAdapter mKeysAdapter;
    private EditSecretViewModel mSharedViewModel;

    private DecodedSecret mDecodedSecret;

    private Disposable mSelectionDisposable;

    private TextInputEditText mNameView,mSecretView;

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

        if(getArguments() != null && getArguments().containsKey(ARG_SECRET))
            mDecodedSecret = getArguments().getParcelable(ARG_SECRET);
        else
            mDecodedSecret = new DecodedSecret();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_secret,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNameView = view.findViewById(R.id.new_secret_name);
        mSecretView = view.findViewById(R.id.new_secret_information);
        RecyclerView keys = view.findViewById(R.id.key_list);

        mNameView.addTextChangedListener(new TextChangedListener() {
            @Override
            public void onTextChanged(@NonNull CharSequence text) {
                if(mSharedViewModel != null){
                    mDecodedSecret.setName(text.toString());

                    mSharedViewModel.setDecodedSecret(mDecodedSecret);
                }
            }
        });

        mSecretView.addTextChangedListener(new TextChangedListener() {
            @Override
            public void onTextChanged(@NonNull CharSequence text) {
                if(mSharedViewModel != null){
                    mDecodedSecret.setSecret(text.toString());

                    mSharedViewModel.setDecodedSecret(mDecodedSecret);
                }
            }
        });

        mKeysAdapter = new KeysAdapter(view.getContext().getApplicationContext(),false,true);
        LinearLayoutManager lManager = new LinearLayoutManager(view.getContext());

        mSelectionDisposable = mKeysAdapter.getSelection()
                .subscribe(selection -> {
                    if(mSharedViewModel != null){
                        mDecodedSecret.setKeys(selection);

                        mSharedViewModel.setDecodedSecret(mDecodedSecret);
                    }
                });

        keys.setLayoutManager(lManager);
        keys.addItemDecoration(new DividerItemDecoration(view.getContext(),lManager.getOrientation()));
        keys.setAdapter(mKeysAdapter);

        if(mDecodedSecret.isValid(Constants.MIN_KEY_COUNT)){
            mNameView.setText(mDecodedSecret.getName());
            mSecretView.setText(mDecodedSecret.getSecret());
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
            mSharedViewModel.setDecodedSecret(mDecodedSecret);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mSelectionDisposable.dispose();
        mSharedViewModel = null;
        mKeysAdapter = null;
        mSecretView = null;
        mNameView = null;
    }
}
