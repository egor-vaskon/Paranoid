package com.egorvaskon.paranoid.ui.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallbackImpl:
    ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START or ItemTouchHelper.END) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if(viewHolder is BaseRecyclerViewAdapterWithSelectableItems.BaseViewHolder){
            viewHolder.remove()
        } else
            throw IllegalArgumentException("Arbitrary view holder cannot be removed.")
    }
}