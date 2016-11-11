object Models {

  // INPUT

  case class Row(cells: String*)

  val people = List(
    Row("Ivan",   "30",        "scala, java, c"),
    Row("Mirko",  "millenial", "js"),
    Row("Josip",  "30",        "scala, haskell"),
    Row("Miro",   "40",        "java, php")
  )

  // OUTPUT

  case class ScalaDeveloper(name: String, age: Int, languages: Seq[String])
}