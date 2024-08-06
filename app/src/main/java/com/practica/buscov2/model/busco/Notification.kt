package com.practica.buscov2.model.busco

data class Notification(
    val id:Int? = null,
    val userReceiveId:Int,
    val userSenderId:Int? = null,
    val dateAndTime:String? = null,
    val text:String? = null,
    val proposalId:Int? = null,

    val userSender:User? = null,
    val proposal:Proposal? = null
)
