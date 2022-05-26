package ro.westaco.carhome.presentation.screens.data.cars

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import ro.westaco.carhome.R
import ro.westaco.carhome.data.sources.remote.responses.models.Vehicle
import ro.westaco.carhome.di.ApiModule

class CarsAdapter(
    private val context: Context,
    private var cars: ArrayList<Vehicle>,
    private val listener: OnSelectCarListner? = null

) : RecyclerView.Adapter<CarsAdapter.ViewHolder>() {
    var selectedPos: Int = -1

    interface OnSelectCarListner {
        fun onItemClick(item: Vehicle)
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int {
        return cars.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_car_list_, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var mImage: AppCompatImageView = itemView.findViewById(R.id.mImage)
        private var mSelected: ImageView = itemView.findViewById(R.id.mSelected)
        private var mCarName: TextView = itemView.findViewById(R.id.mCarName)
        private var mCarNumberPlate: TextView = itemView.findViewById(R.id.mCarNumberPlate)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            val item = cars[position]


            if (item.vehicleBrand?.isNotEmpty() == true && item.model?.isNotEmpty() == true) {

                mCarName.text = "${item.vehicleBrand} ${item.model}"

            } else {

                mCarName.text = "-"
            }

            mCarNumberPlate.text = item.licensePlate

            val options = RequestOptions()
            mImage.clipToOutline = true
            Glide.with(context)
                .load(ApiModule.BASE_URL_RESOURCES + item.vehicleBrandLogo)
                .apply(
                    options.fitCenter()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .error(R.drawable.carhome_icon_roviii)
                .into(mImage)

            if (selectedPos == position)
                mSelected.setImageResource(R.drawable.ic_selected_done_)
            else
                mSelected.setImageResource(R.drawable.ic_selected_blank_)

            itemView.setOnClickListener {

                val prevSelectedPos = selectedPos
                selectedPos = position
                notifyItemChanged(prevSelectedPos)
                notifyItemChanged(selectedPos)
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