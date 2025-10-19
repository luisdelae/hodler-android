package com.luisd.hodler.data.mapper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.luisd.hodler.domain.model.Result
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .catch { emit(Result.Error(it)) }
}
