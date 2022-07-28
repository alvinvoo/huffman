import scala.math.*, scala.collection.mutable.*

type Node = NodeTree | String
class NodeTree(l: Node, r: Node):
  def children() = (l, r) 
  override def toString() =
    s"${l}_${r}"
end NodeTree

def huffmanCode(node: Node, binString: String =""): Map[String, String] =
  node match 
    case _: String => Map(node.asInstanceOf[String] -> binString)
    case _ => 
      val (l, r) = node.asInstanceOf[NodeTree].children()
      huffmanCode(l, binString+"0") ++ huffmanCode(r, binString+"1")

def huffmanEncode(f: List[(String, Int)]): (Map[String, String], Node) =
  var lb: List[(Node, Int)] = f 
  // first create the tree
  while lb.length > 1 do
    lb = lb.sortWith((a,b) => a._2 < b._2)
    val List(l, r) = lb.take(2)
    lb = lb.drop(2) :+ (NodeTree(l._1, r._1), l._2 + r._2)
  // traverse the tree the get the code for each node leaf
  val nodeTree = lb(0)._1
  (huffmanCode(nodeTree), nodeTree)

def convertFreq(data: String): List[(String, Int)] =
  StringBuilder(data).groupMapReduce(_.toString)(_ => 1)(_ + _).toList

def encodeOutput(data: String, huffmanCodes: Map[String, String]): String =
  val sb = StringBuilder()
  for d <- data do
    sb.append(huffmanCodes(d.toString))
  sb.toString

/** 
 *  Need to traverse the tree until it reaches a String (Leaf Node)
 *  Then start again from the root
**/
def decodeInput(code: String, node: Node): String = 
  if code == "" then ""
  else 
    val (dC, remainingCode) = traverseOne(code, node)
    dC ++ decodeInput(remainingCode, node)

def traverseOne(code: String, node: Node): (String, String) = 
  if node.isInstanceOf[String] || code == "" then (node.asInstanceOf[String], code)
  else
    code.head match
     case '0' => traverseOne(code.tail, node.asInstanceOf[NodeTree].children()._1) 
     case '1' => traverseOne(code.tail, node.asInstanceOf[NodeTree].children()._2) 

@main def huffman(): Unit =
  val data = "AABBC:CA!!DBEXx@#BYEFF"
  val cf = convertFreq(data)
  println(s"raw data is ${data}")
  println(s"converted data is ${cf}")
  val (hE, node) = huffmanEncode(cf)
  println(s"huffman codes ${hE}")
  val eO = encodeOutput(data, hE)
  println(s"encoded output ${eO}")
  val dI = decodeInput(eO, node)
  println(s"decoded input ${dI}")

