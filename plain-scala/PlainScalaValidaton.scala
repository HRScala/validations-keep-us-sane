package hrscala.validation

import Models.{Row, ScalaDeveloper}
import scala.util.Try

object PlainScalaValidation
  extends App
    with CommonScalacticValidations
    with BusinessScalacticValidations
    with EitherCombinator {

  def constructScalaDeveloper(row: Row): Either[List[String], ScalaDeveloper] = {

    val name      = nonEmptyString(row.cells(0))
    val age       = positiveNumber(row.cells(1)).fold(l => Left(l), r => noMinor(r))
    val languages = commaSeparatedStrings(row.cells(2)).fold(l => Left(l), r => mustHaveScala(r))

    withGoodForThree(name, age, languages) {
      ScalaDeveloper(_, _, _)
    }
  }

  Models.people foreach { row =>
    println(constructScalaDeveloper(row))
  }

}


trait CommonScalacticValidations {

  def nonEmptyString(input: String): Either[List[String], String] =
    if (input != null && input.nonEmpty) Right(input) else Left(List("Input string is empty"))

  def number(input: String): Either[List[String], Int] =
    nonEmptyString(input).fold(l => Left(l), { nes =>
      Try(Right(input.toInt)).getOrElse(Left(List(s"Invalid number format for input: $input")))
    })

  def positiveNumber(input: String): Either[List[String], Int] =
    number(input).fold(l => Left(l), { num =>
      if(num > 0L) Right(num) else Left(List("The input value is not positive"))
    })

  def commaSeparatedStrings(input: String): Either[List[String], Seq[String]] =
    nonEmptyString(input).fold(l => Left(l), { nes =>
      Right(nes.split(", *").toSeq)
    })

}

trait BusinessScalacticValidations {

  def noMinor(age: Int): Either[List[String], Int] =
    if (age < 18) Left(List("Person is a minor")) else Right(age)

  def mustHaveScala(languages: Seq[String]): Either[List[String], Seq[String]] =
    if(languages.find(_ == "scala").isDefined) Right(languages) else Left(List("Languages did not contain Scala"))
}


trait EitherCombinator {
  def withGoodForThree[T1, T2, T3, R](v1: Either[List[String], T1], v2: Either[List[String], T2], v3: Either[List[String], T3])(builder: (T1, T2, T3) => R): Either[List[String], R] = {
    val parts = v1 :: v2 :: v3 :: Nil
    if(parts.forall(_.isRight)) {
      for {
        a <- v1.right
        b <- v2.right
        c <- v3.right
      } yield builder(a, b, c)

    } else {
      Left(parts.filter(_.isLeft).map(_.left.get).flatten)
    }
  }
}
