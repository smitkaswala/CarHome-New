package ro.westaco.carhome.presentation.screens.data.cars

import android.app.Dialog
import android.content.Intent
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_data_cars.*
import ro.westaco.carhome.R
import ro.westaco.carhome.data.sources.remote.responses.models.Vehicle
import ro.westaco.carhome.di.ApiModule
import ro.westaco.carhome.presentation.base.BaseFragment
import ro.westaco.carhome.presentation.screens.home.PdfActivity
import ro.westaco.carhome.presentation.screens.main.MainActivity
import ro.westaco.carhome.utils.DialogUtils.Companion.showErrorInfo
import ro.westaco.carhome.utils.Progressbar

//C- Design
@AndroidEntryPoint
class CarsFragment : BaseFragment<CarsViewModel>() {

    private lateinit var adapter: DataCarAdapter
    private var progressbar: Progressbar? = null

    override fun getContentView() = R.layout.fragment_data_cars

    override fun getStatusBarColor() = ContextCompat.getColor(requireContext(), R.color.white)


    override fun initUi() {
        progressbar = Progressbar(requireContext())
        progressbar?.showPopup()
        fab.setOnClickListener {
            viewModel.onAddNew()
        }

        cta.setOnClickListener {
            viewModel.onAddNew()
        }
    }

    override fun setObservers() {
        viewModel.carsLivedata.observe(viewLifecycleOwner) { cars ->
            if (cars.isNullOrEmpty()) {
                normalState.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
            } else {
                normalState.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                list.layoutManager = LinearLayoutManager(context)

                val listner = object : DataCarAdapter.OnItemInteractionListener {
                    override fun onItemClick(item: Vehicle) {
                        viewModel.onItemClick(item)
                    }

                    override fun onBuyClick(item: Vehicle, service: String) {
                        when (service) {
                            "RO_PASS_TAX" -> {
                                viewModel.onBuyPassTax(item)
                            }
                            "RO_VIGNETTE" -> {
                                viewModel.onBuyVignette(item)
                            }
                            "RO_RCA" -> {
                                showDialog()
                            }
                        }
                    }

                    override fun onDocClick(item: Vehicle, service: String) {
                        var url: String? = null
                        when (service) {
                            "RO_PASS_TAX" -> {
                                url = ApiModule.BASE_URL_RESOURCES + item.passTaxDocumentHref
                            }
                            "RO_VIGNETTE" -> {
                                url = ApiModule.BASE_URL_RESOURCES + item.vignetteTicketHref
                            }
                            "RO_RCA" -> {
                                showDialog()
                            }
                        }
                        if (url != null) {
                            val intent = Intent(requireContext(), PdfActivity::class.java)
                            intent.putExtra(PdfActivity.ARG_DATA, url)
                            intent.putExtra(PdfActivity.ARG_FROM, "DOCUMENT")
                            requireContext().startActivity(intent)
                        }
                    }

                }
                adapter = DataCarAdapter(
                    requireContext(),
                    arrayListOf(),
                    listener = listner
                )
                list.adapter = adapter
                adapter.setItems(cars)
            }
            progressbar?.dismissPopup()
        }

        viewModel.stateStream.observe(viewLifecycleOwner) { state ->
            when (state) {
                CarsViewModel.STATE.DOCUMENT_NOT_FOUND -> {

                    showErrorInfo(requireContext(), getString(R.string.doc_not_found))

                }
            }
        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.info_layout)
        val mOK = dialog.findViewById(R.id.mOK) as TextView
        val mText = dialog.findViewById(R.id.mText) as TextView
        mText.text = requireContext().resources.getString(
            R.string.insurance_info,
            MainActivity.activeUser
        )

        mOK.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


}