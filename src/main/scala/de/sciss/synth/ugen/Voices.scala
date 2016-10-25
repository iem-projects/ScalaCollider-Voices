/*
 *  Voices.scala
 *  (ScalaCollider-Voices)
 *
 *  Copyright (c) 2016 Institute of Electronic Music and Acoustics, Graz.
 *  Written by Hanns Holger Rutz.
 *
 *	This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 */

package de.sciss.synth
package ugen

trait VoicesExample {
  val freqIn  : GE = Seq(123, 345, 0)
  val ampIn   : GE = Seq(0.2, 0.3, 0.0)
  val maxDf   : GE = 50.0
  val lagTime : GE = 1.0

  val vcs     = Voices.T2(4)
  val an      = vcs.assign(freqIn, ampIn)
      .sustain(freqIn)(_.absdif(vcs.in1) < maxDf)

  val env     = Env.asr()
  val eg      = EnvGen.ar(env, gate = an.active)
  val active  = A2K.kr(eg) sig_!= 0
  an.close(active)
  val lag     = an.activated * lagTime
  val freq    = Lag.ar(an.out1, lag)
  val osc     = SinOsc.ar(freq) * an.out2 * eg
  Out.ar(0, osc)
}

/** Building blocks for polyphony (voice) management.
  *
  * Example (OLD):
  *
  * {{{
  *   val vcs     = Voices.T2(4)
  *   val an      = vcs.analyze(freqIn, ampIn)((vcs.in1 absdif freqIn) < maxDf)
  *   val env     = Env.asr(attack = egAtk, release = egRls)
  *   val eg      = EnvGen.ar(env, gate = an.active)
  *   val active  = A2K.kr(eg) sig_!= 0
  *   an.close(active)
  *   val lag     = an.activated * lagTime
  *   val freq    = Lag.ar(an.out1, lag)
  *   val osc     = SinOsc.ar(freq) * an.out2 * eg
  *   Out.ar(0, osc)
  *
  * }}}
  *
  * Example:
  *
  * {{{
  *   val vcs     = Voices.T2(4)
  *   val an      = vcs.assign(freqIn, ampIn, valid = ampIn > 0)
  *                   .sustain(freqIn)(_.absdif(vcs.in1) < maxDf)
  *   val env     = Env.asr(attack = egAtk, release = egRls)
  *   val eg      = EnvGen.ar(env, gate = an.active)
  *   val active  = A2K.kr(eg) sig_!= 0
  *   an.close(active)
  *   val lag     = an.activated * lagTime
  *   val freq    = Lag.ar(an.out1, lag)
  *   val osc     = SinOsc.ar(freq) * an.out2 * eg
  *   Out.ar(0, osc)
  *
  * }}}
  */
object Voices {
  final case class T1(num: Int) extends Voices {
    def features = 1

    def in: GE = featureIn(0)

    def assign(in: GE, valid: GE = 1): A1 = A1(this, in = in, valid = valid, sustainCond = 0)
  }

  final case class T2(num: Int) extends Voices {
    def features = 2

    def in1: GE = featureIn(0)
    def in2: GE = featureIn(1)

    def assign(in1: GE, in2: GE, valid: GE = 1): A2 = A2(this, in1 = in1, in2 = in2, valid = valid, sustainCond = 0)
  }

  final case class T3(num: Int) extends Voices {
    def features = 3

    def in1: GE = featureIn(0)
    def in2: GE = featureIn(1)
    def in3: GE = featureIn(2)

    def assign(in1: GE, in2: GE, in3: GE, valid: GE = 1): A3 = 
      A3(this, in1 = in1, in2 = in2, in3 = in3, valid = valid, sustainCond = 0)
  }

  final case class T4(num: Int) extends Voices {
    def features = 4

    def in1: GE = featureIn(0)
    def in2: GE = featureIn(1)
    def in3: GE = featureIn(2)
    def in4: GE = featureIn(3)

    def assign(in1: GE, in2: GE, in3: GE, in4: GE, valid: GE = 1): A4 =
      A4(this, in1 = in1, in2 = in2, in3 = in3, in4 = in4, valid = valid, sustainCond = 0)
  }

  final case class A1(voices: T1, in: GE, valid: GE, sustainCond: GE)
    extends Analysis {

    def out: GE = featureOut(0)

    type Repr = A1

    protected def replaceSustain(newSustain: GE): Repr = copy(sustainCond = newSustain)

    protected def inputs: List[GE] = in :: Nil
  }

  final case class A2(voices: T2, in1: GE, in2: GE, valid: GE, sustainCond: GE)
    extends Analysis {

    def out1: GE = featureOut(0)
    def out2: GE = featureOut(1)

    type Repr = A2

    protected def replaceSustain(newSustain: GE): Repr = copy(sustainCond = newSustain)

    protected def inputs: List[GE] = in1 :: in2 :: Nil
  }

  final case class A3(voices: T3, in1: GE, in2: GE, in3: GE, valid: GE, sustainCond: GE)
    extends Analysis {

    def out1: GE = featureOut(0)
    def out2: GE = featureOut(1)
    def out3: GE = featureOut(2)

    type Repr = A3

    protected def replaceSustain(newSustain: GE): Repr = copy(sustainCond = newSustain)

    protected def inputs: List[GE] = in1 :: in2 :: in3 :: Nil
  }

