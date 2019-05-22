package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.KsoapProxy

/**
 * Implementation of the [IFile] object for directories on the guest.
 */
@KsoapProxy
interface IGuestFile : IFile, Parcelable
