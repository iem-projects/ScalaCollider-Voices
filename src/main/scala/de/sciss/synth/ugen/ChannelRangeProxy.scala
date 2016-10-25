/*
 *  ChannelRangeProxy.scala
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

package de.sciss.synth.ugen

import de.sciss.synth.{GE, UGenInLike}

/** Maps a range of output indices to channel proxies.
  * Thus, `ChannelRangeProxy(x, a, b)` is the same as
  * `(a until b).map(ChannelProxy(x, _)): GE`.
  *
  * @see  [[RepeatChannels]]
  */
final case class ChannelRangeProxy(elem: GE, from: Int, until: Int) extends GE.Lazy {
  def rate  = elem.rate

  def range: Range = from until until

  override def toString = s"$elem.\\($from until $until)"

  def makeUGens: UGenInLike =
    if (until <= from) UGenInGroup.empty
    else {
      val _elem = elem.expand
      UGenInGroup(range.map(_elem.unwrap))
    }
}