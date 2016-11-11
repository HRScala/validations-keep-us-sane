package hrscala.validation

import scalax.file.Path
import scala.util.Try

object CopyPaster extends App {
  val copyCount = Try {
    args(0).toInt
  } getOrElse {
    println("Using default 100 for copy/pasting")
    100
  }

  val root = {
    val classLocation = getClass.getProtectionDomain.getCodeSource.getLocation.getPath
    val isWindoze = sys.props("file.separator") == "\\"
    val path = if (isWindoze) classLocation.tail else classLocation
    Path(path.replaceFirst("/copypaster.*", ""), '/')
  }

  val validators = Map(
    "scalaz" -> "ScalazValidation.scala",
    "cats" -> "CatsValidation.scala",
    "scalactic" -> "ScalacticValidation.scala",
    "plain-scala" -> "PlainScalaValidaton.scala"
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
