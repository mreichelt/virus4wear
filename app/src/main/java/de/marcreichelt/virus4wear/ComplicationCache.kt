package de.marcreichelt.virus4wear

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ComplicationDataCacheSerializer : Serializer<ComplicationDataCache> {
    override val defaultValue: ComplicationDataCache = ComplicationDataCache.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ComplicationDataCache {
        try {
            return ComplicationDataCache.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ComplicationDataCache, output: OutputStream) = t.writeTo(output)
}

suspend fun DataStore<ComplicationDataCache>.write(data: VaccinationData) {
    updateData { oldData ->
        oldData.toBuilder()
            .setFirstVaccination(data.firstVaccination.value)
            .setFullVaccination(data.fullVaccination.value)
            .build()
    }
}

suspend fun DataStore<ComplicationDataCache>.addComplicationId(complicationId: Int): Set<Int> {
    lateinit var updatedComplicationIds: Set<Int>
    updateData { oldData ->
        updatedComplicationIds = oldData.activeComplicationIdsList.toSet() + complicationId
        oldData.toBuilder()
            .clearActiveComplicationIds()
            .addAllActiveComplicationIds(updatedComplicationIds)
            .build()
    }
    return updatedComplicationIds
}

suspend fun DataStore<ComplicationDataCache>.removeComplicationId(complicationId: Int): Set<Int> {
    lateinit var updatedComplicationIds: Set<Int>
    updateData { oldData ->
        updatedComplicationIds = oldData.activeComplicationIdsList.toSet() - complicationId
        oldData.toBuilder()
            .clearActiveComplicationIds()
            .addAllActiveComplicationIds(updatedComplicationIds)
            .build()
    }
    return updatedComplicationIds
}

val Context.complicationDataCache: DataStore<ComplicationDataCache> by dataStore(
    fileName = "complication_data_cache.pb",
    serializer = ComplicationDataCacheSerializer
)
