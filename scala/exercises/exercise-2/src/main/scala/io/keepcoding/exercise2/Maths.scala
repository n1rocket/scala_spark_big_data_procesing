package io.keepcoding.exercise2

object Maths {

  def fibonacci(n: Int): Int = n match {
    case n < 0 => throw new RuntimeException
    case n == 0 => 0
    case n == 1 => 1
    case n => fibonacci(n -1) + fibonacci(n - 2)
  }

  def main(args: Array[String]): Unit = {
    //con IFS
    //    if (n < 0) throw new RuntimeException
    //    else if (n == 0) 0
    //    else if (n == 1) 1
    //    else fibonacci(n - 1) + fibonacci(n - 2)
  }

}
