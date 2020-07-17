package com.example.foodatdoor

/** class for profile information so that object of profile could me created **/
class ProfileModel(val id:String, val name: String,val phnNo:String, val email: String) {
    constructor():this("","","",""){}
}