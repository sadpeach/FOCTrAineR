package com.example.foctrainer.exercise

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import androidx.annotation.NonNull;

class ScopedExecutor(@NonNull executor: Executor?)  : Executor {
    private val executor: Executor? = executor
    private val shutdown: AtomicBoolean = AtomicBoolean()


    override fun execute(@NonNull command: Runnable) {
        // Return early if this object has been shut down.
        if (shutdown.get()) {
            return
        }
        executor!!.execute {

            // Check again in case it has been shut down in the mean time.
            if (shutdown.get()) {
                return@execute
            }
            command.run()
        }
    }

    /**
     * After this method is called, no runnables that have been submitted or are subsequently
     * submitted will start to execute, turning this executor into a no-op.
     *
     *
     * Runnables that have already started to execute will continue.
     */
    fun shutdown() {
        shutdown.set(true)
    }
}