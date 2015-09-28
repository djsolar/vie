package test

import scala.collection.mutable.ArrayBuffer


object kan {
  
	def main(args: Array[String]) {
//		var a : Int = 11
//				11 + a
//		print("what?")
//		var num = readInt
//		println("a+num="+ (a + num))
////		for(i <- 1 until num) println(i)
//		for(i <- 1 to 10; j <- 1 to 11 if i!=j)print(i*10 +j)
//		var ii = for(i <- 0 to 10) yield i%3 
//		for(i <- ii)println(i)
		
		println(lookbookname("linux鸟哥私房菜"))
		println(lookbookname(left="[", right="]",context="温晓辰"))
		println(recursiveSum(1 to 5 : _*))
		
		var arr = new Array[String](10)
		arr(0) = "keyi"
		print(arr(0))
		val testarr = new ArrayBuffer[String]()
		testarr += ("s","f")
		for(i <- 0 until testarr.length)print(testarr(i))
		
	}
  

	def aa(x:Int)=if(x>0) 2 else 3
	def lookbookname(context:String,left:String = "<<",right:String=">>"):String=left + context + right
	def recursiveSum(args: Int*): Int = {
			if (args.length == 0) 0
			else args.head + recursiveSum(args.tail: _*)
}

}
