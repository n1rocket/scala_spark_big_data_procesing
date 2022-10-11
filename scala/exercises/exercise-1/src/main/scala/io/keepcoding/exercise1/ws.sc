import scala.language.implicitConversions
import scala.util.Try

val numSet = Set(1, 1, 2, 4, 5, 5, 7, 9)
val numList = List(1, 2, 3, 4, 5, 6, 7, 8, 9)

numSet.exists(elem => elem == 2)
numSet.contains(2)

numList.filter(_ % 2 == 0) // Devuelve todos los pares
numList.map(_ * 10) // Multiplica por 10 todos los valores
numList.fold(0)((acc, curr) => acc + curr) // Acumula todos los valores
numList.sum // Acumula todos los valores

//todos estos métodos no modifican la lista, devuelven el resiltado en una lista

val strings = List("Hola", "Soy un", "Robot")
strings.map(str => str.toList)
strings.map(str => str.toList).flatten

strings.flatMap(str => str.toList)

//Options

def divide(a: Int, b: Int): Option[Double] = {
  if (b == 0) None
  else Some(a / b)
}

val res1 = divide(12, 3)
val res2 = divide(12, 0)

res1 match {
  case Some(value) => println(s"Resultado: $value")
  case None => println(s"No se puede dividir entre 0")
}

res1.getOrElse(0.0)
res2.isEmpty
res1.isEmpty

res2.map(v => v + 10)


//For Comprehension - Exercise 4

//TRY

def dividirTry(a: Int, b: Int): Try[Double] = Try(a / b)

val div = dividirTry(12, 0)
dividirTry(12, 2)

div.getOrElse(0.0)


//Implicit Parameters
def printPrefix(text: String)(implicit prefix: String): Unit =
  println(s"$prefix - $text")

implicit val prefix: String = "TAG"

printPrefix("Texto")

//Implicit conversion

case class Point(x: Int, y: Int)

implicit def tupleToPoint(tuple: (Int, Int)): Point =
  Point(tuple._1, tuple._2)

def sumPoints(p1:Point, p2:Point) =
  Point(p1.x + p2.x, p1.y + p2.y)

//Permite usar Point o tuplas a la vez

val p1: Point = Point(1,2)
val p2: (Int, Int) = (3,4)

sumPoints(p1, p2)

//Implicit class
val str = "Keepcoding"

implicit class EnrichExternalClass(str: String){
  def toUpperReverse = str.reverse.toUpperCase
}

str.toUpperReverse