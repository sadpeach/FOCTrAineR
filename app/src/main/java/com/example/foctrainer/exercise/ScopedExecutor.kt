package com.example.foctrainer.exercise

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import androidx.annotation.NonNull;

class ScopedExecutor(@NonNull executor: Executor?)  : Executor {
    private val executor: Executor? = executor
    private val shutdown: AtomicBoolean = AtomicBoolean()


    override fun execute(@NonNull command: Runnable) {
        if (shutdown.get()) {
            return
        }
        executor!!.execute {

            if (shutdown.get()) {
                return@execute
            }
            command.run()
        }
    }

    fun shutdown() {
        shutdown.set(true)
    }
}