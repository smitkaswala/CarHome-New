package ro.westaco.carhome.presentation.screens.settings.extras

import ro.westaco.carhome.R
import ro.westaco.carhome.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_contact_us.*

//C- Social screen
@AndroidEntryPoint
class SocialFragment : BaseFragment<CommenViewModel>() {


    override fun getContentView() = R.layout.fragment_social

    override fun initUi() {
        back.setOnClickListener {
            viewModel.onBack()
        }

        home.setOnClickListener {
            viewModel.onMain()
        }
    }

    override fun setObservers() {
    }
}