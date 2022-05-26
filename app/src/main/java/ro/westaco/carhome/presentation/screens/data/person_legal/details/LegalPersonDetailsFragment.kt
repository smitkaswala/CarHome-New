package ro.westaco.carhome.presentation.screens.data.person_legal.details

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_legal_person_details.*
import org.json.JSONObject
import ro.westaco.carhome.R
import ro.westaco.carhome.data.sources.local.prefs.AppPreferencesDelegates
import ro.westaco.carhome.data.sources.remote.responses.models.CatalogItem
import ro.westaco.carhome.data.sources.remote.responses.models.Country
import ro.westaco.carhome.data.sources.remote.responses.models.LegalPerson
import ro.westaco.carhome.data.sources.remote.responses.models.LegalPersonDetails
import ro.westaco.carhome.databinding.DialogDeleteLegalPersonBinding
import ro.westaco.carhome.di.ApiModule
import ro.westaco.carhome.presentation.base.BaseFragment
import ro.westaco.carhome.presentation.screens.data.commen.PhoneCodeModel
import ro.westaco.carhome.utils.CountryCityUtils
import ro.westaco.carhome.utils.DialogUtils.Companion.showErrorInfo
import ro.westaco.carhome.utils.FileUtil
import ro.westaco.carhome.utils.Progressbar
import ro.westaco.carhome.utils.ViewUtils
import java.io.File
import java.util.*

//C- Rebuilt
@AndroidEntryPoint
class LegalPersonDetailsFragment : BaseFragment<LegalPersonDetailsViewModel>() {
    private var legalPerson: LegalPerson? = null
    private var legalPersonDetails: LegalPersonDetails? = null
    var progressbar: Progressbar? = null
    var lottieAnimationView: LottieAnimationView? = null
    private var menuOpen = false
    lateinit var bottomSheet: BottomSheetDialog
    var countriesList: ArrayList<Country> = ArrayList()
    var streetTypeList: ArrayList<CatalogItem> = ArrayList()
    private var menuOpen3 = false

    companion object {
        const val ARG_LEGAL_PERSON = "arg_legal_person"
    }

    override fun getContentView() = R.layout.fragment_legal_person_details

    override fun getStatusBarColor() = ContextCompat.getColor(requireContext(), R.color.white)

    override fun onResume() {
        super.onResume()
        arguments?.let {
            legalPerson = it.getSerializable(ARG_LEGAL_PERSON) as? LegalPerson?
            legalPerson?.id?.toLong()?.let { it1 -> viewModel.getLegalPersonDetails(it1) }
        }
    }

    override fun initUi() {
        progressbar = Progressbar(requireContext())
        progressbar?.showPopup()
        bottomSheet = BottomSheetDialog(requireContext())
        lottieAnimationView = lottieAnimation
        lottieAnimationView?.visibility = View.VISIBLE

        back.setOnClickListener {
            viewModel.onBack()
        }

        home.setOnClickListener {
            viewModel.onMain()
        }

        avatar.setOnClickListener {
            val result = FileUtil.checkPermission(requireContext())
            if (result) {
                openGallery()
            } else {
                FileUtil.requestPermission(requireActivity())
            }
        }

        mMenu.setOnClickListener {
            menuVisible()
        }



        name_in_lay.setOnClickListener {

            if (cd_hidden_view.visibility == View.VISIBLE) {
                ViewUtils.collapse(cd_hidden_view)
                name_in_lay.setBackgroundResource(R.color.white)
                name_arrow.setImageResource(R.drawable.ic_arrow_circle_down)
            } else {
                ViewUtils.expand(cd_hidden_view)
                name_in_lay.setBackgroundResource(R.color.expande_colore)
                name_arrow.setImageResource(R.drawable.ic_arrow_circle_up)


            }
        }

        adds_fix_lay.setOnClickListener {
            if (menuOpen3) {
                menuOpen3 = !menuOpen3
                ViewUtils.expand(adds_hidden_view)
                adds_fix_lay.setBackgroundResource(R.color.expande_colore)
                adds_arrow.setImageResource(R.drawable.ic_arrow_circle_up)
            } else {
                menuOpen3 = !menuOpen3
                ViewUtils.collapse(adds_hidden_view)
                adds_fix_lay.setBackgroundResource(R.color.white)
                adds_arrow.setImageResource(R.drawable.ic_arrow_circle_down)
            }
        }

        personal_info_fixed_layout.setOnClickListener {

            if (p_hidden_view.visibility == View.VISIBLE) {
                ViewUtils.collapse(p_hidden_view)
                p_arrow.setImageResource(R.drawable.ic_arrow_circle_down)
                personal_info_lay.setBackgroundResource(R.color.white)
            } else {
                ViewUtils.expand(p_hidden_view)
                p_arrow.setImageResource(R.drawable.ic_arrow_circle_up)
                personal_info_lay.setBackgroundResource(R.color.expande_colore)
            }

        }

    }

