package at.iem.scalacollider

import de.sciss.synth.{SynthDef, ugen}

object RepeatChannelsTest extends App {
  val df1 = SynthDef("test-1") {
    import ugen._
    val in      = In.kr(0, 2)
    val state   = DC.kr(Seq(3, 4, 5))
    val all     = in sig_== state
    Out.kr(0, all)
  }

  val df2 = SynthDef("test-2") {
    import ugen._
    val in      = In.kr(0, 2)
    val state   = DC.kr(Seq(3, 4, 5))
    val num     = 4
    val inComb  = RepeatChannels(in, num)
    val all     = inComb sig_== state
    Out.kr(0, all)
  }

  val config = ScalaColliderDOT.Config()
  config.input = df1.graph
  val dot1 = ScalaColliderDOT(config)
  println("\n---- GRAPH 1 ----\n")
  println(dot1)
  config.input = df2.graph
  println("\n---- GRAPH 2 ----\n")
  println(
    """You should add this to the graph:
      |
      |ranksep = 1.0;
      |{rank = same; ugen0->ugen1->ugen4->ugen7 [color=white]; }
      |""")
  val dot2 = ScalaColliderDOT(config)
  println(dot2)
}
