package com.evanvonoehsen.shoppinglist

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.evanvonoehsen.shoppinglist.adapter.ShoppingItemAdapter
import com.evanvonoehsen.shoppinglist.data.AppDatabase
import com.evanvonoehsen.shoppinglist.data.ShoppingItem
import com.evanvonoehsen.shoppinglist.touch.TouchCallback
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scrolling.*

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import kotlin.concurrent.thread

class ScrollingActivity : AppCompatActivity(), ShoppingItemDialog.ShoppingItemHandler {
    companion object {
        const val KEY_ITEM_EDIT = "KEY_ITEM_EDIT"
        const val KEY_FIRST = "KEY_FIRST"
    }

    lateinit var itemAdapter: ShoppingItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title

        initRecyclerView()

        fab.setOnClickListener {
            ShoppingItemDialog()
                .show(supportFragmentManager, "TAG_ITEM_DIALOG")

        }

        fabDelete.setOnClickListener {
            itemAdapter.deleteAllItems()
        }

        if (isFirstRun()) {
            MaterialTapTargetPrompt.Builder(this@ScrollingActivity)
                .setTarget(findViewById<View>(R.id.fab))
                .setPrimaryText("New Shopping List Item")
                .setSecondaryText("Tap here to add an item to your shopping list")
                .show()
        }
        saveThatItWasStarted()
    }

    private fun isFirstRun(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
            KEY_FIRST, true
        )
    }

    private fun saveThatItWasStarted() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit()
            .putBoolean(KEY_FIRST, false)
            .apply()
    }

    private fun initRecyclerView() {
        itemAdapter = ShoppingItemAdapter(this)
        recyclerShopping.adapter = itemAdapter

        AppDatabase.getInstance(this).shoppingItemDao().getAllShoppingItems()
            .observe(this, Observer { items ->
                itemAdapter.submitList(items)
            })

        val touchCallbackList = TouchCallback(itemAdapter)
        val itemTouchHelper = ItemTouchHelper(touchCallbackList)
        itemTouchHelper.attachToRecyclerView(recyclerShopping)
    }

    override fun shoppingItemCreated(item: ShoppingItem) {
        thread {
            AppDatabase.getInstance(this).shoppingItemDao().addShoppingItem(item)
        }
    }

    override fun shoppingItemUpdated(item: ShoppingItem) {
        thread {
            AppDatabase.getInstance(this).shoppingItemDao().updateShoppingItem(item)
        }
        itemAdapter.subtractCostAfterEdit()
    }


    public fun showDeleteMessage() {
        Snackbar.make(recyclerShopping, "Item removed", Snackbar.LENGTH_LONG)
            .setAction("Undo", object : View.OnClickListener {
                override fun onClick(v: View?) {
                    itemAdapter.restoreItem()
                }
            })
            .show()
    }

    public fun showDeleteAllMessage() {
        Snackbar.make(recyclerShopping, "All items removed", Snackbar.LENGTH_LONG)
            .show()
    }

    public fun updateCost(cost: String) {
        tvCostSum.text = cost
    }

    public fun showEditDialog(itemToEdit: ShoppingItem) {
        val editDialog = ShoppingItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_EDIT, itemToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, "TAG_ITEM_EDIT")
    }

}