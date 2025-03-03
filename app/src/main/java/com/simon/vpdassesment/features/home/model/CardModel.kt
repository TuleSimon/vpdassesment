package com.simon.vpdassesment.features.home.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Accounts")
data class CardModel(
    val userName:String,
    @PrimaryKey
    val accountNumber:Long,
    val balance:Long,
    val userId:String
)
