package com.buptsdmda.openbyrtv

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


class FileViewAdapter(list: MutableList<String>?) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.file_view_item, list) {
    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(
            R.id.textView,
            item
        )
    }

}