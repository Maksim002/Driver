package ru.telecor.gm.mobile.droid.presentation.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import moxy.MvpPresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.model.data.server.Result
import ru.telecor.gm.mobile.droid.model.data.server.ServerError
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.base
 *
 * Base presenter for the project.
 *
 * Created by Artem Skopincev (aka sharpyx) 03.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
open class BasePresenter<V : BaseView> : MvpPresenter<V>(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    /**
     * Handle result of server response or another source.
     * @param result Result from server or another source
     * @param success Execute if Result is Result.Success
     * @param error Execute if Result is Result.Error
     */
    protected fun <T : Any> handleResult(result: Result<T>, success: (success: Result.Success<T>) -> Unit,
                               error: (error: Result.Error) -> Unit) {
        if (result is Result.Success) {
            success(result)
        } else if (result is Result.Error) {
            error(result)
        }
    }

    protected fun handleError(error: Result.Error, rm: IResourceManager) {
        when(error.exception) {
            is ConnectException -> viewState.showMessage(rm.getString(R.string.error_connection_failed))
            is SocketTimeoutException -> viewState.showMessage(rm.getString(R.string.error_connection_timeout))
            is ServerError -> viewState.showMessage(error.exception.errorResponse?.message ?: rm.getString(R.string.error_base))
            else -> viewState.showMessage(error.exception.message
                ?: rm.getString(R.string.error_base))
        }
    }

    protected fun handleError2(error: Result.Error, rm: IResourceManager,
                               block: ((message: String) -> Unit)? = null) {
        val msg = when(error.exception) {
            is ConnectException -> rm.getString(R.string.error_connection_failed)
            is SocketTimeoutException -> rm.getString(R.string.error_connection_timeout)
            is ServerError -> when(error.exception.errorCode){
                401 -> rm.getString(R.string.error_authorization)
                else -> error.exception.errorResponse?.message ?: rm.getString(R.string.error_base)
            }
            else -> error.exception.message ?: rm.getString(R.string.error_base)
        }

        block?.invoke(msg)
    }
}