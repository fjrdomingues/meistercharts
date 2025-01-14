package it.neckar.open.formatting

import it.neckar.open.i18n.I18nConfiguration
import it.neckar.open.i18n.convert
import it.neckar.open.kotlin.lang.SpecialChars
import it.neckar.open.kotlin.lang.WhitespaceConfig
import it.neckar.open.time.DateUtils
import it.neckar.open.time.toDoubleMillis
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalQueries

actual class DateTimeFormatIso8601 : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    return DateUtils.toZonedDateTime(timestamp.toLong(), i18nConfiguration.timeZone.toZoneId()).format(DateTimeFormatter.ISO_DATE_TIME)
  }
}

actual class DateFormatIso8601 : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    return DateUtils.toZonedDateTime(timestamp.toLong(), i18nConfiguration.timeZone.toZoneId()).format(DateTimeFormatter.ISO_LOCAL_DATE)
  }
}

actual class TimeFormatIso8601 : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    return DateUtils.toZonedDateTime(timestamp.toLong(), i18nConfiguration.timeZone.toZoneId()).format(DateTimeFormatter.ISO_LOCAL_TIME)
  }
}

actual class DateTimeFormatUTC : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    return DateUtils.toOffsetDateTime(timestamp.toLong(), i18nConfiguration.timeZone.toZoneId()).withOffsetSameInstant(ZoneOffset.UTC).format(it.neckar.open.time.utcDateTimeFormat)
  }

  actual companion object {
    /**
     * Parses the UTC string to a timestamp
     */
    actual fun parse(formattedUtc: String, i18n: I18nConfiguration): Double {
      it.neckar.open.time.utcDateTimeFormat.parse(formattedUtc).let {
        val date = it.query(TemporalQueries.localDate())
        val time = it.query(TemporalQueries.localTime())

        val instant = ZonedDateTime.of(date, time, ZoneOffset.UTC).toInstant()
        return instant.toDoubleMillis()
      }
    }
  }
}

actual class TimeFormat : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())
    return DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(i18nConfiguration.formatLocale.convert()).format(dateTime)
  }
}

actual class TimeFormatWithMillis : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())
    return DateUtils.createTimeMillisFormat(i18nConfiguration.formatLocale.convert()).format(dateTime)
  }
}

actual class DateFormat : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())
    return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(i18nConfiguration.formatLocale.convert()).format(dateTime)
  }
}

actual class DefaultDateTimeFormat : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())
    return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(i18nConfiguration.formatLocale.convert()).format(dateTime)
  }
}

actual class DateTimeFormatShort : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())
    return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(i18nConfiguration.formatLocale.convert()).format(dateTime)
  }
}

actual class DateTimeFormatWithMillis : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())
    return DateUtils.createDateTimeMillisFormat(i18nConfiguration.formatLocale.convert()).format(dateTime)
  }
}

actual class DateTimeFormatShortWithMillis : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())
    return DateUtils.createDateTimeShortMillisFormat(i18nConfiguration.formatLocale.convert()).format(dateTime)
  }
}

/**
 * A format that formats a date - but only prints the month and year
 */
actual class YearMonthFormat actual constructor() : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())
    return monthYearFormatSpace(whitespaceConfig).withLocale(i18nConfiguration.formatLocale.convert()).format(dateTime)
  }

  companion object {
    fun monthYearFormatSpace(whitespaceConfig: WhitespaceConfig): DateTimeFormatter {
      return when (whitespaceConfig) {
        WhitespaceConfig.NonBreaking, WhitespaceConfig.NonBreakingOnlyNbsp -> monthYearFormatNbsp
        WhitespaceConfig.Spaces -> monthYearFormatSpace
      }
    }

    val monthYearFormatSpace: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val monthYearFormatNbsp: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM${SpecialChars.nbsp}yyyy")
  }
}

actual class YearFormat actual constructor() : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())
    return yearFormat.withLocale(i18nConfiguration.formatLocale.convert()).format(dateTime)
  }

  companion object {
    val yearFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
  }
}

/**
 * Formats a time stamp as second with millis
 */
actual class SecondMillisFormat actual constructor() : DateTimeFormat {
  override fun format(timestamp: Double, i18nConfiguration: I18nConfiguration, whitespaceConfig: WhitespaceConfig): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), i18nConfiguration.timeZone.toZoneId())

    val value = dateTime.second + dateTime.nano / 1_000_000_000.0
    return decimalFormat(3).format(value, i18nConfiguration)
  }
}
