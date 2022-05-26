package ro.westaco.carhome.presentation.screens.data.cars

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ro.westaco.carhome.data.sources.remote.apis.CarHomeApi
import ro.westaco.carhome.data.sources.remote.responses.models.Vehicle
import ro.westaco.carhome.navigation.BundleProvider
import ro.westaco.carhome.navigation.Screen
import ro.westaco.carhome.navigation.SingleLiveEvent
import ro.westaco.carhome.navigation.UiEvent
import ro.westaco.carhome.navigation.events.NavAttribs
import ro.westaco.carhome.presentation.base.BaseViewModel
import ro.westaco.carhome.presentation.screens.data.cars.details.CarDetailsFragment
import ro.westaco.carhome.presentation.screens.service.bridgetax_rovignette.init.PassTaxInitFragment
import ro.westaco.carhome.presentation.screens.service.vignette.buy.BuyVignetteFragment
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject


@HiltViewModel
class CarsViewModel @Inject constructor(
    private val app: Application,
    private val api: CarHomeApi
) : BaseViewModel() {

    val carsLivedata: MutableLiveData<ArrayList<Vehicle>> = MutableLiveData()

    override fun onFragmentCreated() {
        fetchRemoteData()
    }

    val stateStream: SingleLiveEvent<STATE> = SingleLiveEvent()

    enum class STATE {
        DOCUMENT_NOT_FOUND
    }

    private fun fetchRemoteData() {
        api.getVehicles()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                carsLivedata.value = resp?.data
            }, {
                it.printStackTrace()
                carsLivedata.value = null
            })
    }

    internal fun onItemClick(item: Vehicle) {
        uiEventStream.value =
            UiEvent.Navigation(NavAttribs(Screen.CarDetails, object : BundleProvider() {
                override fun onAddArgs(bundle: Bundle?): Bundle {
                    return Bundle().apply {
                        putSerializable(CarDetailsFragment.ARG_CAR, item)
                    }
                }
            }))
    }

    internal fun onAddNew() {
        uiEventStream.value = UiEvent.Navigation(NavAttribs(Screen.QueryCar))
    }

    internal fun onBuyPassTax(vehicle: Vehicle) {
        uiEventStream.value =
            UiEvent.Navigation(NavAttribs(Screen.BridgeTaxInit, object : BundleProvider() {
                override fun onAddArgs(bundle: Bundle?): Bundle {
                    return Bundle().apply {

                        putSerializable(PassTaxInitFragment.ARG_CAR, vehicle)
                    }
                }
            }))
    }

    internal fun onBuyInsurance(vehicle: Vehicle) {
        uiEventStream.value =
            UiEvent.Navigation(NavAttribs(Screen.Insurance, object : BundleProvider() {
                override fun onAddArgs(bundle: Bundle?): Bundle {
                    return Bundle().apply {

                        putSerializable(BuyVignetteFragment.ARG_CAR, vehicle)
                    }
                }
            }))
    }

    internal fun onBuyVignette(vehicle: Vehicle) {
        uiEventStream.value =
            UiEvent.Navigation(NavAttribs(Screen.BuyVignette, object : BundleProvider() {
                override fun onAddArgs(bundle: Bundle?): Bundle {
                    return Bundle().apply {

                        putSerializable(BuyVignetteFragment.ARG_CAR, vehicle)
                    }
                }
            }))
    }


}