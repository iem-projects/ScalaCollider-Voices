/*
 *  RepeatChannels.scala
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

/** An auxiliary graph element that repeats
  * the channels of an input signal, allowing
  * for example for an exhaustive element-wise
  * combination with another signal.
  *
  * Normally, the way multi-channel expansion
  * works is that when two signals are combined,
  * the output signal has a number of channels
  * that is the ''maximum'' of the individual number
  * of channels, and channels will be automatically
  * wrapped around.
  *
  * For example, in `x * y` if `x` has three and
  * `y` has five channels, the result expands to
  *
  * {{{
  * Seq[GE](
  *   x\0 * y\0, x\1 * y\1, x\2 * y\2, x\0 * y\3, x\1 * y\4
  * }}}
  *
  * Using this element, we can enforce the appearance
  * of all combinations of channels, resulting in a signal
  * whose number of channels is the ''sum'' of the individual
  * number of channels.
  *
  * For example, `RepeatChannels(x, 5)` expands to
  *
  * {{{
  * Seq[GE](
  *   x \ 0, x \ 0, x \ 0, x \ 0, x \ 0,
  *   x \ 1, x \ 1, x \ 1, x \ 1, x \ 1,
  *   x \ 2, x \ 2, x \ 2, x \ 2, x \ 2
  * )
  * }}}
  * 
  * And `RepeatChannels(x, 5) * y` accordingly expands to
  * the fifteen-channels signal
  * 
  * {{{
  * Seq[GE](
  *   (x\0) * (y\0), (x\0) * (y\1), (x\0) * (y\2), (x\0) * (y\3), (x\0) * (y\4),
  *   (x\1) * (y\0), (x\1) * (y\1), (x\1) * (y\2), (x\1) * (y\3), (x\1) * (y\4),
  *   (x\2) * (y\0), (x\2) * (y\1), (x\2) * (y\2), (x\2) * (y\3), (x\2) * (y\4)
  * )
  * }}}
  *
  * @see  [[ChannelRangeProxy]]
  */
final case class RepeatChannels(a: GE, num: Int) extends GE.Lazy {
  def rate: MaybeRate = a.rate

  protected def makeUGens: UGenInLike = {
    val out = a.expand.outputs
    val seq = Seq.fill(num)(out).transpose.flatten
    seq: GE
  }
}
