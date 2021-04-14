package com.evanvonoehsen.shoppinglist

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.evanvonoehsen.shoppinglist.data.ShoppingItem
import kotlinx.android.synthetic.main.item_dialog.*
import kotlinx.android.synthetic.main.item_dialog.view.*
import java.lang.RuntimeException
import java.util.*

class ShoppingItemDialog : DialogFragment(), AdapterView.OnItemSelectedListener {

    interface ShoppingItemHandler {
        fun shoppingItemCreated(item: ShoppingItem)
        fun shoppingItemUpdated(item: ShoppingItem)
    }

    lateinit var shoppingItemHandler: ShoppingItemHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ShoppingItemHandler) {
            shoppingItemHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.error_item_handler_not_implemented)
            )
        }
    }

    lateinit var etItemName: EditText
    lateinit var etItemPrice: EditText
    lateinit var cbItemDone: CheckBox
    lateinit var spinnerCategory: Spinner

    var selectedIndex = 10

    var inEditMode = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setTitle(getString(R.string.add_edit_title))

        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.item_dialog, null
        )

        etItemName = dialogView.etItemName
        cbItemDone = dialogView.cbItemDone
        spinnerCategory = dialogView.spinnerCategory
        etItemPrice = dialogView.etPrice

        val categoryAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.category_names_array,
            android.R.layout.simple_spinner_item
        )

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        dialogBuilder.setView(dialogView)

        inEditMode =
            (this.arguments != null && this.arguments!!.containsKey(ScrollingActivity.KEY_ITEM_EDIT))

        if (inEditMode) {
            val itemEdit =
                this.arguments!!.getSerializable(ScrollingActivity.KEY_ITEM_EDIT) as ShoppingItem
            etItemName.setText(itemEdit.name)
            etItemPrice.setText(itemEdit.price.toString())
            cbItemDone.isChecked = itemEdit.found
            spinnerCategory.setSelection(itemEdit.categoryId)

        }

        dialogBuilder.setPositiveButton(getString(R.string.ok_button)) { dialog, which ->
            //
        }
        dialogBuilder.setNegativeButton(getString(R.string.cancel_button)) { dialog, which ->
            //
        }
        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener {
            if (etItemName.text.isNotEmpty()) {
                if (etItemPrice.text.isNotEmpty()) {
                    if (inEditMode) {
                        handleItemEdit()
                    } else {
                        handleItemCreate()
                    }
                    dialog.dismiss()
                } else {
                    etItemPrice.error = getString(R.string.et_price_error)
                }

            } else {
                etItemName.error = getString(R.string.et_name_error)
            }


        }
    }

    private fun handleItemCreate() {
        shoppingItemHandler.shoppingItemCreated(
            ShoppingItem(
                null,
                Date(System.currentTimeMillis()).toString(),
                cbItemDone.isChecked,
                etItemName.text.toString(),
                etItemPrice.text.toString().toFloat(),
                spinnerCategory.selectedItemPosition
            )
        )
    }

    private fun handleItemEdit() {
        val itemToEdit = (arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM_EDIT
        ) as ShoppingItem).copy(
            name = etItemName.text.toString(),
            found = cbItemDone.isChecked,
            price = etItemPrice.text.toString().toFloat(),
            categoryId = spinnerCategory.selectedItemPosition
        )
        shoppingItemHandler.shoppingItemUpdated(itemToEdit)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        selectedIndex = 10
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedIndex = p2
    }
}