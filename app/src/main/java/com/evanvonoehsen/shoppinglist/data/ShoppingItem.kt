package com.evanvonoehsen.shoppinglist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.evanvonoehsen.shoppinglist.R
import java.io.Serializable

@Entity(tableName = "shopping_item")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) var shoppingItemId: Long?,
    @ColumnInfo(name = "createDate") var createDate: String,
    @ColumnInfo(name = "found") var found: Boolean,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "price") var price: Float,
    @ColumnInfo(name = "categoryId") var categoryId: Int
) : Serializable {

    companion object {
        const val BEVERAGES: Int = 0
        const val BREAD_BAKERY: Int = 1
        const val CANNED_GOODS: Int = 2
        const val DAIRY: Int = 3
        const val DRY_BAKING_GOODS: Int = 4
        const val FROZEN_FOODS: Int = 5
        const val MEAT: Int = 6
        const val PRODUCE: Int = 7
        const val CLEANERS: Int = 8
        const val PERSONAL_CARE: Int = 9
        const val OTHER: Int = 10
    }

    fun getImageResource(): Int {
        return when (categoryId) {
            BEVERAGES -> {
                R.drawable.beverages
            }
            BREAD_BAKERY -> {
                R.drawable.bread_bakery
            }
            CANNED_GOODS -> {
                R.drawable.canned_goods
            }
            DAIRY -> {
                R.drawable.dairy
            }
            DRY_BAKING_GOODS -> {
                R.drawable.dry_baking_goods
            }
            FROZEN_FOODS -> {
                R.drawable.frozen_foods
            }
            MEAT -> {
                R.drawable.meat
            }
            PRODUCE -> {
                R.drawable.produce
            }
            CLEANERS -> {
                R.drawable.cleaners
            }
            PERSONAL_CARE -> {
                R.drawable.personal_care
            }
            else -> {
                R.drawable.other
            }
        }
    }
}
