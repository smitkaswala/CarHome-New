package ro.westaco.carhome.presentation.screens.service.person

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_billing_information.*
import ro.westaco.carhome.R
import ro.westaco.carhome.data.sources.remote.responses.models.LegalPerson
import ro.westaco.carhome.data.sources.remote.responses.models.NaturalPerson
import ro.westaco.carhome.presentation.screens.service.bridgetax_rovignette.summary.BridgeTaxSummaryViewModel
import ro.westaco.carhome.presentation.screens.service.person.BillingInfoViewModel.Companion.legalItem
import ro.westaco.carhome.presentation.screens.service.person.BillingInfoViewModel.Companion.naturalItem

class BillingInformationFragment(
    var listener: OnServicePersonListener,
    var newListener : addNewPersonList,
    var type : String) :
    BottomSheetDialogFragment() {

    var adapter: BillingPagerAdapter? = null
    lateinit var viewModel: BridgeTaxSummaryViewModel
    private lateinit var bottomSheet: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var viewPager: ViewPager

    interface addNewPersonList {
        fun openNewPerson(type: String?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(BridgeTaxSummaryViewModel::class.java)

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val myview: View = inflater.inflate(R.layout.fragment_billing_information, container, false)
        val tabLayout = myview.findViewById<TabLayout>(R.id.tabs)
        val mClose = myview.findViewById<ImageView>(R.id.close)
        val mDissmiss = myview.findViewById<TextView>(R.id.dissmiss)
        val cta = myview.findViewById<TextView>(R.id.cta)
        val title = myview.findViewById<TextView>(R.id.title)
        viewPager = myview.findViewById(R.id.newPage)
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.natural_person)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.legal_person)))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = BillingPagerAdapter(childFragmentManager, listener , type , newListener)
        viewPager.adapter = adapter

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        mClose.setOnClickListener { dismiss() }
        mDissmiss.setOnClickListener { dismiss() }

        cta.setOnClickListener {
            listener.onPersonChange(naturalItem = naturalItem, legalItem = legalItem)
            dismiss()
        }

        return myview

    }


    interface OnServicePersonListener {
        fun onPersonChange(naturalItem: NaturalPerson?, legalItem: LegalPerson?)
    }

    override fun onStart() {
        super.onStart()
        bottomSheet =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup // notice the R root package
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

    }


    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }

    fun setTitle(position: Int) {
        when (position) {
            0 -> title.text = getString(R.string.billing_info)
            else -> title.text = getString(R.string.billing_info)
        }
    }

}