package com.evanvonoehsen.shoppinglist.touch

interface TouchHelper {
    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}