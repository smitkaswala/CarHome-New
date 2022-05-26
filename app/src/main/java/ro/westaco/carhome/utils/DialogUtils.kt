package ro.westaco.carhome.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ro.westaco.carhome.R
import ro.westaco.carhome.data.sources.remote.responses.models.WarningsItem
import ro.westaco.carhome.databinding.InfoLayoutBinding

internal class DialogUtils {

    companion object {

        fun showErrorInfo(
            context: Context?,
            massage: String) {
            if (context != null) {
                MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
                    .setTitle(context.getString(R.string.information_items_))
                    .setMessage(massage)
                    .setPositiveButton("Ok", null)
                    .show()
            }
        }
    }

}