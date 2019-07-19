package com.kedzie.vbox.server

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "servers")
@Parcelize
data class Server(@PrimaryKey var id: Long? = null,
                  var name: String? = null,
                  var port: Int = 18083,
                  var host: String = "",
                  var username: String = "",
                  var password: String = "",
                  var isSSL: Boolean = false) : Parcelable {

    val uriString: String
        get() =  "${if(isSSL) "https" else "http"}://$host:$port"

    val url: Uri
        get() = Uri.parse(uriString)
}
