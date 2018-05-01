package app.heroeswear.com.heroesfb

import android.content.ContentValues.TAG
import android.util.Log

class Logger {

    companion object {
        private const val TAG = "Heroes"

        private fun generateLogText(text: String): String {
            var log = text
            val stackTrace = Thread.currentThread().stackTrace
            if (stackTrace != null && stackTrace.size > 4) {
                val element = stackTrace[4]
                val className = element.className
                val shortClassName = className.substring(className.lastIndexOf(".") + 1)
                log = "Tread: " + Thread.currentThread().id + " | " +
                        "Class Name: " + shortClassName + " | " +
                        "Method: " + element.methodName + " | " +
                        text
            }

            return log
        }

        fun d() {
            if (BuildConfig.DEBUG)
                Log.d(TAG, generateLogText(""))
        }

        fun d(text: String) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, generateLogText(text))
        }

        fun e(text: String) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, generateLogText(text))
        }
    }
}