package ru.twoxconnect

import android.app.Application
import android.content.Context
import ru.denis0001dev.utils.UtilsLibrary
import java.lang.ref.WeakReference

class TwoXConnectApp: Application() {
    companion object {
        private lateinit var _context: WeakReference<Context>
        val context get() = _context.get()!!
    }

    override fun onCreate() {
        super.onCreate()
        _context = WeakReference(this)

        UtilsLibrary.init(this)
    }
}