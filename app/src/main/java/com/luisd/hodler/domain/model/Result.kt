package com.luisd.hodler.domain.model

/**
 * Represents the result of a data operation with cache awareness.
 *
 * @param T The type of data being returned
 */
sealed interface Result<out T> {
    /**
     * Loading state - operation in progress
     */
    object Loading : Result<Nothing>

    /**
     * Success state - operation completed successfully
     *
     * @property data The successful result data
     * @property isFromCache Whether this data came from cache (true) or fresh API call (false)
     * @property lastUpdated Timestamp in milliseconds when data was last updated, null if unknown
     */
    data class Success<T>(
        val data: T,
        val isFromCache: Boolean = false,
        val lastUpdated: Long? = null
    ) : Result<T>

    /**
     * Error state - operation failed
     *
     * @property Throwable The throwable that caused the failure
     */
    data class Error(
        val exception: Throwable
    ) : Result<Nothing>
}
