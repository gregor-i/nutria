/*
 * Copyright (C) 2016  Gregor Ihmor & Merlin Göttlinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//package nutria.accumulator
//
//import org.specs2._
//
//class DoubleAccumulator extends Specification with ScalaCheck {
//  def is =
//    s2"""
//  Accumulators are defined over a Monoid. So folding with the neutral element is the identity function:
//    Arithmetic: ${prop { (d: Double) => Arithmetic.fold(Arithmetic.neutral, d) === d }}
//                ${prop { (d: Double) => Arithmetic.fold(d, Arithmetic.neutral) === d }}
//    Geometric:  ${prop { (d: Double) => Geometric.fold(Geometric.neutral, d) === d }}
//                ${prop { (d: Double) => Geometric.fold(d, Geometric.neutral) === d }}
//    Harmonic:   ${prop { (d: Double) => Harmonic.fold(Harmonic.neutral, d) === d }.pendingUntilFixed("something wrong")}
//    Harmonic:   ${prop { (d: Double) => Harmonic.fold(d, Harmonic.neutral) === d }.pendingUntilFixed("something wrong")}
//    Max:        ${prop { (d: Double) => Max.fold(Max.neutral, d) === d }}
//                ${prop { (d: Double) => Max.fold(d, Max.neutral) === d }}
//    Min:        ${prop { (d: Double) => Min.fold(Min.neutral, d) === d }}
//                ${prop { (d: Double) => Min.fold(d, Min.neutral) === d }}
//
//    Order: ${order}
//
//
//    Laws: Arithmetic ${AccumulatorLaws(Arithmetic).is}
//    Laws: Geometric ${AccumulatorLaws(Geometric).is}
//    Laws: Harmonic ${AccumulatorLaws(Harmonic).is}
//    Laws: Max ${AccumulatorLaws(Max).is}
//    Laws: Min ${AccumulatorLaws(Min).is}
//    Laws: Variance ${AccumulatorLaws(Variance).is}
//"""
//
//
//  def order =  prop{ (ds: Seq[Double]) => ds.nonEmpty ==>
//    //(Min(ds) <= Harmonic(ds)
//    ( Harmonic(ds) <= Geometric(ds)
//      && Geometric(ds) <= Arithmetic(ds)
//      //&& Arithmetic(ds) <= Max(ds))}
//      )}
//}
//
//
