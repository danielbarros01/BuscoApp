package com.practica.buscov2.model.busco

data class Proposal(
    val id:Int? = null,
    val title:String? = null,
    val description :String? = null,
    val requirements :String? = null,
    val date:String? = null,
    val minBudget:Double? = null,
    val maxBudget:Double? = null,
    val image:String? = null,
    val status:Boolean? = null,
    val userId:Int? = null,
    val professionId:Int? = null,

    val user:User? = null,
    val profession: Profession? = null,
    val applications: List<Application>? = null
)
