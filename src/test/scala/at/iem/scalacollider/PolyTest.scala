package at.iem.scalacollider

import de.sciss.synth.Server
import de.sciss.synth.Ops._
import de.sciss.synth.ugen._

import scala.Predef.{any2stringadd => _, _}

object PolyTest extends App {
  Server.run { s =>
    println(s"${new java.util.Date} - before play")
    play {
      def mkWalk() = Gate.kr(Ramp.kr(BrownNoise.kr, 0.1).linexp(-1, 1, 300, 3000), ToggleFF.kr(Dust.kr(1)))

      val freq    = Seq.fill(3)(mkWalk())
      val vcs     = Voices.T1(4)
      val an      = vcs.assign(in = freq).sustain(freq)(_.absdif(vcs.in) < 50)
      val env     = Env.asr(attack = 0.1, release = 1.0)
      val eg      = EnvGen.ar(env, gate = an.active)
      val active  = A2K.kr(eg) sig_!= 0
      an.close(active)

      val sig     = SplayAz.ar(numChannels = 2, in = Resonz.ar(LFSaw.ar(an.out), an.out, rq = eg + 0.1) * 0.2)
      Out.ar(0, sig)
    }
    println(s"${new java.util.Date} - after  play")

    s.startAliveThread()
    Thread.sleep(2000)
    println(s.counts)
  }
}
