package com.alijt.foodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ItemCouponBinding
import com.alijt.foodapp.model.Coupon
import java.text.SimpleDateFormat
import java.util.Locale

class CouponAdapter(
    private val onEditClick: (Coupon) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : ListAdapter<Coupon, CouponAdapter.CouponViewHolder>(CouponDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = getItem(position)
        holder.bind(coupon, onEditClick, onDeleteClick)
    }

    class CouponViewHolder(private val binding: ItemCouponBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: Coupon, onEditClick: (Coupon) -> Unit, onDeleteClick: (Int) -> Unit) {
            binding.tvCouponCode.text = binding.root.context.getString(R.string.coupon_code_format, coupon.coupon_code)
            binding.tvCouponType.text = binding.root.context.getString(R.string.coupon_type_format, coupon.type)

            // نمایش مقدار کوپن بر اساس نوع (درصدی یا ثابت)
            binding.tvCouponValue.text = if (coupon.type == "percent") {
                binding.root.context.getString(R.string.coupon_value_format, "${coupon.value.toInt()}% off")
            } else {
                binding.root.context.getString(R.string.coupon_value_format, "$%.2f".format(coupon.value))
            }

            // فرمت‌بندی تاریخ‌ها
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDateFormatted = try { dateFormatter.format(dateFormatter.parse(coupon.start_date)!!) } catch (e: Exception) { coupon.start_date }
            val endDateFormatted = try { dateFormatter.format(dateFormatter.parse(coupon.end_date)!!) } catch (e: Exception) { coupon.end_date }

            binding.tvCouponDates.text = binding.root.context.getString(R.string.coupon_dates_format, startDateFormatted, endDateFormatted)

            binding.btnEditCoupon.setOnClickListener { onEditClick(coupon) }
            binding.btnDeleteCoupon.setOnClickListener { onDeleteClick(coupon.id) }
        }
    }

    private class CouponDiffCallback : DiffUtil.ItemCallback<Coupon>() {
        override fun areItemsTheSame(oldItem: Coupon, newItem: Coupon): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Coupon, newItem: Coupon): Boolean {
            return oldItem == newItem
        }
    }
}