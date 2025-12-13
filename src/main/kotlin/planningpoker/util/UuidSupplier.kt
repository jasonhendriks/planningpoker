package ca.hendriks.planningpoker.util

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun uuidSupplier(): () -> String = @OptIn(ExperimentalUuidApi::class) {
    Uuid.random().toString()
}
