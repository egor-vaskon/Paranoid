package com.egorvaskon.paranoid.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.TreeSet;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

public abstract class BaseRecyclerViewAdapterWithSelectableItems
            <A extends BaseRecyclerViewAdapterWithSelectableItems.BaseViewHolder>
        extends RecyclerView.Adapter<A> {

    private static final String TAG = "BaseRecyclerViewAdapter";

    private TreeSet<Long> mCurrentSelection;
    private AdapterContext mAdapterContext;

    private BehaviorSubject<TreeSet<Long>> mSelectionSubject = BehaviorSubject.createDefault(new TreeSet<>());

    private RecyclerView mRecyclerView;

    private Disposable mMessageDisposable;

    public BaseRecyclerViewAdapterWithSelectableItems(@NonNull Context context){
        setHasStableIds(true);

        mAdapterContext = new AdapterContext(context.getApplicationContext());
    }

    public Context getContext() {
        return mAdapterContext.context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        Log.d(TAG,"Attached to recycler view.");
        mCurrentSelection = new TreeSet<>();

        mMessageDisposable = mAdapterContext.messageStream
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msg -> {
            switch (msg.code){
                case Message.SELECT_ITEM:
                    select(msg.itemId);
                    break;
                case Message.DESELECT_ITEM:
                    deselect(msg.itemId);
            }
        });

        mRecyclerView = recyclerView;
    }

    public TreeSet<Long> getCurrentSelection(){
        return mCurrentSelection;
    }

    public void setSelection(@NonNull TreeSet<Long> selection){
        mCurrentSelection = selection;
        mSelectionSubject.onNext(selection);
        notifyDataSetChanged();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        Log.d(TAG,"Detached from recycler view.");

        mMessageDisposable.dispose();
        mMessageDisposable = null;

        mRecyclerView = null;
    }

    public Observable<TreeSet<Long>> getSelection(){
        return mSelectionSubject;
    }

    public Observable<Message> getEventStream(){
        return mAdapterContext.messageStream;
    }

    @Override
    public final void onBindViewHolder(@NonNull A holder, int position) {
        if(holder.isSelected() && !isSelected(getItemId(position)))
            holder.onDeselected();
        else if(!holder.isSelected() && isSelected(getItemId(position)))
            holder.onSelected();

        bind(holder,position,isSelected(getItemId(position)));
    }

    @Override
    public abstract int getItemCount();

    @Override
    public abstract long getItemId(int position);

    public abstract int getItemPosition(long id);

    public abstract void bind(@NonNull A holder,int position,boolean isSelected);

    public final RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @NonNull
    @Override
    public abstract A onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    public static final class Message {
        public final int code;
        public final long itemId;

        public static final int SELECT_ITEM = 1;
        public static final int DESELECT_ITEM = 2;
        public static final int REMOVE_ITEM = 3;
        public static final int ITEM_CLICK = 4;

        public Message(int code, long itemId) {
            this.code = code;
            this.itemId = itemId;
        }
    }

    protected AdapterContext getAdapterContext() {
        return mAdapterContext;
    }

    public static class AdapterContext {
        private final Context context;
        private final PublishSubject<Message> messageStream;

        public AdapterContext(Context context) {
            this.context = context;
            messageStream = PublishSubject.create();
        }
    }

    protected final void select(long id){
        mCurrentSelection.add(id);
        ((BaseViewHolder)getRecyclerView().findViewHolderForItemId(id)).onSelected();
        mSelectionSubject.onNext(mCurrentSelection);
    }

    protected final void deselect(long id){
        mCurrentSelection.remove(id);
        ((BaseViewHolder)getRecyclerView().findViewHolderForItemId(id)).onDeselected();
        mSelectionSubject.onNext(mCurrentSelection);
    }

    protected boolean isSelected(long id){
        return mCurrentSelection.contains(id);
    }

    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder{

        private boolean mSelected;
        private AdapterContext mAdapterContext;

        public BaseViewHolder(@NonNull View itemView,@NonNull AdapterContext adapterContext) {
            super(itemView);

            mAdapterContext = adapterContext;
            mSelected = false;
        }

        @CallSuper
        protected void onSelected(){
            mSelected = true;
        }

        @CallSuper
        protected void onDeselected(){
            mSelected = false;
        }

        public final void select(){
            if(getItemId() == RecyclerView.NO_ID)
                return;

            mAdapterContext.messageStream.onNext(new Message(Message.SELECT_ITEM,getItemId()));
        }

        public final void deselect() {
            if(getItemId() == RecyclerView.NO_ID)
                return;

            mAdapterContext.messageStream.onNext(new Message(Message.DESELECT_ITEM,getItemId()));
        }

        public final void remove(){
            if(getItemId() == RecyclerView.NO_ID)
                return;

            mAdapterContext.messageStream.onNext(new Message(Message.REMOVE_ITEM,getItemId()));
        }

        public final void sendMessage(int code){
            mAdapterContext.messageStream.onNext(new Message(code,getItemId()));
        }

        public final boolean isSelected(){
            return mSelected;
        }
    }

}
