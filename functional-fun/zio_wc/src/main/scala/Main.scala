import zio.App
import zio.console._
import java.nio.file.Path
import zio.IO
import scala.collection.JavaConverters._
import java.io.IOException
import java.nio.file.Paths
import java.nio.file.Files

object MyApp extends App {

  case class Counters(chars: Long = 0, words: Long = 0, lines: Long = 0) {
    def +(b: Counters) = Counters(chars + b.chars, words + b.words, lines + b.lines)
  }
  object Counters {
    val zero = Counters()
  }

  def run(args: List[String]) =
    myAppLogic.fold(_ => 1, _ => 0)

  type Error = String

  def list(location: Path): IO[Error, List[Path]] = {
    IO {
      Files.newDirectoryStream(location).asScala.map(_.toAbsolutePath()).toList
    }.catchAll {
      case x: IOException => IO.fail(x.getMessage())
      case x => IO.fail(s"Unexpected error: $x")
    }
  }

  def listAndExpand(needToList: Set[Path], seen: Set[Path] = Set.empty): IO[Error, Set[Path]] = {
    for {
      content <- IO.sequence(needToList.map(list))
      nowSeen = seen ++ needToList ++ content.flatten.toSet
      newDirs = content.flatten.toSet.filter(_.toFile().isDirectory()) -- seen -- needToList
      result <- if (newDirs.isEmpty)
        IO.succeed(nowSeen)
      else
        listAndExpand(newDirs, nowSeen)
    } yield result
  }

  def count(file: Path): IO[Error, Counters] = {
    IO.succeed(Counters(chars = file.toFile().length())) // .length() doesn't throw
  }

  val myAppLogic =
    for {
      result <- listAndExpand(Set(Paths.get("").toAbsolutePath()))
      counts <- IO.sequence(result.map(count))
      count = counts.foldLeft(Counters.zero)(_ + _)
      _    <- putStrLn(s"Bytes: ${count.chars}")
    } yield ()
}
