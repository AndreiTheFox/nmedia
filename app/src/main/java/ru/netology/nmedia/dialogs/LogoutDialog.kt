package ru.netology.nmedia.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

class LogoutDialog @Inject constructor (
    private val appAuth: AppAuth
): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.logoutConfirm)
            .setPositiveButton(R.string.logout) { dialog, _ ->
                appAuth.removeAuth()
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