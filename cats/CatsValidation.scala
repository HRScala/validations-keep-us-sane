package hrscala.validation

import Models.{Row, ScalaDeveloper}
import scala.util.Try
import cats.Apply
import cats.data.ValidatedNel
import cats.data.Validated.{invalidNel, valid}


object CatsValidation
    extends App
    with CommonCatsValidations
    with BusinessCatsValidations {

  def constructScalaDeveloper(row: Row): ValidatedNel[String, ScalaDeveloper] = {
    Apply[ValidatedNel[String, ?]].map3(
      nonEmptyString(row.cells(0)),
      positiveNumber(row.cells(1)) andThen noMinor,
      commaSeparatedStrings(row.cells(2)) andThen mustHaveScala
    )(ScalaDeveloper(_, _, _))
  }

  Models.people foreach { row =>
    println(constructScalaDeveloper(row))
  }
}

trait CommonCatsValidations {
  def nonEmptyString(input: String): ValidatedNel[String, String] =
    if (input != null && input.nonEmpty) valid(input) else invalidNel("Input string is empty")

  def number(input: String): ValidatedNel[String, Int] =
    nonEmptyString(input) andThen { nes =>
      Try(valid(input.toInt)).getOrElse(invalidNel(s"Invalid number format for input: $input"))
    }

  def positiveNumber(input: String): ValidatedNel[String, Int] =
    number(input) andThen { num =>
      if(num > 0L) valid(num) else invalidNel("The input value is not positive")
    }

  def commaSeparatedStrings(input: String): ValidatedNel[String, Seq[String]] =
    nonEmptyString(input) andThen  { nes =>
      valid(nes.split(", *").toSeq)
    }
}

trait BusinessCatsValidations {
  def noMinor(age: Int): ValidatedNel[String, Int] =
    if (age < 18) invalidNel("Person is a minor") else valid(age)

  def mustHaveScala(languages: Seq[String]): ValidatedNel[String, Seq[String]] =
    languages.find(_ == "scala") match {
      case _: Some[String] => valid(languages)
      case _ => invalidNel("Languages did not contain Scala")
    }
}