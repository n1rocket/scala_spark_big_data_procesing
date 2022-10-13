package io.keepcoding.exercise2

object Maths {

  def fibonacci(n: Int): Int = n match {
    case n if n < 0 => throw new RuntimeException
    case 0 | 1 => n
    case _ => fibonacci(n - 1) + fibonacci(n - 2)
  }

  def main(args: Array[String]): Unit = {
    //con IFS
    //    if (n < 0) throw new RuntimeException
    //    else if (n == 0) 0
    //    else if (n == 1) 1
    //    else fibonacci(n - 1) + fibonacci(n - 2)
  }

}
