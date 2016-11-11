package hrscala.validation

object Models {

  // INPUT

  case class Row(cells: String*)

  val people = List(
    Row("Ivan",   "30",        "scala, java, c"),
    Row("Mirko",  "millenial", "js"),
    Row("Josip",  "30",        "scala, haskell"),
    Row("Miro",   "400",        "java, php, scala")
  )

  // OUTPUT

  case class ScalaDeveloper(name: String, age: Int, languages: Seq[String]) {
    assert(name != null && name.nonEmpty, {
      "Name cannot be empty"
    })
    require(age >= 18, "cannont be minor")
    require(languages.contains("scala"), "The developer must have scala language.")
  }
}

object RunIt extends App {
  println(Models.ScalaDeveloper("aa", 13, Seq("js", "haskell", "scala")))
}
