package uz.kabir.pastimegame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.kabir.pastimegame.AnimationButton.animateClick
import uz.kabir.pastimegame.databinding.RecycleItemLayoutBinding


class LanguageAdapter(
    private val languageList: List<LanguageItem>,
    initiallySelectedIconCode: String? = null // Receive the saved code
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var onItemClickListener: ((LanguageItem) -> Unit)? = null
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    init {
        initiallySelectedIconCode?.let { savedCode ->
            selectedPosition = languageList.indexOfFirst { it.iconCode == savedCode }
        }
    }

    fun setOnClickListener(listener: (LanguageItem) -> Unit) {
        onItemClickListener = listener
    }

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val languageNameTextView: TextView = itemView.findViewById(R.id.language_name)
        private val languageIconImageView: ImageView = itemView.findViewById(R.id.language_icon)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val previousSelectedPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedPosition)
                    onItemClickListener?.invoke(languageList[adapterPosition])
                    itemView.animateClick()
                }
            }
        }

        fun bind(languageItem: LanguageItem) {
            languageNameTextView.text = languageItem.name
            when (languageItem.iconCode) {
                "uz" ->  languageIconImageView.setImageResource(R.drawable.ic_flag_uz)
                "kaa" -> languageIconImageView.setImageResource(R.drawable.ic_flag_ka)
                "en" ->  languageIconImageView.setImageResource(R.drawable.ic_flag_uk)
                "ru" ->  languageIconImageView.setImageResource(R.drawable.ic_flag_ru)
                "es" ->  languageIconImageView.setImageResource(R.drawable.ic_flag_es)
                "fr" ->  languageIconImageView.setImageResource(R.drawable.ic_flag_fr)
                else ->  languageIconImageView.setImageResource(R.drawable.ic_flag_uz)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_layout, parent, false)
        return LanguageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val currentItem = languageList[position]
        holder.bind(currentItem)

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.background_boundary_color)
        } else {
            holder.itemView.setBackgroundResource(0)
        }
    }

    override fun getItemCount() = languageList.size
}