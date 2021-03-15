package net.catstack.rsa.models

import net.catstack.rsa.utils.BigInt

data class PrivateKey(
    val mod: BigInt,
    val d: BigInt
)
