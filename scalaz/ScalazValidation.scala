import Models.{Row, ScalaDeveloper}

import scala.util.Try
import scalaz.Validation.FlatMap._
import scalaz.ValidationNel
import scalaz.syntax.validation._
import scalaz.syntax.applicative._

object ScalazValidation
    extends App
    with CommonScalazValidations
    with BusinessScalazValidations {

  def constructScalaDeveloper(row: Row): ValidationNel[String, ScalaDeveloper] = {
    val name      = nonEmptyString(row.cells(0))
    val age       = positiveNumber(row.cells(1)) flatMap noMinor
    val languages = commaSeparatedStrings(row.cells(2)) flatMap mustHaveScala

    (name |@| age |@| languages) {
      ScalaDeveloper(_, _, _)
    }
  }

  Models.people foreach { row =>
    println(constructScalaDeveloper(row))
  }
}

trait CommonScalazValidations {
  def nonEmptyString(input: String): ValidationNel[String, String] =
    if (input != null && input.nonEmpty) input.successNel else "Input string is empty".failureNel

  def number(input: String): ValidationNel[String, Int] =
    nonEmptyString(input).flatMap { nes =>
      Try(input.toInt.successNel[String]).getOrElse(s"Invalid number format for input: $input".failureNel)
    }

  def positiveNumber(input: String): ValidationNel[String, Int] =
    number(input).flatMap { num =>
      if(num > 0L) num.successNel else "The input value is not positive".failureNel
    }

  def commaSeparatedStrings(input: String): ValidationNel[String, Seq[String]] =
    nonEmptyString(input).flatMap { nes =>
      nes.split(", *").toSeq.successNel
    }
}

trait BusinessScalazValidations {
  def noMinor(age: Int): ValidationNel[String, Int] =
    if (age < 18) "Person is a minor".failureNel else age.successNel

  def mustHaveScala(languages: Seq[String]): ValidationNel[String, Seq[String]] =
    languages.find(_ == "scala") match {
      case _: Some[String] => languages.successNel
      case _ => "Languages did not contain Scala".failureNel
    }
}
