package ro.westaco.carhome.presentation.screens.maps

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import ro.westaco.carhome.data.sources.remote.responses.models.SectionModel
import ro.westaco.carhome.databinding.SectionItemBinding
import ro.westaco.carhome.navigation.SingleLiveEvent

class CategoriesFilterAdapter(
    var context: Context,
    var viewLifecycleOwner: LifecycleOwner,
    var dataSource: ArrayList<SectionModel>,
    var selectedItems: ArrayList<SectionModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var intermediateSelectedItems: ArrayList<SectionModel> = ArrayList()
    private var selectedItemsLiveData: SingleLiveEvent<ArrayList<SectionModel>> = SingleLiveEvent()


    fun initIntermediateSelectedItems(list: ArrayList<SectionModel>) {
        list.forEach {
            intermediateSelectedItems.add(SectionModel(it.category, ArrayList()))
        }
    }

    fun initIntermediateWithSelectedItems() {
        intermediateSelectedItems.clear()
        intermediateSelectedItems.addAll(selectedItems)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CategoriesFilterViewHolder(
            SectionItemBinding.inflate(
                LayoutInflater.from(context)
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var item = dataSource[position]
        var customHolder = holder as CategoriesFilterViewHolder
        customHolder.binding.sectionNameTV.text = item.category
        var categoryForItemFromSelectedItems = selectedItems.find {
            item.category == it.category
        }
        val mapFilterBottomSheetAdapter = categoryForItemFromSelectedItems?.filters?.let {
            MapFilterBottomSheetAdapter(
                context,
                item.filters,
                it
            )
        }
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        customHolder.binding.categoriesRecyclerView.layoutManager = layoutManager
        customHolder.binding.categoriesRecyclerView.adapter = mapFilterBottomSheetAdapter

        mapFilterBottomSheetAdapter?.getSelectedItems()
            ?.observe(viewLifecycleOwner) { selectedFilters ->
                val categoryForSelectedItems = intermediateSelectedItems.find {
                    item.category == it.category
                }
                categoryForSelectedItems?.filters?.clear()
                categoryForSelectedItems?.filters?.addAll(selectedFilters)
                selectedItemsLiveData.value = intermediateSelectedItems
            }
    }

    fun getIntermediateSelectedItems(): SingleLiveEvent<ArrayList<SectionModel>> {
        return selectedItemsLiveData
    }


    override fun getItemCount(): Int {
        return dataSource.size
    }

    class CategoriesFilterViewHolder(itemView: SectionItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: SectionItemBinding = itemView
    }
}