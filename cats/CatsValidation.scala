import Models.{Row, ScalaDeveloper}

object CatsValidation
    extends App
    with CommonCatsValidations
    with BusinessCatsValidations {

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

trait CommonCatsValidations {
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

trait BusinessCatsValidations {
  def noMinor(age: Int): ValidationNel[String, Int] =
    if (age < 18) "Person is a minor".failureNel else age.successNel

  def mustHaveScala(languages: Seq[String]): ValidationNel[String, Seq[String]] =
    languages.find(_ == "scala") match {
      case _: Some[String] => languages.successNel
      case _ => "Languages did not contain Scala".failureNel
    }
}


/*
import cats.Apply
import cats.data.ValidatedNel
import cats.data.Validated.{Invalid, Valid}
import cats.data.Validated.{invalidNel, valid}

import scala.util.Try

object CatsValidated extends App with CommonCatsValidations {
  MigrationInput.clientSheet
    .map(constructKiller)
    .foreach(println)

  def constructKiller(row: Excel.Row): ValidatedNel[String, Instafin.Killer] = {
    Apply[ValidatedNel[String, ?]].map3(
      nonEmptyString(row.cells(0).value),
      number(row.cells(1).value),
      optionalPositiveNumber(row.cells(2).value)
    )(Instafin.Killer(_, _, _))
  }
}

trait CommonCatsValidations {

  def nonEmptyString(input: String): ValidatedNel[String, String] = {
    if(input != null && input.nonEmpty) valid(input) else invalidNel("Input string is empty")
  }

  def number(input: String): ValidatedNel[String, Long] = {
    Try(valid(input.toLong)).getOrElse(invalidNel(s"invalid number format for input [$input]"))
  }

  def positiveNumber(value: Long): ValidatedNel[String, Long] = {
    if(value > 0) valid(value) else invalidNel("the input value is not positive")
  }

  def optionalPositiveNumber(input: String): ValidatedNel[String, Option[Long]] = {
    if(input == null || input.isEmpty) valid(None) else {
      number(input).andThen(positiveNumber).map(Some(_))
    }
  }
}


*/