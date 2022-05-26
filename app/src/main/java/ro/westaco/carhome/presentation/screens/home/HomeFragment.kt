package ro.westaco.carhome.presentation.screens.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.ResponseBody
import ro.westaco.carhome.R
import ro.westaco.carhome.data.sources.local.prefs.AppPreferencesDelegates
import ro.westaco.carhome.data.sources.remote.responses.models.*
import ro.westaco.carhome.presentation.base.BaseFragment
import ro.westaco.carhome.presentation.screens.dashboard.DashboardFragment
import ro.westaco.carhome.presentation.screens.home.adapter.CarDetailAdapter
import ro.westaco.carhome.presentation.screens.home.adapter.CarProgressAdapter
import ro.westaco.carhome.presentation.screens.home.adapter.RecentDocumentAdapter
import ro.westaco.carhome.presentation.screens.home.adapter.ReminderAdapter
import ro.westaco.carhome.presentation.screens.main.MainActivity
import ro.westaco.carhome.presentation.screens.main.MainActivity.Companion.activeUser
import ro.westaco.carhome.presentation.screens.main.MainActivity.Companion.profileItem
import ro.westaco.carhome.presentation.screens.settings.history.HistoryAdapter
import ro.westaco.carhome.utils.DialogUtils.Companion.showErrorInfo
import ro.westaco.carhome.utils.FirebaseAnalyticsList
import ro.westaco.carhome.utils.Progressbar
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat


