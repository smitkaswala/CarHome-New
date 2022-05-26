package ro.westaco.carhome.presentation.screens.service.insurance.selectcar

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import ro.westaco.carhome.R
import ro.westaco.carhome.data.sources.remote.responses.models.Vehicle
import ro.westaco.carhome.di.ApiModule
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SelectCarsAdapter(
    private val context: Context,
    private var cars: ArrayList<Vehicle>,
    private val listener: OnSelectCarsInteractionListener? = null,
) : RecyclerView.Adapter<SelectCarsAdapter.ViewHolder>() {


    interface OnSelectCarsInteractionListener {
        fun onItemClick(item: Vehicle)
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = cars.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_in_cars, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var logo: ImageView = itemView.findViewById(R.id.logo)
        private var makeAndModel: TextView = itemView.findViewById(R.id.makeAndModel)
        private var carNumber: TextView = itemView.findViewById(R.id.carNumber)
        private var policyExpiry: TextView = itemView.findViewById(R.id.policyExpiry)
        private var status: TextView = itemView.findViewById(R.id.status)
        private var vehicleBrandCar: TextView = itemView.findViewById(R.id.vehicleBrandCar)

        @SuppressLint("NewApi", "SimpleDateFormat", "SetTextI18n")
        fun bind(position: Int) {

            val item = cars[position]

            makeAndModel.text = "${item.vehicleBrand ?: ""} ${item.model ?: ""}"

            /*vehicleBrandCar.text = item.vehicleBrand ?: ""*/
            carNumber.text = item.licensePlate

            if (item.policyExpirationDate.isNullOrEmpty()) {
                policyExpiry.text = "N/A"
                status.text = "N/A"
                policyExpiry.setTextColor(context.getColor(R.color.unselected))
                status.setTextColor(context.getColor(R.color.unselected))

            } else {

                val dateFormat: DateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val date: Date? = dateFormat.parse(item.policyExpirationDate)
                val formatter: DateFormat =
                    SimpleDateFormat("dd-MM-yyyy")
                val dateStr: String =
                    formatter.format(date)
                policyExpiry.text = dateStr
                policyExpiry.setTextColor(context.getColor(R.color.text_color))

                val sdf = SimpleDateFormat("dd-MM-yyyy")
                val strDate = sdf.parse(dateStr)
                if (System.currentTimeMillis() > strDate.time) {
                    status.text = context.getString(R.string.status_expires)
                    status.setTextColor(context.getColor(R.color.redExpiredRovii))
                } else {
                    status.text = context.getString(R.string.status_active)
                    status.setTextColor(context.getColor(R.color.list_time))
                }

            }


            val options = RequestOptions()
            logo.clipToOutline = true
            Glide.with(context)
                .load(ApiModule.BASE_URL_RESOURCES + item.vehicleBrandLogo)
                .apply(
                    options.fitCenter()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .error(R.drawable.carhome_icon_roviii)
                .into(logo)

            itemView.setOnClickListener {

                listener?.onItemClick(item)
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(cars: List<Vehicle>?) {
        this.cars = ArrayList(cars ?: listOf())
        notifyDataSetChanged()
    }


}