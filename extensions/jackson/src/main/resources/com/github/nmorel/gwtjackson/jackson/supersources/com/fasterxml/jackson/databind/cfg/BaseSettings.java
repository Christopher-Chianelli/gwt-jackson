package com.fasterxml.jackson.databind.cfg;

import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.StdDateFormat;

/**
 * Immutable container class used to store simple configuration
 * settings. Since instances are fully immutable, instances can
 * be freely shared and used without synchronization.
 */
public final class BaseSettings
        implements java.io.Serializable {

    // for 2.6
    private static final long serialVersionUID = 1L;

    /**
     * We will use a default TimeZone as the baseline.
     */
    private static final TimeZone DEFAULT_TIMEZONE =
            //  TimeZone.getDefault()
            /* [databind#915] 05-Nov-2015, tatu: Changed to UTC, from earlier
             * baseline of GMT (up to 2.6)
             */
            TimeZone.getTimeZone("UTC");

    /*
    /**********************************************************
    /* Configuration settings; introspection, related
    /**********************************************************
     */

    /**
     * Custom property naming strategy in use, if any.
     */
    protected final PropertyNamingStrategy _propertyNamingStrategy;

    /**
     * Specific factory used for creating {@link JavaType} instances;
     * needed to allow modules to add more custom type handling
     * (mostly to support types of non-Java JVM languages)
     */
    protected final TypeFactory _typeFactory;

    /*
    /**********************************************************
    /* Configuration settings; type resolution
    /**********************************************************
     */

    /*
    /**********************************************************
    /* Configuration settings; other
    /**********************************************************
     */

    /**
     * Custom date format to use for de-serialization. If specified, will be
     * used instead of {@link com.fasterxml.jackson.databind.util.StdDateFormat}.
     *<p>
     * Note that the configured format object will be cloned once per
     * deserialization process (first time it is needed)
     */
    protected final DateFormat _dateFormat;
    /**
     * Default {@link java.util.Locale} used with serialization formats.
     * Default value is {@link Locale#getDefault()}.
     */
    protected final Locale _locale;

    /**
     * Default {@link java.util.TimeZone} used with serialization formats,
     * if (and only if!) explicitly set by use; otherwise `null` to indicate
     * "use default", which means "UTC" (from Jackson 2.7); earlier versions
     * (up to 2.6) used "GMT".
     *<p>
     * Note that if a new value is set, timezone is also assigned to
     * {@link #_dateFormat} of this object.
     */
    protected final TimeZone _timeZone;

    /**
     * Explicitly default {@link Base64Variant} to use for handling
     * binary data (<code>byte[]</code>), used with data formats
     * that use base64 encoding (like JSON, CSV).
     * 
     * @since 2.1
     */
    protected final Base64Variant _defaultBase64;

    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    public BaseSettings(
            PropertyNamingStrategy pns, TypeFactory tf,
            DateFormat dateFormat,
            Locale locale, TimeZone tz, Base64Variant defaultBase64) {
        _propertyNamingStrategy = pns;
        _typeFactory = tf;
        _dateFormat = dateFormat;
        _locale = locale;
        _timeZone = tz;
        _defaultBase64 = defaultBase64;
    }

    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */

    public BaseSettings withPropertyNamingStrategy(PropertyNamingStrategy pns) {
        if (_propertyNamingStrategy == pns) {
            return this;
        }
        return new BaseSettings(pns, _typeFactory,
                _dateFormat, _locale,
                _timeZone, _defaultBase64);
    }

    public BaseSettings withTypeFactory(TypeFactory tf) {
        if (_typeFactory == tf) {
            return this;
        }
        return new BaseSettings(_propertyNamingStrategy, tf,
                _dateFormat, _locale,
                _timeZone, _defaultBase64);
    }

    public BaseSettings withDateFormat(DateFormat df) {
        if (_dateFormat == df) {
            return this;
        }
        // 26-Sep-2015, tatu: Related to [databind#939], let's try to force TimeZone if
        //   (but only if!) it has been set explicitly.
        if ((df != null) && hasExplicitTimeZone()) {
            df = _force(df, _timeZone);
        }
        return new BaseSettings(_propertyNamingStrategy, _typeFactory,
                df, _locale,
                _timeZone, _defaultBase64);
    }

    public BaseSettings with(Locale l) {
        if (_locale == l) {
            return this;
        }
        return new BaseSettings(_propertyNamingStrategy, _typeFactory,
                _dateFormat, l,
                _timeZone, _defaultBase64);
    }

    /**
     * Fluent factory for constructing a new instance that uses specified TimeZone.
     * Note that timezone used with also be assigned to configured {@link DateFormat},
     * changing time formatting defaults.
     */
    public BaseSettings with(TimeZone tz) {
        if (tz == null) {
            throw new IllegalArgumentException();
        }
        if (tz == _timeZone) {
            return this;
        }

        DateFormat df = _force(_dateFormat, tz);
        return new BaseSettings(
                _propertyNamingStrategy, _typeFactory,
                df, _locale,
                tz, _defaultBase64);
    }

    /**
     * @since 2.1
     */
    public BaseSettings with(Base64Variant base64) {
        if (base64 == _defaultBase64) {
            return this;
        }
        return new BaseSettings(
                _propertyNamingStrategy, _typeFactory,
                _dateFormat, _locale,
                _timeZone, base64);
    }

    /*
    /**********************************************************
    /* API
    /**********************************************************
     */
    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return _propertyNamingStrategy;
    }

    public TypeFactory getTypeFactory() {
        return _typeFactory;
    }

    public DateFormat getDateFormat() {
        return _dateFormat;
    }

    public Locale getLocale() {
        return _locale;
    }

    public TimeZone getTimeZone() {
        TimeZone tz = _timeZone;
        return (tz == null) ? DEFAULT_TIMEZONE : tz;
    }

    /**
     * Accessor that may be called to determine whether this settings object
     * has been explicitly configured with a TimeZone (true), or is still
     * relying on the default settings (false).
     *
     * @since 2.7
     */
    public boolean hasExplicitTimeZone() {
        return (_timeZone != null);
    }

    public Base64Variant getBase64Variant() {
        return _defaultBase64;
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private DateFormat _force(DateFormat df, TimeZone tz) {
        if (df instanceof StdDateFormat) {
            return ((StdDateFormat) df).withTimeZone(tz);
        }
        // we don't know if original format might be shared; better create a clone:
        df = (DateFormat) df.clone();
        df.setTimeZone(tz);
        return df;
    }
}