//C- Home tab section
@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel>(),
    CarProgressAdapter.OnCarItemInteractionListener,
    CarDetailAdapter.OnCarDetailListener,
    RecentDocumentAdapter.OnItemInteractionListener,
    HistoryAdapter.OnItemInteractionListener,
    ReminderAdapter.OnItemInteractionListener {

    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var carAdapter: CarProgressAdapter
    private lateinit var carDetailAdapter: CarDetailAdapter
    private lateinit var recentDocAdapter: RecentDocumentAdapter
    private lateinit var reminderAdapter: ReminderAdapter

    lateinit var adapter: HistoryAdapter
    var allFilterList: ArrayList<CatalogItem> = ArrayList()
    var vehicleProgressList: ArrayList<VehicleProgressItem> = ArrayList()
    var progressbar: Progressbar? = null

    override fun getContentView() = R.layout.fragment_home

    override fun getStatusBarColor() = ContextCompat.getColor(requireContext(), R.color.white)

    @SuppressLint("ClickableViewAccessibility")
    override fun initUi() {

        progressbar = Progressbar(requireContext())

        val parentFrag: DashboardFragment = this@HomeFragment.parentFragment as DashboardFragment
        val menu: Menu = parentFrag.bottomNavigationView.menu


        avatar.setOnClickListener {

            viewModel.onAvatarClicked()
        }

        mProfileRL.setOnClickListener {

            viewModel.onAvatarClicked()
        }

        addCar.setOnClickListener {
            viewModel.onAddNewCar()
        }

        noCarRL.setOnClickListener {
            viewModel.onAddNewCar()
        }

        offerOpinion.clipToOutline = true
        offerMap.clipToOutline = true
        offerReminder.clipToOutline = true

        offerOpinion.setOnClickListener {
            viewModel.onContactUsClicked()
        }

        offerMap.setOnClickListener {
            val params = Bundle()
            viewModel.mFirebaseAnalytics.logEvent(FirebaseAnalyticsList.ACCESS_OFFER_HOME, params)
            parentFrag.bottomNavigationView.menu.findItem(R.id.maps).isChecked = true
            parentFrag.viewModel.onItemSelected(menu.findItem(R.id.maps))
        }

        offerReminder.setOnClickListener {
            val params = Bundle()
            viewModel.mFirebaseAnalytics.logEvent(FirebaseAnalyticsList.ACCESS_OFFER_HOME, params)
            parentFrag.bottomNavigationView.menu.findItem(R.id.reminder).isChecked = true
            parentFrag.viewModel.onItemSelected(menu.findItem(R.id.reminder))
        }

        insurance.setOnClickListener {

            viewModel.onInsurance()
        }

        rovinieta.setOnClickListener {

            viewModel.onServiceClicked("RO_VIGNETTE")
        }

        bridge_tax.setOnClickListener {

            viewModel.onServiceClicked("RO_PASS_TAX")

        }


        cars.setOnClickListener {
            val params = Bundle()
            viewModel.mFirebaseAnalytics.logEvent(FirebaseAnalyticsList.ACCESS_CAR_HOME, params)
            viewModel.onDataClicked(0)
        }

        person.setOnClickListener {
            val params = Bundle()
            viewModel.mFirebaseAnalytics.logEvent(FirebaseAnalyticsList.ACCESS_PERSON_HOME, params)
            viewModel.onDataClicked(1)
        }

        companies.setOnClickListener {
            viewModel.onDataClicked(2)
        }

        viewCar.setOnClickListener {
            viewModel.onDataClicked(0)
        }

        documentLL.setOnClickListener {
            viewModel.onNewDocument()
        }

        reminderLL.setOnClickListener {
            viewModel.onNewReminder()
        }

        viewDocument.setOnClickListener {
            viewModel.onNewDocument()
        }

        viewReminder.setOnClickListener {
            parentFrag.bottomNavigationView.menu.findItem(R.id.reminder).isChecked = true
            parentFrag.viewModel.onItemSelected(menu.findItem(R.id.reminder))
        }

        viewHistory.setOnClickListener {
            viewModel.onHistoryClicked()
        }

        mServices.setOnClickListener {
            parentFrag.bottomNavigationView.menu.findItem(R.id.services).isChecked = true
            parentFrag.viewModel.onItemSelected(menu.findItem(R.id.services))
        }

        mService.setOnClickListener {

        }

    }

    private fun showDialog() {

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.info_layout)
        val mOK = dialog.findViewById(R.id.mOK) as TextView
        val mText = dialog.findViewById(R.id.mText) as TextView
        mText.text = requireContext().resources.getString(R.string.insurance_info, activeUser)

        mOK.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    @SuppressLint("NotifyDataSetChanged", "SimpleDateFormat")
    override fun setObservers() {
        viewModel.remindersTabData.observe(viewLifecycleOwner) { tags ->
            if (tags != null)
                allFilterList = tags
        }

        viewModel.profileLogoData?.observe(viewLifecycleOwner) { profileLogo ->
            if (profileLogo != null) {

                val options = RequestOptions()
                avatar.clipToOutline = true
                Glide.with(requireContext())
                    .load(profileLogo)
                    .apply(
                        options.fitCenter()
                            .skipMemoryCache(true)
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(avatar)

            } else {
                viewModel.getUserLivedata()
            }
        }

        viewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val imageUrl = viewModel.getProfileImage(requireContext(), user)
                val options = RequestOptions()
                avatar.clipToOutline = true
                Glide.with(requireContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .apply(
                        options.fitCenter()
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(avatar)
            }
        }

        viewModel.progressData.observe(viewLifecycleOwner) { progressItem ->
            if (progressItem != null) {
                val profileItem = progressItem.profileProgress
                if (profileItem != null) {
                    firstNameHint.text = profileItem.description ?: " "

                    if (profileItem.completionPercent != null) {
                        if (profileItem.completionPercent == 100) {
                            mProfileRL.isVisible = false
                        } else {
                            mProfileRL.isVisible = true
                            profileProgress.max = 100
                            profileProgress.progress = profileItem.completionPercent
                            profileProgressLbl.text =
                                requireContext().resources.getString(
                                    R.string.progress,
                                    profileItem.completionPercent
                                )
                        }

                    } else {
                        profileProgressLbl.text =
                            "${requireContext().resources.getString(R.string.progress, 0)}"
                    }

                } else {
                    firstNameHint.isInvisible = true
                    profileProgressLbl.text =
                        "${requireContext().resources.getString(R.string.progress, 0)}"
                }

                vehicleProgressList = progressItem.vehicleProgress as ArrayList<VehicleProgressItem>


            }
        }

        viewModel.carsLivedata.observe(viewLifecycleOwner) { carList ->
            if (carList.isNullOrEmpty()) {
                carLL.isVisible = false
                noCarRL.isVisible = true
                carProgressRV.isVisible = false
            } else {
                noCarRL.isVisible = false
                carProgressRV.isVisible = true
                carProgressRV.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

                val car100: ArrayList<VehicleProgressItem> = ArrayList()

                for (i in vehicleProgressList.indices) {
                    if (vehicleProgressList[i].completionPercent != 100) {
                        car100.add(vehicleProgressList[i])
                    }
                }
                carAdapter = CarProgressAdapter(requireContext(), car100, this)
                carProgressRV.adapter = carAdapter
                carAdapter.setItems(car100)


                carLL.isVisible = true
                carRv.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                carDetailAdapter =
                    CarDetailAdapter(requireContext(), cars = arrayListOf(), this)
                carRv.adapter = carDetailAdapter
                carDetailAdapter.setItems(carList)
            }
        }

        viewModel.documentLivedata.observe(viewLifecycleOwner) { docList ->
            if (docList.isNullOrEmpty()) {
                documentLL.isVisible = true
                documentRv.isVisible = false
                viewDocument.isVisible = false
            } else {
                documentLL.isVisible = false
                documentRv.isVisible = true
                viewDocument.isVisible = true
                documentRv.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                recentDocAdapter = RecentDocumentAdapter(requireContext(), arrayListOf(), this)
                documentRv.adapter = recentDocAdapter
                recentDocAdapter.setItems(docList)
            }
        }

        viewModel.attachmentData.observe(viewLifecycleOwner) { documentData ->
            progressbar?.dismissPopup()
        }

        viewModel.stateStream.observe(viewLifecycleOwner) { state ->
            when (state) {
                HomeViewModel.STATE.DOCUMENT_NOT_FOUND -> {
                    showErrorInfo(requireContext(), getString(R.string.doc_not_found))
                }
            }
        }

        viewModel.remindersLiveData.observe(viewLifecycleOwner) { reminderList ->
            if (reminderList.isNullOrEmpty()) {
                reminderLL.isVisible = true
                reminderRv.isVisible = false
                viewReminder.isVisible = false
            } else {
                val listItems = ArrayList<Reminder>(reminderList.size)
                reminderLL.isVisible = false
                reminderRv.isVisible = true
                viewReminder.isVisible = true

                reminderAdapter = ReminderAdapter(
                    arrayListOf(),
                    this
                )
                reminderRv.adapter = reminderAdapter
                reminderList.sortWith { o1, o2 ->
                    val date1 = SimpleDateFormat("yyyy-MM-dd").parse(o1.dueDate)
                    val date2 = SimpleDateFormat("yyyy-MM-dd").parse(o2.dueDate)
                    date1.compareTo(date2)
                }
                reminderAdapter.clearAll()
                if (reminderList.size < 3) {
                    for (i in reminderList.indices)
                        listItems.add(reminderList[i])
                } else {
                    for (i in 0..2)
                        listItems.add(reminderList[i])
                }
                reminderAdapter.setItems(listItems, allFilterList)
            }
        }

        viewModel.historyLiveData.observe(viewLifecycleOwner) { historyList ->
            if (historyList.isNullOrEmpty()) {
                historyLL.isVisible = true
                historyRv.isVisible = false
                viewHistory.isVisible = false
            } else {
                historyLL.isVisible = false
                historyRv.isVisible = true
                viewHistory.isVisible = true
                historyRv.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = HistoryAdapter(requireContext(), arrayListOf(), this, "HOME")
                historyRv.adapter = adapter
                val list: ArrayList<HistoryItem> = ArrayList()
                if (historyList.size < 3) {
                    for (i in historyList.indices)
                        list.add(historyList[i])
                } else {
                    for (i in 0..2)
                        list.add(historyList[i])
                }
                adapter.setItems(list)
            }
        }
    }

    override fun onItemClick(item: VehicleProgressItem) {

        val params = Bundle()
        viewModel.mFirebaseAnalytics.logEvent(
            FirebaseAnalyticsList.ACCESS_CAR_COMPLETE_CARD,
            params
        )

        item.id?.let { viewModel.onEditCar(it) }
    }

    override fun onItemClick(item: Vehicle) {

        val params = Bundle()
        viewModel.mFirebaseAnalytics.logEvent(FirebaseAnalyticsList.ACCESS_CAR_HOME, params)

        item.id.let {
            if (it != null) {
                viewModel.onEditCar(it)
            }
        }
    }

    override fun onItemClick(item: HistoryItem) {
        val params = Bundle()
        viewModel.mFirebaseAnalytics.logEvent(FirebaseAnalyticsList.ACCESS_HISTORY_HOME, params)
        viewModel.onHistoryDetail(item)
    }

    override fun onItemClick(item: RowsItem) {

        val params = Bundle()
        viewModel.mFirebaseAnalytics.logEvent(
            FirebaseAnalyticsList.ACCESS_DOCUMENT_HOME,
            params
        )

        progressbar?.showPopup()
        item.href?.let { viewModel.fetchDocumentData(it , item) }

    }

    private fun saveFile(body: ResponseBody?, pathWhereYouWantToSaveFile: String): String? {
        if (body == null)
            return null
        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream(pathWhereYouWantToSaveFile)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }

            return pathWhereYouWantToSaveFile
        } catch (e: Exception) {

            showErrorInfo(requireContext(), getString(R.string.dwld_error))

        } finally {
            input?.close()

        }
        return null
    }


    override fun onResume() {
        super.onResume()

        if (AppPreferencesDelegates.get().language == "en-US") {

            offerOpinion.setImageResource(R.drawable.offer_opinion)
            offerMap.setImageResource(R.drawable.offer_map)
            offerReminder.setImageResource(R.drawable.offer_reminder)


        } else if (AppPreferencesDelegates.get().language == "en-RO") {

            offerOpinion.setImageResource(R.drawable.offer_opinion_ro)
            offerMap.setImageResource(R.drawable.offer_map_ro)
            offerReminder.setImageResource(R.drawable.offer_reminder_ro)

        }
    }

    override fun onItemClick(item: Reminder) {
        viewModel.onUpdate(item)
    }

}