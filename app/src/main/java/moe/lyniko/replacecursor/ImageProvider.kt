package moe.lyniko.replacecursor

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import java.io.File

class ImageProvider: ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    // query: only get Uri last part as filename, send /data -> file
    @SuppressLint("SdCardPath")
    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor {
        Log.e("ReplaceCursor", "query: $p0")
        // fetch Uri last part
        val filename = p0.lastPathSegment
        // get file path
        val filePath = "/data/data/${BuildConfig.APPLICATION_ID}/files/$filename"
        // read content
        val fileContent = File(filePath).readBytes()
        // make cursor
        val cursor = MatrixCursor(arrayOf("data"))
        // add row
        cursor.addRow(arrayOf(fileContent))
        // return cursor
        return cursor
    }

    override fun getType(p0: Uri): String {
        return "vnd.android.cursor.item/single"
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }
}