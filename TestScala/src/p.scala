import scala.io.Source

class p


object p {

  def widthOfLength(s: String) = s.length.toString.length
   def main(args: Array[String]) {
	  if (args.length > 0) {
		  val lines = Source.fromFile(args(0)).getLines.toList
				  val longestLine = lines.reduceLeft(
						  (a, b) => if (a.length > b.length) a else b
						  )
						  val maxWidth = widthOfLength(longestLine)
						  for (line <- lines) {
							  val numSpaces = maxWidth-widthOfLength(line)
							  val padding = " " * numSpaces
							  println(padding + line.length +" | "+ line)
						  }
	  }
	  else
		  Console.err.println("Please enter filename")
  	}

  
}