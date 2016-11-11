package hrscala.validation

import scalax.file._

object CopyPaster extends App {
  val validators = Map(
    "scalaz" -> "ScalazValidation.scala",
    "cats" -> "CatsValidation.scala"
  )

  for ((validator, filename) <- validators) {
    val root = Path("""d:/Code/validations-keep-us-sane/""" + validator, '/')

    val source = root / filename
    val body = source.string

    (root / "src").deleteRecursively(force = true, continueOnFailure = false)

    for (i <- 1 to 1500) {
      val bodyWithInjectedPackage = body.replaceFirst("(package hrscala.validation)", s"$$1\npackage copypasted$i")
      val target = root / "src" / "main" / "scala" / "hrscala" / "validation" / s"copypasted$i" / source.name
      target write bodyWithInjectedPackage
    }
  }
}