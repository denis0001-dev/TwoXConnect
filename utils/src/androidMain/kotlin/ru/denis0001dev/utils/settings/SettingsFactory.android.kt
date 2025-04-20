package ru.denis0001dev.utils.settings

import ru.denis0001dev.utils.UtilsLibrary

actual val settings: Settings get() = AndroidSettings(UtilsLibrary.context)