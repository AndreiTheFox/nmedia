package ru.netology.nmedia.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth

class LogoutDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.logoutConfirm)
            .setPositiveButton(R.string.logout) { dialog, _ ->
                AppAuth.getINstance().clear()
                dialog.cancel()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .create()

    companion object {
        const val TAG = "LogoutConfirmationDialog"
    }
}

//        return activity?.let {
//            // Use the Builder class for convenient dialog construction
//            val builder = AlertDialog.Builder(it)
//            builder.setMessage(R.string.logoutConfirm)
//                .setPositiveButton(R.string.logout, DialogInterface.OnClickListener{
//                        dialog, id ->
//                    AppAuth.getINstance().clear()
//                    dialog.cancel()
//                })
//                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {
//                        dialog, id ->
//                    dialog.cancel()
//                    //User cancelled
//                })
//            builder.setCancelable(true)
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null")
