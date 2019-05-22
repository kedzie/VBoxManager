package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.KsoapProxy

/**
 * Implementation of the [IDirectory] object for directories on the guest.
 */
@KsoapProxy
interface IGuestDirectory : IDirectory, Parcelable