  final case class A4(voices: T4, in1: GE, in2: GE, in3: GE, in4: GE, valid: GE, sustainCond: GE)
    extends Analysis {

    def out1: GE = featureOut(0)
    def out2: GE = featureOut(1)
    def out3: GE = featureOut(2)
    def out4: GE = featureOut(2)

    type Repr = A4

    protected def replaceSustain(newSustain: GE): Repr = copy(sustainCond = newSustain)

    protected def inputs: List[GE] = in1 :: in2 :: in3 :: in4 :: Nil
  }

  trait Analysis extends GE with ControlRated {
    override def productPrefix: String = s"Voices$$A${voices.features}"

    // ---- abstract ----

    type Repr <: Analysis

    val voices      : Voices
    def valid       : GE
    def sustainCond : GE

    protected def replaceSustain(newSustain: GE): Repr

    protected def inputs: List[GE]

    // ---- impl ----

    def active    : GE = featureOut(voices.features)
    def activated : GE = active & !voices.active
    def sustained : GE = active &  voices.active

    final def featureOut(idx: Int): GE =
      ChannelRangeProxy(this, from = idx * voices.num, until = (idx + 1) * voices.num)

    final def close(active: GE = 0): Unit = Out(this, active = active)

    final def sustain(selector: GE)(pred: GE => GE): Repr = {
      val newSustain = pred(RepeatChannels(selector, voices.num))
      replaceSustain(newSustain)
    }

    private[synth] final def expand: UGenInLike = {
      import voices.num
      val _inputs       = inputs
      val inputsExp     = _inputs.map(_.expand.flatOutputs)
      val numInChans    = inputsExp.iterator.map(_.size).max
      var state         = voices.state
      val voiceNos      = 0 until num: GE
      val activeOld     = voices.active
      var activeNew     = Vector.fill(num)(0: GE): GE // this will become `active`
      val bothValid     = activeOld & valid

      val notFound_ = for (ch <- 0 until numInChans) yield {
        val inputsCh    = _inputs.map(_ \ ch): GE
        // remember: sustainCond = [[ch0] * num, [ch1] * num, ... [chN] * num]
        val isSustain   = ChannelRangeProxy(sustainCond, from = ch * num, until = (ch + 1) * num)
        val voiceAvail  = !activeNew
        val bestIn      = Flatten(Seq[GE](0, isSustain * (bothValid & voiceAvail)))
        val best        = ArrayMax.kr(bestIn)
        val bestIdx     = best.index - 1
        val bestMask    = voiceNos sig_== bestIdx
        activeNew      |= bestMask
        val bestMaskN   = !bestMask
        state           = state * bestMaskN + inputsCh * bestMask

        bestIdx sig_== -1
      }
      val notFound: GE  = notFound_
      val startChan     = notFound & valid

      for (ch <- 0 until numInChans) {
        val inputsCh    = _inputs.map(_ \ ch): GE
        val voiceAvail  = !(activeNew | activeOld)
        val freeIn      = Flatten(Seq[GE](0, startChan & voiceAvail))
        val free        = ArrayMax.kr(freeIn)
        val freeIdx     = free.index - 1
        val freeMask    = voiceNos sig_== freeIdx
        activeNew      |= freeMask
        val freeMaskN   = !freeMask
        state           = state * freeMaskN + inputsCh * freeMask
      }

      Flatten(Seq(state, activeNew))
    }
  }

  final case class Out(analysis: Analysis, active: GE = 0) extends Lazy.Expander[Unit] {
    override def productPrefix: String = "Voices$" + "Out"

    protected def makeUGens: Unit = {
      val newActive   = analysis.activated | active
      import analysis.voices.{features, num}
      val newFeatures = ChannelRangeProxy(analysis, from = 0, until = features * num)
      val combined    = Flatten(Seq(newFeatures, newActive))
      LocalOut.kr(combined)
    }
  }
}
trait Voices extends GE with ControlRated {
  override def productPrefix: String = s"Voices$$T$features"

  /** Number of voices allocated. */
  def num: Int

  /** Number of features (graph element components) per voice. */
  def features: Int

  // final def featureIterator: Iterator[GE] = Iterator.range(0, features).map(featureIn)

  /** All (user) state except `active`. */
  def state: GE = ChannelRangeProxy(this, from = 0, until = features * num)

  /*
      organization of the channels - features are joined together (this is an arbitrary decision):

      [vc0_feat0, vc1_feat0, ... vcN_feat0, vc0_feat1, vc1_feat1, ... vcN_feat1,
       ..., vc0_featM, vc1_featM, ..., vcN_featM]

      thus

         def get(vcIdx: Int, featIdx: Int) = ChannelProxy(this, featIdx * num + vcIdx)
         def get(featIdx: Int) = ChannelRangeProxy(this, from = featIdx * num, until = featIdx * num + num)

      Apart from the nominal number of features, `features`, there is an additional
      "feature" which is the on-off state of the voice. Thus we use
      `(num + 1) * features` channels for the local-in/out, and we can
      use `featureIn(features)` to obtain the on-off vector.

   */

  final def featureIn(idx: Int): GE =
    ChannelRangeProxy(this, from = idx * num, until = (idx + 1) * num)

  def active: GE = featureIn(features)

  private[synth] def expand: UGenInLike =
    LocalIn.kr(Seq.fill[Constant]((num + 1) * features)(0))
}