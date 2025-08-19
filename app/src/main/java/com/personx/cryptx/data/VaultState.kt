package com.personx.cryptx.data

import org.json.JSONArray
import org.json.JSONObject

data class VaultMetadata(
    val fileName: String,
    val folderName: String,
    val mimeType: String,
    val createdAt: Long,
    val modifiedAt: Long,
    val size: Long,
    val tags: List<String> = emptyList(),
    val thumbnail: String? = null
) {
    fun toJson(): String {
        val json = JSONObject()
            .put("fileName", fileName)
            .put("folderName", folderName)
            .put("mimeType", mimeType)
            .put("createdAt", createdAt)
            .put("modifiedAt", modifiedAt)
            .put("size", size)
            .put("tags", JSONArray(tags))
        if (thumbnail != null) {
            json.put("thumbnail", thumbnail)
        }
        return json.toString()
    }

    companion object {
        fun fromJson(json: String): VaultMetadata {
            val obj = JSONObject(json)

            // Parse tags list safely
            val tagsList = mutableListOf<String>()
            val tagsArray = obj.optJSONArray("tags")
            if (tagsArray != null) {
                for (i in 0 until tagsArray.length()) {
                    tagsList.add(tagsArray.getString(i))
                }
            }

            return VaultMetadata(
                fileName = obj.getString("fileName"),
                folderName = obj.getString("folderName"),
                mimeType = obj.getString("mimeType"),
                createdAt = obj.getLong("createdAt"),
                modifiedAt = obj.getLong("modifiedAt"),
                size = obj.optLong("size", -1L),
                tags = tagsList,
                thumbnail = obj.optString("thumbnail", null)
            )
        }
    }
}


