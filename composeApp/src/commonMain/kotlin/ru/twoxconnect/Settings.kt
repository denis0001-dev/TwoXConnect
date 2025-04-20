package ru.twoxconnect

import ru.denis0001dev.utils.settings.Settings
import ru.twoxconnect.ui.Theme

object Settings {
    val settings = Settings()

    var materialYou
        get() = settings.getBoolean("materialYou", true)
        set(value) = settings.putBoolean("materialYou", value)

    var theme: Theme
        get() = Theme.entries[settings.getInt("theme", Theme.AsSystem.ordinal)]
        set(value) = settings.putInt("theme", value.ordinal)
}