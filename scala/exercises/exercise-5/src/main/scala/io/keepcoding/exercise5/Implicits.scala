package io.keepcoding.exercise5

object Implicits {

  /* Reference:
    class ExternalClass {
      private val list: List[String] = List(.....)
      def getSize(): Int = list.size
    }

    implicit class EnrichExternalClass(ex: ExternalClass){
      def isEmpty: Boolean =ex.getSize == 0
    }
  */

  // Target: "Hello world!".toPassword == "H3ll0 w0rld!"

  implicit class EnrichString(s: String) {
    def toPassword: String = {

      val substitutions = Map(
        'e' -> '3',
        'a' -> '4',
        'i' -> '1',
        'o' -> '0',
      )
      s.map(c => substitutions.getOrElse(c, c))

      /*
        val s1 = s.replaceAll("e", "3")
        val s2 = s1.replaceAll("a", "4")
        val s3 = s2.replaceAll("i", "1")
        s3.replaceAll("o", "0")
       */
    }
  }

}
