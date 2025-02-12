package com.essloyaltyprogram.dataClasses

data class Setting(
    val api: Api = Api(""),
    val banner: Banner = Banner("","","","",""),
    val contact: Contact = Contact("",""),
    val social: Social = Social()
)