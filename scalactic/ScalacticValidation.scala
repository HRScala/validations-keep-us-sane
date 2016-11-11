package hrscala.validation

import Models.{Row, ScalaDeveloper}
import scala.util.Try
import org.scalactic._
import Accumulation._

object ScalacticValidation
    extends App
    with CommonScalacticValidations
    with BusinessScalacticValidations {

  def constructScalaDeveloper(row: Row): ScalaDeveloper Or Every[ErrorMessage] = {
    val name      = nonEmptyString(row.cells(0))
    val age       = positiveNumber(row.cells(1)) when(noMinor)
    val languages = commaSeparatedStrings(row.cells(2)) when mustHaveScala

    withGood(name, age, languages) {
      ScalaDeveloper(_, _, _)
    } when(notTooOld)
  }

  Models.people foreach { row =>
    println(constructScalaDeveloper(row))
  }
}


trait CommonScalacticValidations {
  def nonEmptyString(input: String): String Or One[ErrorMessage] =
    if (input != null && input.nonEmpty) Good(input) else Bad(One("Input string is empty"))

  def number(input: String): Int Or One[ErrorMessage] =
    nonEmptyString(input) flatMap { nes =>
      Try(Good(input.toInt)).getOrElse(Bad(One(s"Invalid number format for input: $input")))
    }

  def positiveNumber(input: String): Int Or One[ErrorMessage] =
    number(input) flatMap { num =>
      if(num > 0L) Good(num) else Bad(One("The input value is not positive"))
    }

  def commaSeparatedStrings(input: String): Seq[String] Or One[ErrorMessage] =
    nonEmptyString(input) flatMap { nes =>
      Good(nes.split(", *").toSeq)
    }
}

trait BusinessScalacticValidations {
  def noMinor(age: Int): Validation[ErrorMessage] =
    if (age < 18) Fail("Person is a minor") else Pass

  def mustHaveScala(languages: Seq[String]): Validation[ErrorMessage] =
    if(languages.find(_ == "scala").isDefined) Pass else Fail("Languages did not contain Scala")

  def notTooOld(scalaDeveloper: ScalaDeveloper): Validation[ErrorMessage] =
    if(scalaDeveloper.age < 60) Pass else Fail("Dude, you are too old.")
}
