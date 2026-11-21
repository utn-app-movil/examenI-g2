package cr.ac.utn.examenI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.ac.utn.examenI.domain.model.MedCheckup
import cr.ac.utn.examenI.util.util

class MedListAdapter(
    private val items: List<MedCheckup>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MedListAdapter.MedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_list, parent, false)
        return MedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedViewHolder, position: Int) {
        holder.bind(items[position], itemClickListener)
    }

    override fun getItemCount(): Int = items.size

    class MedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvId = view.findViewById<TextView>(R.id.txtText1_Item)
        private val tvName = view.findViewById<TextView>(R.id.txtText2_Item)
        private val tvVitals = view.findViewById<TextView>(R.id.txtText3_Item)
        private val tvDateTime = view.findViewById<TextView>(R.id.txtText4_Item)
        private val imgPicture = view.findViewById<ImageView>(R.id.imgPicture_Item)

        fun bind(item: MedCheckup, listener: OnItemClickListener) {
            tvId.text = item.id
            tvName.text = item.fullName
            tvVitals.text = listOf(
                item.bloodPressure,
                item.weight.toString(),
                item.height.toString(),
                item.oxygenation.toString()
            ).joinToString(" | ")
            tvDateTime.text = util.formatDateTime(item.checkDateTime)
            imgPicture.setImageResource(R.drawable.ic_launcher_foreground)
            itemView.setOnClickListener { listener.onItemClick(item) }
        }
    }
}