package uz.kabir.pastimegame

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import uz.kabir.pastimegame.databinding.RecycleItemLayoutBinding



class LanguageAdapter(private val languageList: List<LanguageItem>) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var itemClickListener: ((LanguageItem) -> Unit)? = null

    fun setOnClickListener(listener: (LanguageItem) -> Unit) {
        itemClickListener = listener
    }

    inner class LanguageViewHolder(val binding: RecycleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(languageItem: LanguageItem) {
            when (languageItem.iconCode) {
                "uz" -> binding.languageIcon.setImageResource(R.drawable.ic_flag_uz)
                "kaa" -> binding.languageIcon.setImageResource(R.drawable.ic_flag_ka)
                "en" -> binding.languageIcon.setImageResource(R.drawable.ic_flag_uk)
                "ru" -> binding.languageIcon.setImageResource(R.drawable.ic_flag_ru)
                "es" -> binding.languageIcon.setImageResource(R.drawable.ic_flag_es)
                "fr" -> binding.languageIcon.setImageResource(R.drawable.ic_flag_fr)
                else -> binding.languageIcon.setImageResource(R.drawable.ic_flag_uz)
            }
            binding.languageName.text = languageItem.name

            binding.root.setOnClickListener {
                itemClickListener?.invoke(languageItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = RecycleItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(languageList[position])
    }

    override fun getItemCount(): Int = languageList.size
}