package com.practica.buscov2.model.busco

data class Proposal(
    val id:Int? = null,
    val title:String? = null,
    val description :String? = null,
    val requirements :String? = null,
    val date:String? = null,
    val minBudget:Int? = null,
    val maxBudget:Int? = null,
    val image:String? = null,
    val status:Boolean? = null,
    val userId:Int? = null,
    var professionId:Int? = null,

    val user:User? = null,
    val profession: Profession? = null,
    val applications: List<Application>? = null,

    val latitude: Double? = null,
    val longitude: Double? = null
)
