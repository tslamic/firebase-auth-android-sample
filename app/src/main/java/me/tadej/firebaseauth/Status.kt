package me.tadej.firebaseauth

sealed class Status {
    object Completed : Status()
    object Pending : Status()
    data class Failed(val exception: Exception) : Status() {
        companion object {
            fun withMessage(message: String) = Failed(Exception(message))
        }
    }
}
