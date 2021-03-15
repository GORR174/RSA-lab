package net.catstack.rsa

import net.catstack.rsa.application.ApplicationMain
import java.io.File

fun main(args: Array<String>) {
    val inFolder = File("in")
    inFolder.mkdir()
    val outFolder = File("out")
    outFolder.mkdir()

    ApplicationMain().run()
}