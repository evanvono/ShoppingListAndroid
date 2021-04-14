package com.evanvonoehsen.shoppinglist.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_item")
    fun getAllShoppingItems() : LiveData<List<ShoppingItem>>

    @Query("DELETE FROM shopping_item")
    fun deleteAllShoppingItems()

    @Insert
    fun addShoppingItem(shoppingItem: ShoppingItem) : Long

    @Delete
    fun deleteShoppingItem(shoppingItem: ShoppingItem)

    @Update
    fun updateShoppingItem(shoppingItem: ShoppingItem)
}