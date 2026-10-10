package com.example.phonehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class CompareFragment : Fragment() {

    private var tvPhone1Title: TextView? = null
    private var tvPhone1Specs: TextView? = null
    private var tvPhone2Title: TextView? = null
    private var tvPhone2Specs: TextView? = null

    private val viewModel: PhoneViewModel by activityViewModels {
        val application = requireActivity().application as PhoneApplication
        PhoneViewModelFactory(application.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPhone1Title = view.findViewById(R.id.tvPhone1Title)
        tvPhone1Specs = view.findViewById(R.id.tvPhone1Specs)
        tvPhone2Title = view.findViewById(R.id.tvPhone2Title)
        tvPhone2Specs = view.findViewById(R.id.tvPhone2Specs)

        viewModel.comparedPhones.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                val p1 = list[0]
                tvPhone1Title?.text = "${p1.brand} ${p1.model}"
                tvPhone1Specs?.text = "Ціна: ${p1.price} грн\nОЗП: ${p1.ramGb} ГБ\nПам'ять: ${p1.storageGb} ГБ\nБатарея: ${p1.batteryCapacity} мАг"
            } else {
                tvPhone1Title?.text = getString(R.string.phone_1_not_selected)
                tvPhone1Specs?.text = ""
            }

            if (list.size > 1) {
                val p2 = list[1]
                tvPhone2Title?.text = "${p2.brand} ${p2.model}"
                tvPhone2Specs?.text = "Ціна: ${p2.price} грн\nОЗП: ${p2.ramGb} ГБ\nПам'ять: ${p2.storageGb} ГБ\nБатарея: ${p2.batteryCapacity} мАг"
            } else {
                tvPhone2Title?.text = getString(R.string.phone_2_not_selected)
                tvPhone2Specs?.text = ""
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tvPhone1Title = null
        tvPhone1Specs = null
        tvPhone2Title = null
        tvPhone2Specs = null
    }
}