    private fun menuVisible() {
        if (menuOpen) {
            menuOpen = !menuOpen
            ViewUtils.collapse(mLinear)
            mMenu.setImageResource(R.drawable.ic_more_new)
        } else {
            menuOpen = !menuOpen
            ViewUtils.expand(mLinear)
            mMenu.setImageResource(R.drawable.ic_more_new_done)
        }
    }

    override fun setObservers() {

        viewModel.legalPersDetailsLiveData?.observe(viewLifecycleOwner) { legalItem ->
            if (legalItem != null) {
                legalPersonDetails = legalItem

                if (legalPersonDetails?.logoHref?.isNotEmpty() == true) {
                    val url = "${ApiModule.BASE_URL_RESOURCES}${legalPersonDetails?.logoHref}"
                    val glideUrl = GlideUrl(
                        url,
                        LazyHeaders.Builder()
                            .addHeader(
                                "Authorization",
                                "Bearer ${AppPreferencesDelegates.get().token}"
                            )
                            .build()
                    )

                    val options = RequestOptions()
                    avatar.clipToOutline = true
                    Glide.with(requireContext())
                        .load(glideUrl)
                        .error(requireContext().resources.getDrawable(R.drawable.ic_user))
                        .apply(
                            options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.HIGH)
                                .format(DecodeFormat.PREFER_ARGB_8888)
                        )
                        .into(avatar)
                }
                lottieAnimationView?.visibility = View.GONE

                viewModel.fetchDefaultData()
                legalItem.let {
                    val address = it.address
                    companyTitle.text = it.companyName

                    noReg.setText(it.noRegistration)
                    emailId.setText(it.email)
                    cui.setText(it.cui)

                    caen.setText(it.caen?.code)
                    activityType.setText(it.activityType?.name)

                    street_type.setText(address?.streetType?.name)
                    streetName.setText(address?.streetName)
                    buildingNo.setText(address?.buildingNo)
                    blockName.setText(address?.block)
                    entrance.setText(address?.entrance)
                    apartment.setText(address?.apartment)
                    floor.setText(address?.floor)
                    zipCode.setText(address?.zipCode)

                    address?.countryCode?.let { changeCountryState(it) }
                    if (address?.countryCode == "ROU") {
                        countySpinnerText.setText(address.region)
                        localitySpinnerText.setText(address.locality)
                    } else {
                        stateProvinceText.setText(address?.region)
                        localityAreaText.setText(address?.locality)
                    }
                }

                cta.setOnClickListener {
                    viewModel.onEdit(legalItem)
                }


                delete.setOnClickListener {
                    menuVisible()

                    val bindingSheet = DataBindingUtil.inflate<DialogDeleteLegalPersonBinding>(
                        layoutInflater,
                        R.layout.dialog_delete_legal_person,
                        null,
                        false
                    )
                    bottomSheet.setContentView(bindingSheet.root)
                    bindingSheet.mDeleteText.text = requireContext().resources.getString(
                        R.string.delete_name,
                        legalPerson?.companyName
                    )

                    bindingSheet.cancel.setOnClickListener {
                        bottomSheet.dismiss()
                    }

                    bindingSheet.mDelete.setOnClickListener {
                        legalPerson?.id?.toLong()?.let { it1 -> viewModel.onDelete(it1) }
                        bottomSheet.dismiss()
                    }

                    bottomSheet.show()
                }
            }
            progressbar?.dismissPopup()
        }

