package com.example.mobiletravel

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobiletravel.baseclass.Place
import java.util.*

class SearchResultAdapter(private val cellClickListener: CellClickListener): RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() ,
    Filterable{
    var searchResultList = listOf<Place>()
    var backupList = listOf<Place>()
        set(value){
            field = value
            searchResultList = value
            notifyDataSetChanged()
        }



    interface CellClickListener {
        fun onCellClickListener(data: Place)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val cardImg: ImageView = view.findViewById(R.id.cardImg)
        val exploreCardTitle: TextView = view.findViewById(R.id.exploreCardTitle)
        val exploreCardRating: TextView = view.findViewById(R.id.exploreCardRating)
        val exploreCardCity: TextView = view.findViewById(R.id.exploreCardCity)
        val exploreCardCategory: TextView = view.findViewById(R.id.exploreCardCategory)
        val exploreCardStatus: TextView = view.findViewById(R.id.exploreCardStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.explore_recycleview_cell,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = searchResultList[position]
        holder.exploreCardTitle.text = item.name
        if(item.avgRating != null) holder.exploreCardRating.text = "Rating: " + item.avgRating.substring(0,3)  else holder.exploreCardRating.text ="Rating: 0"
        holder.exploreCardCity.text = item.city
        if(item.category.equals(1))
            holder.exploreCardCategory.text = ("Category: Attraction")
        else if(item.category.equals(2))
            holder.exploreCardCategory.text = ("Category: Accommodation")
        else
            holder.exploreCardCategory.text = ("Category: Food")

        if(getOperationStatus(item.openHour!!, item.closeHour!!) == 1){
            when(item.category){
                2 -> holder.exploreCardStatus.text = ""
                else ->{
                    holder.exploreCardStatus.text = "Open now"
                    holder.exploreCardStatus.setTextColor(Color.parseColor("#009900"))
                }
            }
        }else{
            when(item.category){
                2 -> holder.exploreCardStatus.text = ""
                else ->{
                    holder.exploreCardStatus.text = "Closed"
                    holder.exploreCardStatus.setTextColor(Color.parseColor("#990000"))
                }
            }
        }

        Glide
            .with(holder.cardImg)
            .load("https://travelimgreg.000webhostapp.com/src/img/attractions/"+item.imageLink)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .fallback(R.drawable.ic_launcher_foreground)
            .into(holder.cardImg)

        holder.itemView.setOnClickListener(){
            cellClickListener.onCellClickListener(item)
        }

    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    private fun getOperationStatus(open:String, close:String): Int{
        var status = 0
        val currentTime: Date = Calendar.getInstance().time
        val tempOpen: Int = open.substring(0,2).toInt()
        val tempClose: Int = close.substring(0,2).toInt()
        if(currentTime.hours >= tempOpen && currentTime.hours <= tempClose)
            status = 1
        else
            status = 0

        return status
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val selection = when(constraint){
                    "Attraction" -> 1
                    "Accommodation" -> 2
                    else -> 3
                }
                var filteredList = listOf<Place>()
                if(constraint.toString().isEmpty()){
                    val filterResults = FilterResults()
                    filterResults.values = backupList
                    return filterResults
                }else{
                    for(row in searchResultList){
                        if(row.category.equals(selection)){
                            filteredList += row
                        }
                    }

                    val filterResults = FilterResults()
                    filterResults.values = filteredList
                    return filterResults
                }

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                searchResultList = results?.values as List<Place>
                notifyDataSetChanged()
            }

        }
    }

}