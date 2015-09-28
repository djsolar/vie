package test


object start  {
  
  
	class Fraction(n:Int,d:Int){
	  n/d
	}
	
	object Fraction{
	  def apply(n:Int,d:Int) = new Fraction(n,d)
	 
	}

	def startnum(){
		val num = readInt
				if(num > 0)print(1)else if(num<0)print(-1)else print(0)
	}
	
	
	
	def main(args: Array[String]) {
//		start.startnum
	  import sys.process._
	  "ls -al"!
//	  val Fraction(a,b) = Fraction(3,4) * Fraction(4,6)
//	  val r:Int = Fraction(3,4)
//	  println(r)
	  
	 val s = Array(11.0,33.4,55.4).map((x:Double) => 3*x)

//	 import helper._
	 
	 for(i<- 0 until s.length){
	   println(s(i))
	 }
	}
	
	
	
}

//object actor {
//	var sum = 0
//		loop {
//		receive {
//			case Data(bytes)
//				=> sum += hash(bytes)
//			case GetSum(requester) => requester ! sum
//		}
//	}
//}
//	def printlna(arr : Array[String]
//			= {arr.foreach(println)}
class view(x:Int, y:Short)
