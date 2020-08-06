package com.example.foodatdoor

data class OrderHistoryModel (
    var orderId:String,
    var restaurantName:String,
    var totalCost:String,
    var orderPlacedAt:String,
    var orderList:ArrayList<CartItems>
){
    constructor():this("","","","", arrayListOf())
}