package com.kedzie.vbox.machine

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.android.synthetic.main.screenshot_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Take a screenshot and save to filesystem
 *
 * @apiviz.stereotype fragment
 */
class ScreenshotDialogFragment : DialogFragment() {

    private val model: MachineListViewModel by sharedViewModel { activity!!.intent.let {
        parametersOf(it.getParcelableExtra(VBoxSvc.BUNDLE), it.getParcelableExtra(IMachine.BUNDLE)) } }

    private lateinit var bitmap: Bitmap
    private val filename = "screenshot_" + DateFormat.format("yyyyMMdd_hmmssaa", Date()) + ".jpg"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Screenshot")
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bytes = arguments!!.getByteArray(BUNDLE_BYTES)
        bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(bytes))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screenshot_dialog, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView.setImageBitmap(bitmap)
        button_save.setOnClickListener {
            model.viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    try {
                        val file = File(Environment.getExternalStorageDirectory().toString(), filename)
                        val fOut = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
                        fOut.flush()
                        fOut.close()
                        MediaStore.Images.Media.insertImage(activity!!.contentResolver, file.absolutePath, file.name, file.name)
                    } catch (e: Exception) {
                        Timber.e(e, "Exception storing in MediaStore")
                    }
                }
                dismiss()
            }
        }
        button_cancel.setOnClickListener { dismiss() }
    }

    companion object {
        private const val BUNDLE_BYTES = "bytes"

        fun getInstance(bytes: ByteArray) = ScreenshotDialogFragment().apply {
            arguments = BundleBuilder().putByteArray(BUNDLE_BYTES, bytes).create()
        }
    }
}
