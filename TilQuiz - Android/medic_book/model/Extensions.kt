package madortil.medicBook.models

import java.util.*

val hourPeriod: HourPeriod
    get() = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 7..14 -> HourPeriod.Morning
        in 15..20 -> HourPeriod.Noon
        else -> HourPeriod.Night
    }


enum class HourPeriod { Morning, Noon, Night }