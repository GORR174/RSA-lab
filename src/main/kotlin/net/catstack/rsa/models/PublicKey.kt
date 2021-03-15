package net.catstack.rsa.models

import net.catstack.rsa.utils.BigInt

data class PublicKey(
    val mod: BigInt,
    val e: BigInt
)
