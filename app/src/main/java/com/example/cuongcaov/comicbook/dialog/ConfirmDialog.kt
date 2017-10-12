package com.example.cuongcaov.comicbook.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cuongcaov.comicbook.R
import kotlinx.android.synthetic.main.dialog_fragment.view.*
import java.io.Serializable

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 03/10/2017
 */
class ConfirmDialog : DialogFragment() {

    private var mTitle = ""
    private var mMessage = ""
    private var mListener: DialogOnclickListener? = null


    companion object {

        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_LISTENER = "listener"

        fun getNewInstance(title: String, message: String, listener: DialogOnclickListener): ConfirmDialog {
            val instance = ConfirmDialog()
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putString(KEY_MESSAGE, message)
            bundle.putSerializable(KEY_LISTENER, listener)
            instance.arguments = bundle
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(arguments) {
            mTitle = getString(KEY_TITLE)
            mMessage = getString(KEY_MESSAGE)
            mListener = getSerializable(KEY_LISTENER) as DialogOnclickListener
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fragment, container, false)
        view.tvDialogTitle.text = mTitle
        view.tvDialogMessage.text = mMessage
        view.btnOk.setOnClickListener {
            mListener?.onOkButtonClick()
            dismiss()
        }
        view.btnCancel.setOnClickListener {
            dismiss()
        }
        return view
    }

}

interface DialogOnclickListener : Serializable {
    fun onOkButtonClick()
}
