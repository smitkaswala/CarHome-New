package ro.westaco.carhome.presentation.screens.settings.extras

import ro.westaco.carhome.R
import ro.westaco.carhome.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_about_us.*

//C- About Us
@AndroidEntryPoint
class AboutUsFragment : BaseFragment<CommenViewModel>() {


    override fun getContentView() = R.layout.fragment_about_us

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