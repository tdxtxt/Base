package com.tdxtxt.baselib.adapter.recycle

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.entity.node.NodeFooterImp

/**
 * <pre>
 *     author : ton
 *     time   : 2022/9/7
 *     desc   : 官方已经停止维护了，只能自己修复：最后一个item删除、替换崩溃问题
 * </pre>
 */
abstract class BaseNodeFixAdapter(nodeList: MutableList<BaseNode>? = null) : BaseNodeAdapter(nodeList) {

    // 此方法有误，需要修复
    override fun removeAt(position: Int) {
        val removeCount = removeNodesAtFix(position)
        notifyItemRangeRemoved(position + headerLayoutCount, removeCount)
        compatibilityDataSizeChanged(0)
    }

    override fun setData(index: Int, data: BaseNode) {
        // 先移除，再添加
        val removeCount = removeNodesAtFix(index)

        val newFlatData = flatData(arrayListOf(data))
        this.data.addAll(index, newFlatData)

        if (removeCount == newFlatData.size) {
            notifyItemRangeChanged(index + headerLayoutCount, removeCount)
        } else {
            notifyItemRangeRemoved(index + headerLayoutCount, removeCount)
            notifyItemRangeInserted(index + headerLayoutCount, newFlatData.size)

//        notifyItemRangeChanged(index + getHeaderLayoutCount(), max(removeCount, newFlatData.size)
        }
    }

    /**
     * 从数组中移除
     * @param position Int
     * @return Int 被移除的数量
     */
    private fun removeNodesAtFix(position: Int): Int {
        if (position >= data.size) {
            return 0
        }
        // 记录被移除的item数量,先移除子项
        var removeCount = removeChildAt(position)
        val node = this.data[position]

        // 是否存在移除的脚部
        val isRemoveFooter = node is NodeFooterImp && node.footerNode != null

        // 移除node自己
        this.data.removeAt(position)
        removeCount += 1

        // 移除脚部
        if (isRemoveFooter) {
            this.data.removeAt(position)
            removeCount += 1
        }
        return removeCount
    }

    private fun removeChildAt(position: Int): Int {
        if (position >= data.size) {
            return 0
        }
        // 记录被移除的item数量
        var removeCount = 0

        val node = this.data[position]
        // 移除子项
        if (!node.childNode.isNullOrEmpty()) {
            if (node is BaseExpandNode) {
                if (node.isExpanded) {
                    val items = flatData(node.childNode!!)
                    this.data.removeAll(items)
                    removeCount = items.size
                }
            } else {
                val items = flatData(node.childNode!!)
                this.data.removeAll(items)
                removeCount = items.size
            }
        }
        return removeCount
    }

    private fun flatData(list: Collection<BaseNode>, isExpanded: Boolean? = null): MutableList<BaseNode> {
        val newList = ArrayList<BaseNode>()

        for (element in list) {
            newList.add(element)

            if (element is BaseExpandNode) {
                // 如果是展开状态 或者需要设置为展开状态
                if (isExpanded == true || element.isExpanded) {
                    val childNode = element.childNode
                    if (!childNode.isNullOrEmpty()) {
                        val items = flatData(childNode, isExpanded)
                        newList.addAll(items)
                    }
                }
                isExpanded?.let {
                    element.isExpanded = it
                }
            } else {
                val childNode = element.childNode
                if (!childNode.isNullOrEmpty()) {
                    val items = flatData(childNode, isExpanded)
                    newList.addAll(items)
                }
            }

            if (element is NodeFooterImp) {
                element.footerNode?.let {
                    newList.add(it)
                }
            }
        }
        return newList
    }
}