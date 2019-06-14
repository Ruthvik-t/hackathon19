package com.piedpipers.sawy

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter


class ProductCarouselAdapter(val context: Context, val items: List<Item>, val layoutInflater: LayoutInflater) :
    PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun getCount(): Int = items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val itemView = layoutInflater.inflate(R.layout.product_card, container, false)

        val imageView = itemView.findViewById(R.id.product_image) as ImageView
        imageView.setImageDrawable(getDrawable(items[position].imageName))
        val titleView = itemView.findViewById<TextView>(R.id.product_name)
        titleView.text = items[position].name
        itemView.findViewById<TextView>(R.id.product_price).text = "£${items[position].price}"
        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    private fun getDrawable(name: String): Drawable {
        val resources = context.resources
        val resourceId = resources.getIdentifier(
            name, "drawable",
            context.packageName
        )
        return context.getDrawable(resourceId)
    }

}