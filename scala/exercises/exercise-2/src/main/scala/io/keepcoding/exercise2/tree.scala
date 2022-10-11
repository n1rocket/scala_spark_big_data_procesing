package io.keepcoding.exercise2

sealed trait Tree

case object Leaf extends Tree

case class Node(left: Tree, value: Int, right: Tree) extends Tree


object Tree {

  def preOrder(node: Tree): List[Int] = node match {
    case Leaf => List.empty
    case Node(left, value, right) => List(value) ++ preOrder(left) ++ preOrder(right)
  }

  def inOrder(node: Tree): List[Int] = ???

  def postOrder(node: Tree): List[Int] = ???

}
