package hrscala.validation

import scalax.file.Path

object CopyPaster extends App {
  val copyCount = args(0).toInt

  val root = {
    val classLocation = getClass.getProtectionDomain.getCodeSource.getLocation.getPath
    val isWindoze = sys.props("file.separator") == "\\"
    val path = if (isWindoze) classLocation.tail else classLocation
    Path(path.replaceFirst("/copypaster.*", ""), '/')
  }

  val validators = Map(
    "scalaz" -> "ScalazValidation.scala",
    "cats" -> "CatsValidation.scala",
    "scalactic" -> "ScalacticValidation.scala"
  )

  for ((validator, filename) <- validators) {
    // cleanup
    (root / "src").deleteRecursively(force = true, continueOnFailure = false)

    val body = (root / validator / filename).string
    for (i <- 1 until copyCount) {
      val bodyWithInjectedPackage = body.replaceFirst("(package hrscala.validation)", s"$$1\npackage copypasted$i")
      val target = root / validator / "src" / "main" / "scala" / "hrscala" / "validation" / s"copypasted$i" / filename
      target write bodyWithInjectedPackage
    }
  }
}

