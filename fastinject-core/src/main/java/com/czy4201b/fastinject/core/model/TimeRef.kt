package com.czy4201b.fastinject.core.model

sealed class AsyncRef

class DelayRef(val ms: Int) : AsyncRef()

class TimeRef internal constructor(
    val varName: String,
    val selector: String,
    val ms: Int,
) : AsyncRef()