        viewModel.countryData.observe(viewLifecycleOwner) { countryData ->
            if (countryData != null) {
                this.countriesList = countryData
                val address = legalPersonDetails?.address
                val pos = address?.countryCode?.let {
                    Country.findPositionForCode(
                        countriesList,
                        it
                    )
                }
                val countryItem = pos?.let { countriesList[it] }
                countryNameTV.text = countryItem?.name
                countryItem?.twoLetterCode?.lowercase(
                    Locale.getDefault()
                )?.let {
                    CountryCityUtils.getFlagId(
                        it
                    )
                }?.let {
                    cuntryFlagIV.text =
                        it

                }
                setPhoneCountryData()
            }
        }

        viewModel.streetTypeData.observe(viewLifecycleOwner) { streetTypeData ->
            if (streetTypeData != null) {
                streetTypeList = streetTypeData
            }
        }


    }

    private fun setPhoneCountryData() {
        val phoneModelList: ArrayList<PhoneCodeModel> = ArrayList()
        val obj = JSONObject(FileUtil.loadJSONFromAsset(requireContext()))
        var phoneCountryItem: Country? = null
        if (legalPersonDetails != null) {
            val pos = legalPersonDetails?.phoneCountryCode?.let { it1 ->
                Country.findPositionForCode(
                    countriesList,
                    it1
                )
            }
            if (pos != null)
                phoneCountryItem = countriesList[pos]
        }

        var romanCode: PhoneCodeModel? = null
        for (key in obj.keys()) {
            val keyStr = key as String
            val keyvalue = obj[keyStr]
            val code = PhoneCodeModel(keyStr, keyvalue as String?)
            phoneModelList.add(code)
            if (phoneCountryItem != null && phoneCountryItem.twoLetterCode == code.key) {
                romanCode = code
            } else {
                if (code.key == "RO" && romanCode == null) {
                    romanCode = code
                }
            }
        }

        if (legalPersonDetails?.phone.isNullOrEmpty()) {
            phoneItem.setText("")
        } else {
            phoneItem.setText("+${romanCode?.value}${legalPersonDetails?.phone}")
        }

    }

    private fun changeCountryState(code: String) {
        if (code == "ROU") {
            spinnerCounty.isVisible = true
            spinnerLocality.isVisible = true
            stateProvinceLabel.isVisible = false
            localityAreaLabel.isVisible = false
        } else {
            spinnerCounty.isVisible = false
            spinnerLocality.isVisible = false
            stateProvinceLabel.isVisible = true
            localityAreaLabel.isVisible = true
        }
    }

    @SuppressLint("InlinedApi")
    fun openGallery() {
        val imageUri: Uri? = null
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/png"
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, imageUri)
        startActivityForResult(intent, 111)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {

                val selectedUri: Uri? = data.data
                var selectedFile: String? = null
                if (selectedUri != null) {
                    selectedFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileUtil.getFilePathFor11(requireContext(), selectedUri)
                    } else {
                        FileUtil.getPath(selectedUri, requireContext())
                    }
                }
                if (selectedFile != null) {
                    legalPerson?.id.let {
                        if (it != null) {
                            viewModel.onAddLogo(it, File(selectedFile))
                        }
                    }
                }
            }
        }

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            openGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            200 -> if (grantResults.size > 0) {
                val READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                    // perform action when allow permission success
                } else {
                    showErrorInfo(requireContext(),getString(R.string.allow_permission))
                }
            }
        }
    }

}