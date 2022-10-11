object Main {
  def main(args: Array[String]): Unit = {
    println("Hello world!")

    val s = Swimmer()

  }

  trait Swimmer{
    def swim()
  }
}

