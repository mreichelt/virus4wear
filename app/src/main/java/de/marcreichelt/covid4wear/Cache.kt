package de.marcreichelt.covid4wear

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object VaccinationDataCacheSerializer : Serializer<VaccinationDataCache> {
    override val defaultValue: VaccinationDataCache = VaccinationDataCache.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): VaccinationDataCache {
        try {
            return VaccinationDataCache.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: VaccinationDataCache, output: OutputStream) = t.writeTo(output)
}

suspend fun DataStore<VaccinationDataCache>.write(data: VaccinationData) {
    updateData { oldData ->
        oldData.toBuilder()
            .setFirstVaccination(data.firstVaccination.value)
            .setFullVaccination(data.fullVaccination.value)
            .build()
    }
}

val Context.vaccinationDataCacheStore: DataStore<VaccinationDataCache> by dataStore(
    fileName = "vaccination_data_cache.pb",
    serializer = VaccinationDataCacheSerializer
)
