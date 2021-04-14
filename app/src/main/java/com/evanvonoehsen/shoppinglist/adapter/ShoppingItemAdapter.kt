package com.evanvonoehsen.shoppinglist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evanvonoehsen.shoppinglist.R
import com.evanvonoehsen.shoppinglist.data.AppDatabase
import com.evanvonoehsen.shoppinglist.data.ShoppingItem
import com.evanvonoehsen.shoppinglist.ScrollingActivity
import com.evanvonoehsen.shoppinglist.touch.TouchHelper
import kotlinx.android.synthetic.main.shopping_item_row.view.*
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.thread

class ShoppingItemAdapter(var context: Context) :
    ListAdapter<ShoppingItem, ShoppingItemAdapter.ViewHolder>(ShoppingItemDiffCallback()),
    TouchHelper {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shopping_item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val categoryNames = context.resources.getStringArray(R.array.category_names_array)

        holder.cbDone.text = item.name
        holder.cbDone.isChecked = item.found
        holder.tvPrice.text = formatCurrency(item.price)
        holder.imgCategory.setImageResource(item.getImageResource())
        holder.tvCategory.text = categoryNames[item.categoryId]

        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

        holder.btnEdit.setOnClickListener {
            costBeforeEdit = item.price
            Log.v("", "before edit price changed to" + item.price)
            (context as ScrollingActivity).showEditDialog(item)
        }

        holder.cbDone.setOnClickListener {
            item.found = holder.cbDone.isChecked
            thread {
                AppDatabase.getInstance(context).shoppingItemDao().updateShoppingItem(item)
            }
        }

        addToTotalCost(item.price)
    }

    public fun formatCurrency(input: Number): String {
        val format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance("USD"));

        return format.format(input);
    }

    private var deletedItem: ShoppingItem? = null
    private var deleteIndex = -1

    private var costBeforeEdit: Float = 0.0F

    fun subtractCostAfterEdit() {
        addToTotalCost(-costBeforeEdit)
    }

    fun deleteItem(position: Int) {
        deletedItem = getItem(position)
        deleteIndex = position

        addToTotalCost(-(deletedItem!!.price))

        thread {
            AppDatabase.getInstance(context).shoppingItemDao().deleteShoppingItem(deletedItem!!)
        }

        (context as ScrollingActivity).showDeleteMessage()
    }

    fun deleteAllItems() {
        thread {
            AppDatabase.getInstance(context).shoppingItemDao().deleteAllShoppingItems()
        }

        totalCost = 0.0
        (context as ScrollingActivity).updateCost(getTotalCost())
    }
    fun restoreItem() {
        thread {
            AppDatabase.getInstance(context).shoppingItemDao().addShoppingItem(deletedItem!!)
        }
    }

    private var totalCost = 0.0
    public fun addToTotalCost(cost: Float) {
        totalCost += cost
        (context as ScrollingActivity).updateCost(getTotalCost())
    }

    public fun getTotalCost():String {
        return formatCurrency(totalCost)
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPrice = itemView.tvPrice
        val tvCategory = itemView.tvCategory
        val imgCategory = itemView.imgCategory
        val cbDone = itemView.cbDone
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
    }

}

class ShoppingItemDiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
    override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem.shoppingItemId == newItem.shoppingItemId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem == newItem
    }
}