package com.lbole.common.tools.beans.tree;

/**
 * 节点大小平衡树（Size Balanced Tree）
 * 它是由中国广东中山纪念中学的陈启峰发明的。陈启峰于2006年底完成论文《Size Balanced Tree》，
 * 并在2007年的全国青少年信息学奥林匹克竞赛冬令营中发表。相比红黑树、AVL树等自平衡二叉查找树，
 * SBT更易于实现。据陈启峰在论文中称，SBT是“目前为止速度最快的高级二叉搜索树”。SBT能在O(log n)
 * 的时间内完成所有二叉搜索树(BST)的相关操作，而与普通二叉搜索树相比，SBT仅仅加入了简洁的核心
 * 操作Maintain。由于SBT赖以保持平衡的是size域而不是其他“无用”的域，它可以很方便地实现动态顺序
 * 统计中的select和rank操作。
 * SBT有两种版本，标准版（Maintain）和退化版。
 * 标准版特点：相对SPLAY,AVL,TREAP速度很快，代码短，不会退化，保证深度非常小。适用于任何程序中
 * 退化版：相对标准版SBT速度更快，代码更短，随机、有序数据不会退化（除人字形数据），深度也很小。
 * 在信息学竞赛中很实用，因为不太可能有人字型数据。但在实际应用中就不能保证一定不退化。
 *
 * @Author 马嘉祺
 * @Date 2020/9/25 0025 11 38
 * @Description <p></p>
 */
public class SBTree {
}
