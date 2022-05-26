package ro.westaco.carhome.presentation.screens.settings

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import ro.westaco.carhome.R
import ro.westaco.carhome.presentation.base.BaseFragment


//C- Redesign
@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel>() {

    companion object {
        const val TAG = "SettingsFragment"
    }

    var dialogLogOut: BottomSheetDialog? = null
    override fun getContentView() = R.layout.fragment_settings
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun getStatusBarColor() =
        ContextCompat.getColor(requireContext(), R.color.white)

    override fun initUi() {
        // Header options
        profile.setOnClickListener {
            viewModel.onProfileClicked()
        }

        paymentMethods.setOnClickListener {
            viewModel.onPaymentMethodsClicked()
        }

        security.setOnClickListener {
            viewModel.onSecurityClicked()
        }

        history.setOnClickListener {
            viewModel.onHistoryClicked()
        }

        data.setOnClickListener {
            viewModel.onDataClicked()
        }

        notifications.setOnClickListener {
            viewModel.onNotificationsClicked()
        }

        documents.setOnClickListener {
            viewModel.onDocumentsClicked()
        }

        // List options
        aboutUs.setOnClickListener {
            viewModel.onAboutUsClicked()
        }

        faq.setOnClickListener {
            viewModel.onFaqClicked()
        }

        terms.setOnClickListener {
            viewModel.onTermsClicked()
        }

        gdpr.setOnClickListener {
            viewModel.onGdprClicked()
        }

        share.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        contactUs.setOnClickListener {
            viewModel.onContactUsClicked()
        }

        socials.setOnClickListener {
            viewModel.onSocialClicked()
        }

        changeLanguage.setOnClickListener {
            viewModel.onLanguageClicked()
        }

        logout.setOnClickListener {
            viewModel.onLogout()
            val view = layoutInflater.inflate(R.layout.logout_dialog, null)
            dialogLogOut = BottomSheetDialog(requireContext())
            dialogLogOut?.setCancelable(true)
            dialogLogOut?.setContentView(view)
            val cancel = view.findViewById<TextView>(R.id.cancel)
            val logout = view.findViewById<TextView>(R.id.logout)
            dialogLogOut?.show()

            cancel.setOnClickListener {
                dialogLogOut?.dismiss()
            }

            logout.setOnClickListener {
                viewModel.onLogoutClicked()
                dialogLogOut?.dismiss()
            }

        }
    }

    override fun setObservers() {
        viewModel.actionStream.observe(viewLifecycleOwner) {
            when (it) {
                is SettingsViewModel.ACTION.onLogout -> {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.gcp_web_client_id))
                        .requestEmail()
                        .build()

                    googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
                    googleSignInClient.signOut()
                    LoginManager.getInstance().logOut()

                    childFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
        }
    }
}