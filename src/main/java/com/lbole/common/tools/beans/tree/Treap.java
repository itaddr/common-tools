package com.lbole.common.tools.beans.tree;

/**
 * 树堆（Tree+Heap），在数据结构中也称Treap
 * 是指有一个随机附加域满足堆的性质的二叉搜索树，其结构相当于以随机数据插入的二叉搜索树。
 * 其基本操作的期望时间复杂度为O(log n)。相对于其他的平衡二叉搜索树，Treap的特点是实现简单，
 * 且能基本实现随机平衡的结构。Treap是一棵二叉排序树，它的左子树和右子树分别是一个Treap，
 * 和一般的二叉排序树不同的是，Treap纪录一个额外的数据，就是优先级。Treap在以关键码构成
 * 二叉排序树的同时，还满足堆的性质(在这里我们假设节点的优先级大于该节点的孩子的优先级)。
 * 但是这里要注意的是Treap和二叉堆有一点不同，就是二叉堆必须是完全二叉树，而Treap可以并不一定是。
 *
 * @Author 马嘉祺
 * @Date 2020/9/25 0025 11 19
 * @Description <p></p>
 */
public class Treap {
}
